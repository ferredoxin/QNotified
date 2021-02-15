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
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//屏蔽群聊界面一起嗨
public class RemovePlayTogether extends CommonDelayableHook {
    private static final RemovePlayTogether self = new RemovePlayTogether();

    public static RemovePlayTogether get() {
        return self;
    }

    private RemovePlayTogether() {
        super("kr_remove_play_together", new DexDeobfStep(DexKit.C_TogetherControlHelper), new DexDeobfStep(DexKit.C_ClockInEntryHelper));
    }

    @Override
    public boolean checkPreconditions() {
        return !HostInformationProviderKt.getHostInfo().isPlayQQ() && super.checkPreconditions();
    }

    @Override
    public boolean initOnce() {
        try {
            String method = "h";
            if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_4_8)) {
                //QQ 8.4.8 除了一起嗨按钮，同一个位置还有一个群打卡按钮。默认显示群打卡，如果已经打卡就显示一起嗨，两个按钮点击之后都会打开同一个界面，但是要同时hook两个
                String entryMethod = "d";
                for (Method m : DexKit.doFindClass(DexKit.C_ClockInEntryHelper).getDeclaredMethods()) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (entryMethod.equals(m.getName()) && m.getReturnType() == boolean.class && argt.length == 0) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return;
                                }
                                if (!isEnabled()) {
                                    return;
                                }
                                param.setResult(false);
                            }
                        });
                    }
                }
                method = "g";
            }
            for (Method m : DexKit.doFindClass(DexKit.C_TogetherControlHelper).getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (method.equals(m.getName()) && m.getReturnType() == void.class && argt.length == 0) {
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
        } catch (Exception t) {
            Utils.log(t);
            return false;
        }
    }
}
