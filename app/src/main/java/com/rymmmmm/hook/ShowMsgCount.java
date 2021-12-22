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

import android.view.ViewGroup;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import me.singleneuron.qn_kernel.annotation.UiItem;
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge;
import me.singleneuron.qn_kernel.data.HostInfo;
import me.singleneuron.qn_kernel.tlb.UiRoutineKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.QQVersion;
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//显示具体消息数量
@FunctionEntry
@UiItem
public class ShowMsgCount extends CommonDelayAbleHookBridge {

    public static final ShowMsgCount INSTANCE = new ShowMsgCount();

    private ShowMsgCount() {
        super(new DexDeobfStep(DexKit.C_CustomWidgetUtil));
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
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            param.args[4] = Integer.MAX_VALUE;
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            if (HostInfo.requireMinQQVersion(QQVersion.QQ_8_8_11)) {
                                TextView tv = (TextView) param.args[0];
                                tv.setMaxWidth(Integer.MAX_VALUE);
                                ViewGroup.LayoutParams lp = tv.getLayoutParams();
                                lp.width = -2;
                                tv.setLayoutParams(lp);
                            }
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

    private final UiSwitchPreference mUiSwitchPreference = this.new UiSwitchPreferenceItemFactory(
        "显示具体消息数量");

    @NotNull
    @Override
    public UiSwitchPreference getPreference() {
        return mUiSwitchPreference;
    }

    @Nullable
    @Override
    public String[] getPreferenceLocate() {
        return UiRoutineKt.get花Q();
    }
}

