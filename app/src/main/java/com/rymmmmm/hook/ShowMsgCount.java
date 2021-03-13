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
package com.rymmmmm.hook;

import static nil.nadph.qnotified.util.Utils.log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;

//显示具体消息数量
@FunctionEntry
public class ShowMsgCount extends CommonDelayableHook {

    public static final ShowMsgCount INSTANCE = new ShowMsgCount();

    private ShowMsgCount() {
        super("rq_show_msg_count", SyncUtils.PROC_MAIN,
            new DexDeobfStep(DexKit.C_CustomWidgetUtil));
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> clazz = DexKit.doFindClass(DexKit.C_CustomWidgetUtil);
            for (Method m : clazz.getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (argt.length == 6 && Modifier.isStatic(m.getModifiers())
                    && m.getReturnType() == void.class) {
                    // TIM 3.1.1(1084) smali references
                    // updateCustomNoteTxt(Landroid/widget/TextView;IIIILjava/lang/String;)V
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
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

