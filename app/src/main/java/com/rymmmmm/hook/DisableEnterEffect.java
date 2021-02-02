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
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;

import static nil.nadph.qnotified.util.Utils.log;

//屏蔽所有进场特效
public class DisableEnterEffect extends CommonDelayableHook {
    private static final DisableEnterEffect self = new DisableEnterEffect();

    public static DisableEnterEffect get() {
        return self;
    }

    protected DisableEnterEffect() {
        super("rq_disable_enter_effect");
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator._TroopEnterEffectController().getDeclaredMethods()) {
                if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class) {
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
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
