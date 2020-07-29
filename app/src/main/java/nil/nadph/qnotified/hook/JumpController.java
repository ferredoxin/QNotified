/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.*;

public class JumpController extends BaseDelayableHook {
    private static final String qn_jmp_ctl_enable = "qn_jmp_ctl_enable";

    private static final String DEFAULT_RULES = "A,P:me.singleneuron.locknotification;\n" +
            "A,P:cn.nexus6p.QQMusicNotify;\n";

    public static final int JMP_DEFAULT = 0;
    public static final int JMP_ALLOW = 1;
    public static final int JMP_REJECT = 2;
    public static final int JMP_QUERY = 3;

    Method JefsClass_runV = null;
    Method Interceptor_checkAndDo = null;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class<?> JefsClass = Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass");
            if (JefsClass == null) return false;
            Method JefsClass_intercept = null;
            for (Method m : JefsClass.getDeclaredMethods()) {
                if (m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 4) {
                        if (argt[0] != Context.class || argt[1] != Intent.class || !argt[3].isInterface()) continue;
                        Interceptor_checkAndDo = argt[3].getMethods()[0];
                        JefsClass_intercept = m;
                    } else if (argt.length == 1) {
                        if (argt[0] != Runnable.class) continue;
                        JefsClass_runV = m;
                        JefsClass_runV.setAccessible(true);
                    }
                }
            }
            XposedBridge.hookMethod(JefsClass_intercept, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        final Object that = param.thisObject;
                        final Context ctx = (Context) param.args[0];
                        final Intent intent = (Intent) param.args[1];
                        final Runnable runnable = (Runnable) param.args[2];
                        Object interceptor = param.args[3];
//                        Utils.logi("JumpController/I intercept: ctx=" + ctx + ", intent=" + intent + ", r=" + runnable + ", interceptor=" + interceptor);
                        if (ctx == null || intent == null || runnable == null || interceptor == null) return;
                        int result = checkIntent(ctx, intent);
                        ComponentName cmp = intent.getComponent();
                        if (cmp != null && ctx.getPackageName().equals(cmp.getPackageName()) &&
                                cmp.getClassName().startsWith("nil.nadph.qnotified.activity.")) {
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
                                        Toast.makeText(ctx, "Reject: " + intent, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                param.setResult(null);
                            } else if (result == JMP_QUERY) {
                                if (!(ctx instanceof Activity)) {
                                    //**sigh**
                                    Utils.logi("JumpController/I JMP_QUERY but no token, ctx=" + ctx + ", intent=" + intent);
                                    Utils.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(ctx, "JumpController/I JMP_QUERY but no token, ctx=" + ctx + ", intent=" + intent, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
//                                PackageManager pm = ctx.getPackageManager();
//                                List<ResolveInfo> activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//                                final String desc = (activities.size() == 1 ? ('"' + activities.get(0).loadLabel(pm).toString() + '"') : intent.toString());
                                final String desc = intent.toString();
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CustomDialog.create(ctx).setTitle("跳转控制").setMessage("即将打开 " + desc)
                                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        try {
                                                            JefsClass_runV.invoke(that, runnable);
                                                        } catch (Exception e) {
                                                            Utils.showErrorToastAnywhere(e.toString());
                                                        }
                                                    }
                                                }).setNegativeButton(android.R.string.cancel, null).setCancelable(true).show();
                                    }
                                });
                                param.setResult(null);
                            } else {
                                final int finalResult = result;
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "JumpController/E: Unknown result: " + finalResult + " for " + intent, Toast.LENGTH_SHORT).show();
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
            inited = true;
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    private boolean inited = false;

    private static final JumpController self = new JumpController();

    public static JumpController get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_jmp_ctl_enable, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrDefault(qn_jmp_ctl_enable, true);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    public int checkIntent(Context ctx, Intent intent) {
        if (intent == null) return JMP_DEFAULT;
        return JMP_DEFAULT;
    }

    @NonNull
    public String getRuleString() {
        return DEFAULT_RULES;
    }

    public int getEffectiveRulesCount() {
        if (isEnabled() && isInited()) {
            return 2;
        } else {
            return -1;
        }
    }
}
