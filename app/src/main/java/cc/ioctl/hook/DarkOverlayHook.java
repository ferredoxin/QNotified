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
package cc.ioctl.hook;

import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;
import static nil.nadph.qnotified.util.Utils.logi;

import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexFieldDescriptor;
import nil.nadph.qnotified.util.DexFlow;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.DexMethodDescriptor;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;

@FunctionEntry
public class DarkOverlayHook extends CommonDelayableHook {

    public static final DarkOverlayHook INSTANCE = new DarkOverlayHook();
    private static final String cache_night_mask_field = "cache_night_mask_field";
    private static final String cache_night_mask_field_version_code = "cache_night_mask_field_version_code";


    DarkOverlayHook() {
        super("qn_disable_dark_overlay", new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__handleNightMask),
            new FindNightMask());
    }

    @Override
    public boolean initOnce() {
        try {
            Method handleNightMask = DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__handleNightMask);
            XposedBridge.hookMethod(handleNightMask, new XC_MethodHook(49) {
                Field fMask = null;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    if (fMask == null) {
                        DexFieldDescriptor desc = FindNightMask.getNightMaskField();
                        if (desc == null) {
                            loge("FindNightMask/E getNightMaskField return null");
                            return;
                        }
                        fMask = desc.getFieldInstance(Initiator.getHostClassLoader());
                        if (fMask != null) {
                            fMask.setAccessible(true);
                        }
                    }
                    if (fMask != null) {
                        Object chatPie = param.thisObject;
                        View mask = (View) fMask.get(chatPie);
                        if (mask != null) {
                            mask.setVisibility(View.GONE);
                        }
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_6_0)) {
            return false;
        }
        return super.isEnabled();
    }

    private static class FindNightMask extends Step {

        public static DexFieldDescriptor getNightMaskField() {
            String fieldName = null;
            ConfigManager cache = ConfigManager.getCache();
            int lastVersion = cache.getIntOrDefault(cache_night_mask_field_version_code, 0);
            int version = HostInformationProviderKt.getHostInfo().getVersionCode32();
            if (version == lastVersion) {
                String name = cache.getString(cache_night_mask_field);
                if (name != null && name.length() > 0) {
                    fieldName = name;
                }
            }
            if (fieldName != null) {
                return new DexFieldDescriptor(fieldName);
            }
            Class<?> baseChatPie = Initiator._BaseChatPie();
            if (baseChatPie == null) {
                return null;
            }
            DexMethodDescriptor handleNightMask = DexKit
                .doFindMethodDesc(DexKit.N_BASE_CHAT_PIE__handleNightMask);
            if (handleNightMask == null) {
                logi("getNightMaskField: handleNightMask is null");
                return null;
            }
            byte[] dex = DexKit.getClassDeclaringDex(DexMethodDescriptor.getTypeSig(baseChatPie),
                DexKit.d(DexKit.N_BASE_CHAT_PIE__handleNightMask));
            DexFieldDescriptor field;
            try {
                field = DexFlow.guessFieldByNewInstance(dex, handleNightMask, View.class);
            } catch (Exception e) {
                log(e);
                return null;
            }
            if (field != null) {
                cache.putString(cache_night_mask_field, field.toString());
                cache.putInt(cache_night_mask_field_version_code, version);
                try {
                    cache.save();
                } catch (IOException e) {
                    log(e);
                }
                return field;
            }
            return null;
        }

        @Override
        public boolean step() {
            return getNightMaskField() != null;
        }

        @Override
        public boolean isDone() {
            try {
                ConfigManager cache = ConfigManager.getCache();
                int lastVersion = cache.getIntOrDefault(cache_night_mask_field_version_code, 0);
                if (HostInformationProviderKt.getHostInfo().getVersionCode32() != lastVersion) {
                    return false;
                }
                String name = cache.getString(cache_night_mask_field);
                return name != null && name.length() > 0;
            } catch (Exception e) {
                log(e);
                return false;
            }
        }

        @Override
        public int getPriority() {
            return 20;
        }

        @Override
        public String getDescription() {
            return "定位 BaseChatPie->mMask:View";
        }
    }
}
