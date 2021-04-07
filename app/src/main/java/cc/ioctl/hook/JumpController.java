/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.hook;

import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.lifecycle.ActProxyMgr;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class JumpController extends CommonDelayableHook {

    public static final String DEFAULT_RULES = "A,P:me.singleneuron.locknotification;\n" +
        "A,P:cn.nexus6p.QQMusicNotify;\n" +
        "A,A:android.media.action.VIDEO_CAPTURE;\n" +
        "A,P:nil.nadph.qnotified;\n";
    public static final int JMP_DEFAULT = 0;
    public static final int JMP_ALLOW = 1;
    public static final int JMP_REJECT = 2;
    public static final int JMP_QUERY = 3;
    public static final JumpController INSTANCE = new JumpController();
    private static final String qn_jmp_ctl_rules = "qn_jmp_ctl_rules";
    Method JefsClass_runV = null;
    Method Interceptor_checkAndDo = null;
    private ArrayList<Rule> rules = null;

    protected JumpController() {
        super("qn_jmp_ctl_enable", SyncUtils.PROC_MAIN, true);
    }

    public static ArrayList<Rule> parseRules(String rules) throws ParseException {
        int idx = 0;
        ArrayList<Rule> result = new ArrayList<>();
        while (idx < rules.length()) {
            String expr;
            int delta = rules.indexOf(';', idx);
            if (delta == -1) {
                break;
            }
            expr = rules.substring(idx, delta);
            if (expr.length() < 3) {
                throw new ParseException("incorrect rule format(too short)", idx);
            }
            if (expr.charAt(1) != ',') {
                throw new ParseException("expected ',', got " + expr.charAt(1), idx + 1);
            }
            Rule r = new Rule();
            String verb = expr.substring(0, 1).toUpperCase();
            switch (verb.charAt(0)) {
                case 'A':
                    r.verb = JMP_ALLOW;
                    break;
                case 'R':
                    r.verb = JMP_REJECT;
                    break;
                case 'Q':
                    r.verb = JMP_QUERY;
                    break;
                default:
                    throw new ParseException("unexpected verb " + expr.charAt(0), idx);
            }
            String[] conditions = expr.substring(2).split(",");
            for (String condition : conditions) {
                if (condition.length() < 3) {
                    throw new ParseException("condition too short: " + condition, idx);
                }
                if (condition.charAt(1) != ':') {
                    throw new ParseException("expected ':', got " + condition.charAt(1), idx);
                }
                String type = condition.substring(0, 1).toUpperCase();
                switch (type.charAt(0)) {
                    case 'P': {
                        r.pkg = condition.substring(2);
                        break;
                    }
                    case 'C': {
                        r.cmp = condition.substring(2);
                        break;
                    }
                    case 'A': {
                        r.action = condition.substring(2);
                        break;
                    }
                    default:
                        throw new ParseException("unexpected condition type " + type.charAt(0),
                            idx);
                }
            }
            result.add(r);
            int lf = rules.indexOf('\n', delta);
            if (lf == -1) {
                break;
            }
            idx = lf + 1;
        }
        return result;
    }

    static boolean cmpWildcard(String exp, String str) {
        if (exp == null || str == null) {
            return false;
        }
        if (exp.equals(str)) {
            return true;
        }
        if (!exp.contains("*")) {
            return false;
        }
        String regex = exp.replace(".", "\\.").replace("**", ".+").replace("*", "[^.]+");
        Pattern p = Pattern.compile(regex);
        return p.matcher(str).matches();
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> JefsClass = Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass");
            if (JefsClass == null) {
                return false;
            }
            Method JefsClass_intercept = null;
            for (Method m : JefsClass.getDeclaredMethods()) {
                if (m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 4) {
                        if (argt[0] != Context.class || argt[1] != Intent.class || !argt[3]
                            .isInterface()) {
                            continue;
                        }
                        Interceptor_checkAndDo = argt[3].getMethods()[0];
                        JefsClass_intercept = m;
                    } else if (argt.length == 1) {
                        if (argt[0] != Runnable.class) {
                            continue;
                        }
                        JefsClass_runV = m;
                        JefsClass_runV.setAccessible(true);
                    }
                }
            }
            XposedBridge.hookMethod(JefsClass_intercept, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        final Object that = param.thisObject;
                        final Context ctx = (Context) param.args[0];
                        final Intent intent = (Intent) param.args[1];
                        final Runnable runnable = (Runnable) param.args[2];
                        Object interceptor = param.args[3];
                        if (ctx == null || intent == null || runnable == null
                            || interceptor == null) {
                            return;
                        }
                        int result = checkIntent(ctx, intent);
                        ComponentName cmp = intent.getComponent();
                        if (cmp != null && ctx.getPackageName().equals(cmp.getPackageName()) &&
                            ActProxyMgr.isModuleProxyActivity(cmp.getClassName())) {
                            result = JMP_ALLOW;
                        }
                        if (result != JMP_DEFAULT) {
                            if (result == JMP_ALLOW) {
                                JefsClass_runV.invoke(that, runnable);
                                param.setResult(null);
                            } else if (result == JMP_REJECT) {
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "Reject: " + intent, Toast.LENGTH_SHORT)
                                            .show();
                                    }
                                });
                                param.setResult(null);
                            } else if (result == JMP_QUERY) {
                                if (!(ctx instanceof Activity)) {
                                    //**sigh**
                                    Utils.logi("JumpController/I JMP_QUERY but no token, ctx=" + ctx
                                        + ", intent=" + intent);
                                    Utils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ctx,
                                                "JumpController/I JMP_QUERY but no token, ctx="
                                                    + ctx + ", intent=" + intent,
                                                Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                                final String desc = intent.toString();
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomDialog.create(ctx).setTitle("跳转控制")
                                            .setMessage("即将打开 " + desc)
                                            .setPositiveButton(android.R.string.ok,
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                                        try {
                                                            JefsClass_runV.invoke(that, runnable);
                                                        } catch (Exception e) {
                                                            Toasts.info(
                                                                HostInformationProviderKt.hostInfo
                                                                    .getApplication(),
                                                                e.toString());
                                                        }
                                                    }
                                                }).setNegativeButton(android.R.string.cancel, null)
                                            .setCancelable(true).show();
                                    }
                                });
                                param.setResult(null);
                            } else {
                                final int finalResult = result;
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx,
                                            "JumpController/E: Unknown result: " + finalResult
                                                + " for " + intent, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    } catch (Throwable e) {
                        Utils.log(e);
                        throw e;
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    public int checkIntent(Context ctx, Intent intent) {
        if (intent == null) {
            return JMP_DEFAULT;
        }
        for (Rule r : getRuleList()) {
            if (r == null) {
                continue;
            }
            int v = r.applyTo(intent);
            if (v != JMP_DEFAULT) {
                return v;
            }
        }
        return JMP_DEFAULT;
    }

    @NonNull
    public String getRuleString() {
        try {
            String r = ConfigManager.getDefaultConfig().getString(qn_jmp_ctl_rules);
            if (r == null) {
                r = DEFAULT_RULES;
            }
            return r;
        } catch (Exception e) {
            log(e);
            return "";
        }
    }

    public void setRuleString(String r) {
        if (r == null) {
            return;
        }
        try {
            ConfigManager.getDefaultConfig().putString(qn_jmp_ctl_rules, r);
            ConfigManager.getDefaultConfig().save();
            reloadRuleList();
        } catch (Exception e) {
            log(e);
        }
    }

    @NonNull
    public ArrayList<Rule> getRuleList() {
        if (rules == null) {
            return reloadRuleList();
        }
        return rules;
    }

    @NonNull
    protected ArrayList<Rule> reloadRuleList() {
        String ruleStr = getRuleString();
        try {
            rules = parseRules(ruleStr);
        } catch (ParseException e) {
            rules = new ArrayList<>();
            log(e);
        }
        return rules;
    }

    public int getEffectiveRulesCount() {
        if (isEnabled() && isInited()) {
            return getRuleList().size();
        } else {
            return -1;
        }
    }

    public static class Rule {

        public int verb;
        @Nullable
        public String pkg;
        @Nullable
        public String cmp;
        @Nullable
        public String action;

        public int applyTo(Intent i) {
            if (i == null) {
                return JMP_DEFAULT;
            }
            if (pkg == null && cmp == null && action == null) {
                return JMP_DEFAULT;
            }
            boolean pass = true;
            if (cmp != null) {
                ComponentName c = i.getComponent();
                if (c == null) {
                    pass = false;
                } else {
                    String p = c.getPackageName();
                    String clz = c.getClassName();
                    String[] _tmp = cmp.split("/");
                    if (_tmp.length != 2) {
                        pass = false;
                    } else {
                        String _p = _tmp[0];
                        String _c = _tmp[1];
                        if (_c.startsWith(".")) {
                            _c = _p + _c;
                        }
                        if (!cmpWildcard(_c, clz) || !cmpWildcard(_p, p)) {
                            pass = false;
                        }
                    }
                }
            } else if (pkg != null) {
                String p = i.getPackage();
                if (p == null) {
                    ComponentName c = i.getComponent();
                    if (c != null) {
                        p = c.getPackageName();
                    }
                }
                if (p == null) {
                    pass = false;
                } else {
                    if (!cmpWildcard(pkg, p)) {
                        pass = false;
                    }
                }
            }
            if (pass && action != null) {
                if (!cmpWildcard(action, i.getAction())) {
                    pass = false;
                }
            }
            if (pass) {
                return verb;
            } else {
                return JMP_DEFAULT;
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            switch (verb) {
                case JMP_ALLOW:
                    sb.append('A');
                    break;
                case JMP_REJECT:
                    sb.append('R');
                    break;
                case JMP_QUERY:
                    sb.append('Q');
                    break;
                default:
                    sb.append(verb);
            }
            if (!TextUtils.isEmpty(cmp)) {
                sb.append(",C:");
                sb.append(cmp);
            }
            if (!TextUtils.isEmpty(pkg)) {
                sb.append(",P:");
                sb.append(pkg);
            }
            if (!TextUtils.isEmpty(action)) {
                sb.append(",A:");
                sb.append(action);
            }
            sb.append(';');
            return sb.toString();
        }
    }
}
