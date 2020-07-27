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

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

public class JumpController extends BaseDelayableHook {
    public static final int JMP_DEFAULT = 0;
    public static final int JMP_ALLOW = 1;
    public static final int JMP_REJECT = 2;
    //public static final int JMP_QUERY = 3;
    // TODO: 2020/7/27 struggle with JMP_QUERY
    Method JefsClass_run = null;
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
                        JefsClass_run = m;
                        JefsClass_run.setAccessible(true);
                    }
                }
            }
            XposedBridge.hookMethod(JefsClass_intercept, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object that = param.thisObject;
                        final Context ctx = (Context) param.args[0];
                        final Intent intent = (Intent) param.args[1];
                        Runnable runnable = (Runnable) param.args[2];
                        Object interceptor = param.args[3];
                        Utils.logi("JumpController/I intercept: ctx=" + ctx + ", intent=" + intent + ", r=" + runnable + ", interceptor=" + interceptor);
                        if (ctx == null || intent == null || runnable == null || interceptor == null) return;
                        final int result = checkIntent(ctx, intent);
                        if (result != JMP_DEFAULT) {
                            if (result == JMP_ALLOW) {
                                JefsClass_run.invoke(that, runnable);
                                param.setResult(null);
                            } else if (result == JMP_REJECT) {
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "Reject: " + intent, Toast.LENGTH_SHORT).show();
                                    }
                                });
                                param.setResult(null);
                            }/* else if (result == JMP_QUERY) {
                                String pkg = intent.getPackage();
                                if (TextUtils.isEmpty(pkg)) {
                                    ComponentName cmp = intent.getComponent();
                                    if (cmp != null) pkg = cmp.getPackageName();
                                }
                                List<ResolveInfo> activities = ctx.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                                String action = intent.getAction();
                                Interceptor_checkAndDo.invoke(interceptor, pkg, intent.getDataString(), action, activities, runnable);
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "Query: " + intent, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } */ else {
                                Utils.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ctx, "JumpController/E: Unknown result: " + result + " for " + intent, Toast.LENGTH_SHORT).show();
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
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public int checkIntent(Context ctx, Intent intent) {
        if (intent == null) return JMP_DEFAULT;
        return JMP_DEFAULT;
    }
}
