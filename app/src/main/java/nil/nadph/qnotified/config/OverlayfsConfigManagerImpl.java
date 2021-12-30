/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OverlayfsConfigManagerImpl extends ConfigManager {

    @NonNull
    final ConfigManager upper;
    @NonNull
    final ConfigManager lower;

    public OverlayfsConfigManagerImpl(@NonNull ConfigManager u, @NonNull ConfigManager l) {
        upper = u;
        lower = l;
    }

    @Override
    public void reinit() throws IOException {
        upper.reinit();
        lower.reinit();
    }

    @Nullable
    @Override
    public File getFile() {
        return upper.getFile();
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) {
        String result = upper.getString(key);
        if (result != null) {
            return result;
        }
        return lower.getString(key);
    }

    @Nullable
    @Override
    public Object getObject(@NonNull String key) {
        Object result = upper.getObject(key);
        if (result != null) {
            return result;
        }
        return lower.getObject(key);
    }

    @NonNull
    @Override
    public Map<String, ?> getAll() {
        return new Map<String, Object>() {
            final Map<String, Object> upperEntry = (Map<String, Object>) upper.getAll();
            final Map<String, Object> lowerEntry = (Map<String, Object>) lower.getAll();

            @Override
            public int size() {
                return keySet().size();
            }

            @Override
            public boolean isEmpty() {
                return upperEntry.isEmpty() && lowerEntry.isEmpty();
            }

            @Override
            public boolean containsKey(@Nullable Object key) {
                return upperEntry.containsKey(key) || lowerEntry.containsKey(key);
            }

            @Override
            public boolean containsValue(@Nullable Object value) {
                return upperEntry.containsValue(value) || lowerEntry.containsValue(value);
            }

            @Nullable
            @Override
            public Object get(@Nullable Object key) {
                Object result = upperEntry.get(key);
                if (result != null) {
                    return result;
                }
                return lowerEntry.get(key);
            }

            @Nullable
            @Override
            public Object put(String key, Object value) {
                Object last = upperEntry.put(key, value);
                if (last != null) {
                    return last;
                } else {
                    return lowerEntry.get(key);
                }
            }

            @Nullable
            @Override
            public Object remove(@Nullable Object key) {
                Object last = upperEntry.remove(key);
                if (last != null) {
                    return last;
                } else {
                    //noinspection SuspiciousMethodCalls
                    return lowerEntry.get(key);
                }
            }

            @Override
            public void putAll(@NonNull Map<? extends String, ?> m) {
                for (Map.Entry entry : m.entrySet()) {
                    String key = (String) entry.getKey();
                    Object value = entry.getValue();
                    putObject(key, value);
                }
            }

            @Override
            public void clear() {
                upper.clear();
                if (!lower.isReadOnly()) {
                    lower.clear();
                }
            }

            @NonNull
            @Override
            public Set<String> keySet() {
                Set<String> keys = new HashSet<>();
                keys.addAll(upperEntry.keySet());
                keys.addAll(lowerEntry.keySet());
                return keys;
            }

            @NonNull
            @Override
            public Collection<Object> values() {
                ArrayList<Object> results = new ArrayList<>();
                for (Map.Entry entry : entrySet()) {
                    results.add(entry.getValue());
                }
                return results;
            }

            @NonNull
            @Override
            public Set<Entry<String, Object>> entrySet() {
                HashMap<String, Entry> result = new HashMap<>();
                for (Map.Entry entry : lowerEntry.entrySet()) {
                    String key = (String) entry.getKey();
                    result.put(key, entry);
                }
                for (Map.Entry entry : upperEntry.entrySet()) {
                    String key = (String) entry.getKey();
                    result.put(key, entry);
                }
                return new HashSet(result.values());
            }
        };
    }

    @Nullable
    @Override
    public String getString(@NonNull String key, @Nullable String defValue) {
        return upper.getString(key, lower.getString(key, defValue));
    }

    @Nullable
    @Override
    public Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValues) {
        return upper.getStringSet(key, lower.getStringSet(key, defValues));
    }

    @Override
    public int getInt(@NonNull String key, int defValue) {
        return upper.getInt(key, lower.getInt(key, defValue));
    }

    @Override
    public long getLong(@NonNull String key, long defValue) {
        return upper.getLong(key, lower.getLong(key, defValue));
    }

    @Override
    public float getFloat(@NonNull String key, float defValue) {
        return upper.getFloat(key, lower.getFloat(key, defValue));
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return upper.getBoolean(key, lower.getBoolean(key, defValue));
    }

    @Override
    public boolean contains(@NonNull String key) {
        return upper.contains(key) || lower.contains(key);
    }

    @Override
    public void reload() throws IOException {
        upper.reload();
        if (!lower.isReadOnly()) {
            lower.reload();
        }
    }

    @Override
    public void save() throws IOException {
        upper.save();
    }

    @Override
    public void saveAndNotify(int what) throws IOException {
        upper.saveAndNotify(what);
    }

    @Override
    public void saveWithoutNotify() throws IOException {
        upper.saveWithoutNotify();
    }

    @NonNull
    @Override
    public ConfigManager putObject(@NonNull String key, @NonNull Object v) {
        upper.putObject(key, v);
        return this;
    }

    @NonNull
    @Override
    public Editor putString(@NonNull String key, @Nullable String value) {
        upper.putString(key, value);
        return this;
    }

    @NonNull
    @Override
    public Editor putStringSet(@NonNull String key, @Nullable Set<String> values) {
        upper.putStringSet(key, values);
        return this;
    }

    @NonNull
    @Override
    public Editor putInt(@NonNull String key, int value) {
        upper.putInt(key, value);
        return this;
    }

    @NonNull
    @Override
    public Editor putLong(@NonNull String key, long value) {
        upper.putLong(key, value);
        return this;
    }

    @NonNull
    @Override
    public Editor putFloat(@NonNull String key, float value) {
        upper.putFloat(key, value);
        return this;
    }

    @NonNull
    @Override
    public Editor putBoolean(@NonNull String key, boolean value) {
        upper.putBoolean(key, value);
        return this;
    }

    @NonNull
    @Override
    public Editor remove(@NonNull String key) {
        upper.remove(key);
        if (!lower.isReadOnly()) {
            lower.remove(key);
        }
        return this;
    }

    @NonNull
    @Override
    public Editor clear() {
        upper.clear();
        if (!lower.isReadOnly()) {
            lower.clear();
        }
        return this;
    }

    @Nullable
    @Override
    public byte[] getBytes(@NonNull String key, @Nullable byte[] defValue) {
        byte[] result = upper.getBytes(key, null);
        if (result != null) {
            return result;
        }
        return lower.getBytes(key, defValue);
    }

    @NonNull
    @Override
    public byte[] getBytesOrDefault(@NonNull String key, @NonNull byte[] defValue) {
        byte[] result = upper.getBytes(key, null);
        if (result != null) {
            return result;
        }
        return lower.getBytesOrDefault(key, defValue);
    }

    @NonNull
    @Override
    public ConfigManager putBytes(@NonNull String key, @NonNull byte[] value) {
        upper.putBytes(key, value);
        return this;
    }

    @Override
    public boolean commit() {
        return upper.commit();
    }

    @Override
    public void apply() {
        upper.apply();
    }

    @Override
    public boolean isPersistent() {
        return upper.isPersistent();
    }

    @Override
    public boolean isReadOnly() {
        return upper.isReadOnly();
    }
}
