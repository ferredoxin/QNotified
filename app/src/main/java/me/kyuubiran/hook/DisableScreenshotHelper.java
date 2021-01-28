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
package me.kyuubiran.hook;

import android.content.Context;
import android.os.Handler;
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
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.log;

//屏蔽截屏分享
public class DisableScreenshotHelper extends BaseDelayableHook {
    public static final String kr_disable_screenshot_helper = "kr_disable_screenshot_helper";
    private static final DisableScreenshotHelper self = new DisableScreenshotHelper();
    private boolean isInit = false;

    public static DisableScreenshotHelper get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_ANY;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            for (Method m : DexKit.doFindClass(DexKit.C_ScreenShotHelper).getDeclaredMethods()) {
                if (m.getName().equals("a") && Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 3 && argt[0] == Context.class && argt[1] == String.class && argt[2] == Handler.class) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) return;
                                if (!isEnabled()) return;
                                param.setResult(null);
                            }
                        });
                    }
                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ScreenShotHelper)};
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_disable_screenshot_helper);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(kr_disable_screenshot_helper, enabled);
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
}
