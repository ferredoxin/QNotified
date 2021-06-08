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
package nil.nadph.qnotified.config;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cc.ioctl.H;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import nil.nadph.qnotified.util.Utils;

public abstract class ConfigManager implements SharedPreferences, SharedPreferences.Editor {

    private static ConfigManager sDefConfig;
    private static ConfigManager sCache;
    private static final ConcurrentHashMap<Long, ConfigManager> sUinConfig =
        new ConcurrentHashMap<>(4);

    protected ConfigManager() {
    }

    @NonNull
    public static synchronized ConfigManager getDefaultConfig() {
        if (sDefConfig == null) {
            ConfigManager overlay = new MmkvConfigManagerImpl("global_config");
            ConfigManager base = null;
            File f = new File(H.getApplication().getFilesDir(), "qnotified_config.dat");
            if (f.exists()) {
                try {
                    base = new LegacyRoConfigManager(f);
                } catch (Exception | OutOfMemoryError e) {
                    try {
                        f.delete();
                    } catch (Exception ignored) {
                    }
                }
            }
            if (base == null) {
                sDefConfig = overlay;
            } else {
                sDefConfig = new OverlayfsConfigManagerImpl(overlay, base);
            }
        }
        return sDefConfig;
    }

    /**
     * Get isolated config for a specified account
     *
     * @param uin account number
     * @return config for raed/write
     */
    @NonNull
    public static synchronized ConfigManager forAccount(long uin) {
        if (uin < 10000) {
            throw new IllegalArgumentException("uin must >= 10000");
        }
        ConfigManager cfg = sUinConfig.get(uin);
        if (cfg != null) {
            return cfg;
        }
        ConfigManager overlay = new MmkvConfigManagerImpl("u_" + uin);
        ConfigManager base = null;
        File f = new File(H.getApplication().getFilesDir(), "qnotified_" + uin + ".dat");
        if (f.exists()) {
            try {
                base = new LegacyRoConfigManager(f);
            } catch (Exception | OutOfMemoryError e) {
                try {
                    f.delete();
                } catch (Exception ignored) {
                }
            }
        }
        if (base == null) {
            cfg = overlay;
        } else {
            cfg = new OverlayfsConfigManagerImpl(overlay, base);
        }
        sUinConfig.put(uin, cfg);
        return cfg;
    }

    /**
     * Get isolated config for current account logged in. See {@link #forAccount(long)}
     *
     * @return if no account is logged in, {@code null} will be returned.
     */
    @Nullable
    public static ConfigManager forCurrentAccount() {
        long uin = Utils.getLongAccountUin();
        if (uin >= 10000) {
            return forAccount(uin);
        }
        return null;
    }

    @NonNull
    public static synchronized ConfigManager getCache() {
        if (sCache == null) {
            sCache = new MmkvConfigManagerImpl("global_cache");
        }
        return sCache;
    }

    public abstract void reinit() throws IOException;

    @Nullable
    public abstract File getFile();

    @Nullable
    public Object getOrDefault(@NonNull String key, @Nullable Object def) {
        if (!containsKey(key)) {
            return def;
        }
        return getObject(key);
    }

    public boolean getBooleanOrFalse(@NonNull String key) {
        return getBooleanOrDefault(key, false);
    }

    public boolean getBooleanOrDefault(@NonNull String key, boolean def) {
        return getBoolean(key, def);
    }

    public int getIntOrDefault(@NonNull String key, int def) {
        return getInt(key, def);
    }

    @Nullable
    public abstract String getString(@NonNull String key);

    @NonNull
    public String getStringOrDefault(@NonNull String key, @NonNull String defVal) {
        return getString(key, defVal);
    }

    @Nullable
    public abstract Object getObject(@NonNull String key);

    @Nullable
    public byte[] getBytes(@NonNull String key) {
        return getBytes(key, null);
    }

    @Nullable
    public abstract byte[] getBytes(@NonNull String key, @Nullable byte[] defValue);

    @NonNull
    public abstract byte[] getBytesOrDefault(@NonNull String key, @NonNull byte[] defValue);

    @NonNull
    public abstract ConfigManager putBytes(@NonNull String key, @NonNull byte[] value);

    /**
     * @return READ-ONLY all config
     * @deprecated Avoid use getAll(), MMKV only have limited support for this.
     */
    @Override
    @Deprecated
    @NonNull
    public abstract Map<String, ?> getAll();

    public abstract void reload() throws IOException;

    public abstract void save() throws IOException;

    public abstract void saveAndNotify(int what) throws IOException;

    public abstract void saveWithoutNotify() throws IOException;

    public long getLongOrDefault(@Nullable String key, long i) {
        return getLong(key, i);
    }

    @NonNull
    public abstract ConfigManager putObject(@NonNull String key, @NonNull Object v);

    public boolean containsKey(@NonNull String k) {
        return contains(k);
    }

    @NonNull
    @Override
    public Editor edit() {
        return this;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
        @NonNull OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
        @NonNull OnSharedPreferenceChangeListener listener) {
        throw new UnsupportedOperationException("not implemented");
    }

    public abstract boolean isReadOnly();

    public abstract boolean isPersistent();
}
