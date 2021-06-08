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

import static nil.nadph.qnotified.config.Table.TYPE_ARRAY;
import static nil.nadph.qnotified.config.Table.TYPE_BOOL;
import static nil.nadph.qnotified.config.Table.TYPE_BYTE;
import static nil.nadph.qnotified.config.Table.TYPE_DOUBLE;
import static nil.nadph.qnotified.config.Table.TYPE_EOF;
import static nil.nadph.qnotified.config.Table.TYPE_FLOAT;
import static nil.nadph.qnotified.config.Table.TYPE_INT;
import static nil.nadph.qnotified.config.Table.TYPE_IRAW;
import static nil.nadph.qnotified.config.Table.TYPE_IUTF8;
import static nil.nadph.qnotified.config.Table.TYPE_LONG;
import static nil.nadph.qnotified.config.Table.TYPE_SHORT;
import static nil.nadph.qnotified.config.Table.TYPE_TABLE;
import static nil.nadph.qnotified.config.Table.TYPE_VOID;
import static nil.nadph.qnotified.config.Table.TYPE_WCHAR32;
import static nil.nadph.qnotified.config.Table.VOID_INSTANCE;
import static nil.nadph.qnotified.config.Table.readArray;
import static nil.nadph.qnotified.config.Table.readIRaw;
import static nil.nadph.qnotified.config.Table.readIStr;
import static nil.nadph.qnotified.config.Table.readTable;
import static nil.nadph.qnotified.util.Utils.log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LegacyRoConfigManager extends ConfigManager {

    private final File file;
    private ConcurrentHashMap<String, Object> config;

    public LegacyRoConfigManager(@NonNull File f) throws IOException {
        file = Objects.requireNonNull(f);
        reload();
    }

    @Override
    public void reinit() throws IOException {
        reload();
    }

    @Nullable
    @Override
    public File getFile() {
        return file;
    }

    @NonNull
    @Override
    public Map<String, ?> getAll() {
        return config;
    }

    @Nullable
    @Override
    public String getString(@NonNull String key, @Nullable String defValue) {
        String val = (String) config.get(key);
        if (val == null) {
            val = defValue;
        }
        return val;
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) {
        return (String) config.get(key);
    }

    @Nullable
    @Override
    public Object getObject(@NonNull String key) {
        return config.get(key);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValues) {
        // not implemented
        return null;
    }

    @Override
    public int getInt(@NonNull String key, int defValue) {
        try {
            Number n = (Number) config.get(key);
            if (n != null) {
                return n.intValue();
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @Override
    public long getLong(@NonNull String key, long defValue) {
        try {
            Number n = (Number) config.get(key);
            if (n != null) {
                return n.longValue();
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @Override
    public float getFloat(@NonNull String key, float defValue) {
        try {
            Number n = (Number) config.get(key);
            if (n != null) {
                return n.floatValue();
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defValue) {
        try {
            Boolean n = (Boolean) config.get(key);
            if (n != null) {
                return n;
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @Override
    public boolean contains(@NonNull String key) {
        return config.contains(key);
    }

    /**
     * ?(0xFE)QNC I_version I_size I_RAW_reserved 16_md5 DATA
     */
    @Override
    public void reload() throws IOException {
        synchronized (this) {
            if (config == null) {
                config = new ConcurrentHashMap<>();
            }
            FileInputStream fin;
            fin = new FileInputStream(file);
            if (fin.available() == 0) {
                return;
            }
            config.clear();
            DataInputStream in = new DataInputStream(fin);
            in.skip(4);//flag
            int endian = in.readInt();
            int file_size = in.readInt();
            readIRaw(in);//ignore
            byte[] md5 = new byte[16];
            if (in.read(md5, 0, 16) < 16) {
                throw new IOException("Failed to read md5");
            }
            String key;
            a:
            while (in.available() > 0) {
                int _type = in.read();
                if (_type < 0 || _type > 255) {
                    throw new IOException("Unexpected type:" + _type + ",version:" + endian);
                }
                key = readIStr(in);
                switch ((byte) _type) {
                    case TYPE_VOID:
                        log(new RuntimeException(
                            "ConcurrentHashMap/reload: replace null with " + VOID_INSTANCE
                                + " in [key=\"" + key + "\",type=TYPE_VOID] at " + file
                                .getAbsolutePath()));
                        config.put(key, VOID_INSTANCE);
                        break;
                    case TYPE_BYTE:
                        config.put(key, (byte) in.read());
                        break;
                    case TYPE_BOOL:
                        config.put(key, in.read() != 0);
                        break;
                    case TYPE_WCHAR32:
                        config.put(key, in.readInt());
                        break;
                    case TYPE_INT:
                        config.put(key, in.readInt());
                        break;
                    case TYPE_SHORT:
                        config.put(key, in.readShort());
                        break;
                    case TYPE_LONG:
                        config.put(key, in.readLong());
                        break;
                    case TYPE_FLOAT:
                        config.put(key, in.readFloat());
                        break;
                    case TYPE_DOUBLE:
                        config.put(key, in.readDouble());
                        break;
                    case TYPE_IUTF8:
                        config.put(key, readIStr(in));
                        break;
                    case TYPE_IRAW:
                        config.put(key, readIRaw(in));
                        break;
                    case TYPE_TABLE:
                        config.put(key, readTable(in));
                        break;
                    case TYPE_ARRAY:
                        config.put(key, readArray(in));
                        break;
                    case TYPE_EOF:
                        break a;
                    default:
                        throw new IOException(
                            "Unexpected type:" + _type + ",name:\"" + key + "\",version:" + endian);
                }
            }
        }
    }

    @Override
    public void save() {
        // read only
    }

    @Override
    public void saveAndNotify(int what) {
        // read only
    }

    @Override
    public void saveWithoutNotify() {
        // read only
    }

    @NonNull
    @Override
    public ConfigManager putObject(@NonNull String key, @NonNull Object v) {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }

    @NonNull
    @Override
    public Editor putString(@NonNull String key, @Nullable String value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor putStringSet(@NonNull String key, @Nullable Set<String> values) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor putInt(@NonNull String key, int value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor putLong(@NonNull String key, long value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor putFloat(@NonNull String key, float value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor putBoolean(@NonNull String key, boolean value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public byte[] getBytesOrDefault(@NonNull String key, @NonNull byte[] defValue) {
        try {
            byte[] n = (byte[]) config.get(key);
            if (n != null) {
                return n;
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @Nullable
    @Override
    public byte[] getBytes(@NonNull String key, @Nullable byte[] defValue) {
        try {
            byte[] n = (byte[]) config.get(key);
            if (n != null) {
                return n;
            } else {
                return defValue;
            }
        } catch (ClassCastException e) {
            return defValue;
        }
    }

    @NonNull
    @Override
    public ConfigManager putBytes(@NonNull String key, @NonNull byte[] value) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor remove(@NonNull String key) {
        throw new UnsupportedOperationException("read only");
    }

    @NonNull
    @Override
    public Editor clear() {
        throw new UnsupportedOperationException("read only");
    }

    @Override
    public boolean commit() {
        return false;
    }

    @Override
    public void apply() {
        // read only
    }
}
