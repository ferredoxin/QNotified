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
package me.kyuubiran.hook;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//屏蔽聊天界面相机图标
public class RemoveCameraButton extends CommonDelayableHook {
    private static final RemoveCameraButton self = new RemoveCameraButton();


    public static RemoveCameraButton get() {
        return self;
    }

    private RemoveCameraButton() {
        super("kr_disable_camera_button");
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator._ConversationTitleBtnCtrl().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if ("a".equals(m.getName()) && m.getReturnType() == void.class && argt.length == 0)
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            param.setResult(null);
                        }
                    });
            }
            return true;
        } catch (Exception t) {
            Utils.log(t);
            return false;
        }
    }
}
