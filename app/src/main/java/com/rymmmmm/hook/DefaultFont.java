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

import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//强制使用默认字体
@FunctionEntry
public class DefaultFont extends CommonDelayableHook {

    public static final DefaultFont INSTANCE = new DefaultFont();

    protected DefaultFont() {
        super("rq_default_font");
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> C_ChatMessage = Initiator.load("com.tencent.mobileqq.data.ChatMessage");
            for (Method m : Initiator._TextItemBuilder().getDeclaredMethods()) {
                if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers())
                    && m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 2 && argt[0] != View.class && argt[1] == C_ChatMessage) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return;
                                }
                                if (!isEnabled()) {
                                    return;
                                }
                                param.setResult(null);
                            }
                        });
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }
}
