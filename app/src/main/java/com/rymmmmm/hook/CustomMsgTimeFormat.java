/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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
import cc.ioctl.dialog.RikkaCustomMsgTimeFormatDialog;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//自定义聊天页面时间格式
public class CustomMsgTimeFormat extends CommonDelayableHook {
    private static final CustomMsgTimeFormat self = new CustomMsgTimeFormat();

    public static CustomMsgTimeFormat get() {
        return self;
    }

    protected CustomMsgTimeFormat() {
        super("__NOT_USED__", new DexDeobfStep(DexKit.C_TimeFormatterUtils));
    }

    @Override
    public boolean initOnce() {
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
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
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
