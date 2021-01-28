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
package com.rymmmmm.hook;

import android.annotation.SuppressLint;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.dialog.RikkaCustomMsgTimeFormatDialog;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//自定义聊天页面时间格式
public class CustomMsgTimeFormat extends BaseDelayableHook {
    private static final CustomMsgTimeFormat self = new CustomMsgTimeFormat();
    private boolean isInit = false;

    public static CustomMsgTimeFormat get() {
        return self;
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
    public boolean init() {
        if (isInit) return true;
        try {
            for (Method m : DexKit.doFindClass(DexKit.C_TimeFormatterUtils).getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("a") && argt.length == 3 && Modifier.isStatic(m.getModifiers())) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            String fmt = RikkaCustomMsgTimeFormatDialog.getCurrentMsgTimeFormat();
                            if (fmt != null) {
                                param.setResult(new SimpleDateFormat(fmt).format(new Date((long) param.args[2])));
                            }
                        }
                    });
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
        return new Step[]{new DexDeobfStep(DexKit.C_TimeFormatterUtils)};
    }

    @Override
    public boolean isEnabled() {
        return RikkaCustomMsgTimeFormatDialog.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
