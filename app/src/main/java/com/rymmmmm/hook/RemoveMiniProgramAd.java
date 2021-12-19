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

import static me.singleneuron.qn_kernel.data.HostInfo.requireMinQQVersion;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.ReflexUtil.iput_object;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.annotation.UiItem;
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge;
import me.singleneuron.qn_kernel.tlb.UiRoutineKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.QQVersion;
import nil.nadph.qnotified.util.Utils;
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference;

//去除小程序广告 需要手动点关闭
@FunctionEntry
@UiItem
public class RemoveMiniProgramAd extends CommonDelayAbleHookBridge {

    private final UiSwitchPreference mUiSwitchPreference = this.new UiSwitchPreferenceItemFactory("屏蔽小程序广告", "需要手动关闭广告, 请勿反馈此功能无效");

    @NonNull
    @Override
    public UiSwitchPreference getPreference() {
        return mUiSwitchPreference;
    }

    @Nullable
    @Override
    public String[] getPreferenceLocate() {
        return UiRoutineKt.get净化_扩展();
    }

    public static final RemoveMiniProgramAd INSTANCE = new RemoveMiniProgramAd();

    protected RemoveMiniProgramAd() {
        super(
            SyncUtils.PROC_ANY & ~(SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF | SyncUtils.PROC_QZONE
                | SyncUtils.PROC_PEAK | SyncUtils.PROC_VIDEO));
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator._GdtMvViewController().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("x") && argt.length == 0) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            iput_object(param.thisObject, "c", Boolean.TYPE, true);
                            if (requireMinQQVersion(QQVersion.QQ_8_4_1)) {
                                invoke_virtual(param.thisObject, "e");
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
}
