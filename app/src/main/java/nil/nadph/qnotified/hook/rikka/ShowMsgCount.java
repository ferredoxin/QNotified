/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
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
package nil.nadph.qnotified.hook.rikka;

import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.*;

//显示具体消息数量
public class ShowMsgCount extends BaseDelayableHook {
    public static final String rq_show_msg_count = "rq_show_msg_count";
    private static final ShowMsgCount self = new ShowMsgCount();
    private boolean isInit = false;

    private ShowMsgCount() {
    }

    @NonNull
    public static ShowMsgCount get() {
        return self;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            Class<?> clazz = DexKit.doFindClass(DexKit.C_CustomWidgetUtil);
            for (Method m : clazz.getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (argt.length == 6 && Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class) {
                    // TIM 3.1.1(1084) smali references
                    // updateCustomNoteTxt(Landroid/widget/TextView;IIIILjava/lang/String;)V
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            param.args[4] = Integer.MAX_VALUE;
                        }
                    });
                    break;
                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_CustomWidgetUtil)};
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_show_msg_count, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_show_msg_count);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}

