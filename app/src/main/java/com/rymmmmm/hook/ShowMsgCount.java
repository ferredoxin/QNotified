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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.NonNull;

import static nil.nadph.qnotified.util.Utils.*;

//显示具体消息数量
public class ShowMsgCount extends CommonDelayableHook {
    private static final ShowMsgCount self = new ShowMsgCount();

    private ShowMsgCount() {
        super("rq_show_msg_count", SyncUtils.PROC_MAIN, new DexDeobfStep(DexKit.C_CustomWidgetUtil));
    }

    @NonNull
    public static ShowMsgCount get() {
        return self;
    }

    @Override
    public boolean initOnce() {
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
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}

