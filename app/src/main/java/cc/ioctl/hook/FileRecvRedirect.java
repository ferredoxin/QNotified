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

import android.os.Environment;
import androidx.annotation.Nullable;
import java.lang.reflect.Field;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
public class FileRecvRedirect extends BaseDelayableHook {

    public static final FileRecvRedirect INSTANCE = new FileRecvRedirect();
    private boolean inited = false;

    private Field TARGET_FIELD = null;

    FileRecvRedirect() {
    }

    @Override
    public boolean init() {
        if (inited) {
            return true;
        }
        try {
            if (!isEnabled()) {
                return false;
            }
            String redirectPath = getRedirectPath();
            if (redirectPath != null) {
                inited = doSetPath(redirectPath);
                return inited;
            } else {
                return false;
            }
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private boolean doSetPath(String str) {
        Field[] fields = DexKit.doFindClass(DexKit.C_APP_CONSTANTS).getFields();
        try {
            if (TARGET_FIELD == null) {
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object value = field.get(null);
                    String path = String.valueOf(value);
                    if (path.toLowerCase().endsWith("file_recv/")) {
                        TARGET_FIELD = field;
                        break;
                    }
                }
            }
            TARGET_FIELD.setAccessible(true);
            TARGET_FIELD.set(null, str);
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    public String getDefaultPath() {
        if (HostInformationProviderKt.getHostInfo().isTim()) {
            return Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Tencent/TIMfile_recv/";
        } else {
            if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_2_8)) {
                return HostInformationProviderKt.getHostInfo().getApplication()
                    .getExternalFilesDir(null) + "/Tencent/QQfile_recv/";
            } else {
                return Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Tencent/QQfile_recv/";
            }
        }
    }

    @Nullable
    public String getRedirectPath() {
        return ConfigManager.getDefaultConfig().getString(ConfigItems.qn_file_recv_redirect_path);
    }

    public void setRedirectPathAndEnable(String path) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putString(ConfigItems.qn_file_recv_redirect_path, path);
            cfg.putBoolean(ConfigItems.qn_file_recv_redirect_enable, true);
            cfg.save();
            inited = doSetPath(path);
        } catch (Exception e) {
            log(e);
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_APP_CONSTANTS)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig()
                .getBooleanOrFalse(ConfigItems.qn_file_recv_redirect_enable);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    /**
     * Still follow the rule only apply if it is already inited.
     *
     * @param enabled if true set to config value, otherwise restore to default value
     */
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putBoolean(ConfigItems.qn_file_recv_redirect_enable, enabled);
            cfg.save();
            if (inited) {
                if (enabled) {
                    String path = getRedirectPath();
                    if (path != null) {
                        inited = doSetPath(path);
                    }
                } else {
                    doSetPath(getDefaultPath());
                }
            }
        } catch (Exception e) {
            log(e);
        }
    }
}
