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
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//屏蔽头像挂件
@FunctionEntry
public class DisableAvatarDecoration extends CommonDelayableHook {

    public static final DisableAvatarDecoration INSTANCE = new DisableAvatarDecoration();

    protected DisableAvatarDecoration() {
        super("rq_disable_avatar_decoration");
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator.load("com.tencent.mobileqq.vas.PendantInfo")
                .getDeclaredMethods()) {
                if (m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length != 5) {
                        continue;
                    }
                    if (argt[0] != View.class) {
                        continue;
                    }
                    if (argt[1] != int.class) {
                        continue;
                    }
                    if (argt[2] != long.class) {
                        continue;
                    }
                    if (argt[3] != String.class) {
                        continue;
                    }
                    if (argt[4] != int.class) {
                        continue;
                    }
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
            return true;
        } catch (Throwable t) {
            Utils.log(t);
            return false;
        }
    }
}
