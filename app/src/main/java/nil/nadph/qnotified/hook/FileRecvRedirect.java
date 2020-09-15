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

import android.os.Environment;

import java.lang.reflect.Field;

import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Nullable;

import static nil.nadph.qnotified.util.Utils.*;

public class FileRecvRedirect extends BaseDelayableHook {
    private static final FileRecvRedirect self = new FileRecvRedirect();
    private boolean inited = false;

    private Field TARGET_FIELD = null;

    FileRecvRedirect() {
    }

    public static FileRecvRedirect get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            if (!isEnabled()) return false;
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
        if (isTim(getApplication())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tencent/TIMfile_recv/";
        } else {
            if (getHostVersionCode32() > 1334) {
                return getApplication().getExternalFilesDir(null) + "/Tencent/QQfile_recv/";
            } else {
                return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Tencent/QQfile_recv/";
            }
        }
    }

    @Nullable
    public String getRedirectPath() {
        return ConfigManager.getDefaultConfig().getString(ConfigItems.qn_file_recv_redirect_path);
    }

    /**
     * Still follow the rule
     * only apply if it is already inited.
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(ConfigItems.qn_file_recv_redirect_enable);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
