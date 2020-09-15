/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package nil.nadph.qnotified.hook;

import android.app.Application;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

public class DarkOverlayHook extends BaseDelayableHook {
    public static final String qn_disable_dark_overlay = "qn_disable_dark_overlay";
    private static final DarkOverlayHook self = new DarkOverlayHook();
    private boolean inited = false;

    DarkOverlayHook() {
    }

    public static DarkOverlayHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method handleNightMask = DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__handleNightMask);
            XposedBridge.hookMethod(handleNightMask, new XC_MethodHook(49) {
                Field fMask = null;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    if (fMask == null) {
                        DexFieldDescriptor desc = FindNightMask.getNightMaskField();
                        if (desc == null) {
                            loge("FindNightMask/E getNightMaskField return null");
                            return;
                        }
                        fMask = desc.getFieldInstance(Initiator.getHostClassLoader());
                        if (fMask != null) fMask.setAccessible(true);
                    }
                    if (fMask != null) {
                        Object chatPie = param.thisObject;
                        View mask = (View) fMask.get(chatPie);
                        if (mask != null) mask.setVisibility(View.GONE);
                    }
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        //NOTICE: does qzone also has?
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__handleNightMask), new FindNightMask()};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_disable_dark_overlay, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_disable_dark_overlay);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }


    private static final String cache_night_mask_field = "cache_night_mask_field";
    private static final String cache_night_mask_field_version_code = "cache_night_mask_field_version_code";

    private static class FindNightMask extends Step {

        public static DexFieldDescriptor getNightMaskField() {
            String fieldName = null;
            ConfigManager cache = ConfigManager.getCache();
            int lastVersion = cache.getIntOrDefault(cache_night_mask_field_version_code, 0);
            int version = Utils.getHostVersionCode32();
            if (version == lastVersion) {
                String name = cache.getString(cache_night_mask_field);
                if (name != null && name.length() > 0) {
                    fieldName = name;
                }
            }
            if (fieldName != null) return new DexFieldDescriptor(fieldName);
            Class<?> baseChatPie = Initiator._BaseChatPie();
            if (baseChatPie == null) return null;
            DexMethodDescriptor handleNightMask = DexKit.doFindMethodDesc(DexKit.N_BASE_CHAT_PIE__handleNightMask);
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
                if (getHostVersionCode32() != lastVersion) {
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
