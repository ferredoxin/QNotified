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
import com.google.gson.Gson;
import com.tencent.mmkv.MMKV;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import nil.nadph.qnotified.util.Utils;

public class MmkvConfigManagerImpl extends ConfigManager {

    final MMKV mmkv;
    final File file;
    public static final String TYPE_SUFFIX = "$shadow$type";
    private static final String CLASS_SUFFIX = "$shadow$class";
    private static final int TYPE_BOOL = 0x80 + 2;
    private static final int TYPE_INT = 0x80 + 4;
    private static final int TYPE_LONG = 0x80 + 6;
    private static final int TYPE_FLOAT = 0x80 + 7;
    private static final int TYPE_STRING = 0x80 + 31;
    private static final int TYPE_STRING_SET = 0x80 + 32;
    private static final int TYPE_BYTES = 0x80 + 33;
    private static final int TYPE_SERIALIZABLE = 0x80 + 41;
    public static final int TYPE_JSON = 0x80 + 42;

    HashMap<String, Entry<String, Object>> mCacheMap = new HashMap<>();

    class VirtEntry implements Map.Entry<String, Object> {

        VirtEntry(String key) {
            this.key = key;
        }

        final String key;

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return getObject(key);
        }

        @Override
        public Object setValue(Object value) {
            return putObject(key, value);
        }

        @Override
        public boolean equals(@Nullable Object o) {
            if (o instanceof VirtEntry) {
                return key.equals(((VirtEntry) o).key);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }
    }

    final Set<Entry<String, Object>> mVirtEntrySet = new Set<>() {
        @Override
        public int size() {
            return mShadowMap.size();
        }

        @Override
        public boolean isEmpty() {
            return mShadowMap.isEmpty();
        }

        @Override
        public boolean contains(@Nullable Object o) {
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<String, Object> entry = (Entry<String, Object>) o;
            for (Entry<String, Object> e : this) {
                if (e.equals(entry)) {
                    return true;
                }
            }
            return false;
        }

        @NonNull
        @Override
        public Iterator<Entry<String, Object>> iterator() {
            return new Iterator<>() {
                final Iterator<String> iterator = mShadowMap.keySet().iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Entry<String, Object> next() {
                    String key = iterator.next();
                    Entry<String, Object> ret = mCacheMap.get(key);
                    if (ret != null) {
                        return ret;
                    } else {
                        ret = new VirtEntry(key);
                        mCacheMap.put(key, ret);
                        return ret;
                    }
                }
            };
        }

        @NonNull
        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("entry set");
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean add(Entry<String, Object> stringObjectEntry) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean remove(@Nullable Object o) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(@NonNull Collection<? extends Entry<String, Object>> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("entry set");
        }
    };

    final Collection<Object> mVirtValues = new Collection<>() {
        @Override
        public int size() {
            return mShadowMap.size();
        }

        @Override
        public boolean isEmpty() {
            return mShadowMap.isEmpty();
        }

        @Override
        public boolean contains(@Nullable Object o) {
            for (Object val : this) {
                if (Objects.equals(o, val)) {
                    return true;
                }
            }
            return false;
        }

        @NonNull
        @Override
        public Iterator<Object> iterator() {
            return new Iterator<>() {
                final Iterator<String> iterator = mShadowMap.keySet().iterator();

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public Object next() {
                    return getObject(iterator.next());
                }
            };
        }

        @NonNull
        @Override
        public Object[] toArray() {
            throw new UnsupportedOperationException("entry set");
        }

        @NonNull
        @Override
        public <T> T[] toArray(@NonNull T[] a) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean remove(@Nullable Object o) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean containsAll(@NonNull Collection<?> c) {
            for (Object o : c) {
                if (!contains(o)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean addAll(@NonNull Collection<?> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean removeAll(@NonNull Collection<?> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public boolean retainAll(@NonNull Collection<?> c) {
            throw new UnsupportedOperationException("entry set");
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException("entry set");
        }
    };
    final Map<String, Object> mShadowMap = new Map<>() {
        @Override
        public int size() {
            return entrySet().size();
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean containsKey(@Nullable Object key) {
            Objects.requireNonNull(key);
            return MmkvConfigManagerImpl.this.containsKey((String) key);
        }

        @Override
        public boolean containsValue(@Nullable Object value) {
            for (Object o : entrySet()) {
                if (Objects.equals(o, value)) {
                    return true;
                }
            }
            return false;
        }

        @Nullable
        @Override
        public Object get(@Nullable Object key) {
            Objects.requireNonNull(key);
            return getObject((String) key);
        }

        @Nullable
        @Override
        public Object put(String key, Object value) {
            Objects.requireNonNull(key);
            Object obj = getObject(key);
            putObject(key, value);
            return obj;
        }

        @Nullable
        @Override
        public Object remove(@Nullable Object key) {
            Objects.requireNonNull(key);
            Object obj = getObject((String) key);
            if (obj != null) {
                mmkv.remove((String) key);
            }
            return obj;
        }

        @Override
        public void putAll(@NonNull Map<? extends String, ?> m) {
            for (Entry<? extends String, ?> entry : m.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                putObject(key, value);
            }
        }

        @Override
        public void clear() {
            mmkv.clear();
        }

        @NonNull
        @Override
        public Set<String> keySet() {
            Set<String> keys = new HashSet<>();
            String[] allKeys = mmkv.allKeys();
            if (allKeys == null) {
                return keys;
            }
            for (String s : allKeys) {
                if (!s.endsWith(TYPE_SUFFIX)) {
                    keys.add(s);
                }
            }
            return keys;
        }

        @NonNull
        @Override
        public Collection<Object> values() {
            return mVirtValues;
        }

        @NonNull
        @Override
        public Set<Entry<String, Object>> entrySet() {
            return mVirtEntrySet;
        }
    };

    protected MmkvConfigManagerImpl(@NonNull String name) {
        mmkv = MMKV.mmkvWithID(name, MMKV.MULTI_PROCESS_MODE);
        file = new File(MMKV.getRootDir(), name);
    }

    @Override
    public void reinit() {
        // not required
    }

    @NonNull
    @Override
    public File getFile() {
        return file;
    }

    @Nullable
    @Override
    public String getString(@NonNull String key) {
        return mmkv.getString(key, null);
    }

    @Nullable
    @Override
    public Object getObject(@NonNull String key) {
        if (!mmkv.contains(key)) {
            return null;
        }
        switch (mmkv.getInt(key.concat(TYPE_SUFFIX), 0)) {
            case TYPE_BOOL:
                return mmkv.getBoolean(key, false);
            case TYPE_FLOAT:
                return mmkv.getFloat(key, 0);
            case TYPE_INT:
                return mmkv.getInt(key, 0);
            case TYPE_LONG:
                return mmkv.getLong(key, 0L);
            case TYPE_STRING:
                return mmkv.getString(key, null);
            case TYPE_STRING_SET:
                return mmkv.getStringSet(key, null);
            case TYPE_BYTES:
                return mmkv.getBytes(key, null);
            case TYPE_SERIALIZABLE: {
                byte[] bytes = mmkv.getBytes(key, null);
                if (bytes == null) {
                    return null;
                }
                ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    return objectInputStream.readObject();
                } catch (Exception e) {
                    Utils.log(e);
                    return null;
                }
            }
            case TYPE_JSON: {
                String string = mmkv.getString(key, null);
                String clazz = mmkv.getString(key.concat(CLASS_SUFFIX), null);
                if (string == null || clazz == null) {
                    return null;
                }
                try {
                    Class<?> clazz2 = Class.forName(clazz);
                    return new Gson().fromJson(string, clazz2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
            default:
                return null;
        }
    }

    @NonNull
    @Override
    public Map<String, ?> getAll() {
        return mShadowMap;
    }

    @Nullable
    @Override
    public String getString(@NonNull String key, @Nullable String defValue) {
        return mmkv.getString(key, defValue);
    }

    @Nullable
    @Override
    public Set<String> getStringSet(@NonNull String key, @Nullable Set<String> defValues) {
        return mmkv.getStringSet(key, defValues);
    }

    @Override
    public int getInt(@NonNull String key, int defValue) {
        return mmkv.getInt(key, defValue);
    }

    @Override
    public long getLong(@NonNull String key, long defValue) {
        return mmkv.getLong(key, defValue);
    }

    @Override
    public float getFloat(@NonNull String key, float defValue) {
        return mmkv.getFloat(key, defValue);
    }

    @Override
    public boolean getBoolean(@NonNull String key, boolean defValue) {
        return mmkv.getBoolean(key, defValue);
    }

    @Override
    public boolean contains(@NonNull String key) {
        return mmkv.contains(key);
    }

    @Override
    public void reload() {
        // not required
    }

    @Override
    public void save() {
        commit();
    }

    @Override
    public void saveAndNotify(int what) {
        commit();
    }

    @Override
    public void saveWithoutNotify() {
        commit();
    }

    @NonNull
    @Override
    public ConfigManager putObject(@NonNull String key, @NonNull Object v) {
        if (v == null || key == null) {
            throw new NullPointerException("null key/value not allowed");
        }
        if (v instanceof Float || v instanceof Double) {
            putFloat(key, ((Number) v).floatValue());
        } else if (v instanceof Long) {
            putLong(key, (long) v);
        } else if (v instanceof Integer) {
            putInt(key, (int) v);
        } else if (v instanceof Boolean) {
            putBoolean(key, (boolean) v);
        } else if (v instanceof String) {
            putString(key, (String) v);
        } else if (v instanceof Set) {
            putStringSet(key, (Set<String>) v);
        } else if (v instanceof byte[]) {
            putBytes(key, (byte[]) v);
        } else if (v instanceof String[]) {
            HashSet<String> set = new HashSet<>(((String[]) v).length);
            Collections.addAll(set, (String[]) v);
            putStringSet(key, set);
        } else if (v instanceof Serializable) {
            try {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                objectOutputStream.writeObject(v);
                mmkv.putBytes(key, outputStream.toByteArray());
                mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_SERIALIZABLE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            mmkv.putString(key, new Gson().toJson(v));
            mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_JSON);
            mmkv.putString(key.concat(CLASS_SUFFIX), v.getClass().getName());
        }
        return this;
    }

    @NonNull
    @Override
    public Editor putString(@NonNull String key, @Nullable String value) {
        mmkv.putString(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_STRING);
        return this;
    }

    @NonNull
    @Override
    public Editor putStringSet(@NonNull String key, @Nullable Set<String> values) {
        mmkv.putStringSet(key, values);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_STRING_SET);
        return this;
    }

    @NonNull
    @Override
    public Editor putInt(@NonNull String key, int value) {
        mmkv.putInt(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_INT);
        return this;
    }

    @NonNull
    @Override
    public Editor putLong(@NonNull String key, long value) {
        mmkv.putLong(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_LONG);
        return this;
    }

    @NonNull
    @Override
    public Editor putFloat(@NonNull String key, float value) {
        mmkv.putFloat(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_FLOAT);
        return this;
    }

    @NonNull
    @Override
    public Editor putBoolean(@NonNull String key, boolean value) {
        mmkv.putBoolean(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_BOOL);
        return this;
    }

    @NonNull
    @Override
    public byte[] getBytesOrDefault(@NonNull String key, @NonNull byte[] defValue) {
        return mmkv.getBytes(key, defValue);
    }

    @Nullable
    @Override
    public byte[] getBytes(@NonNull String key, @Nullable byte[] defValue) {
        return mmkv.getBytes(key, defValue);
    }

    @NonNull
    @Override
    public ConfigManager putBytes(@NonNull String key, @NonNull byte[] value) {
        mmkv.putBytes(key, value);
        mmkv.putInt(key.concat(TYPE_SUFFIX), TYPE_BYTES);
        return this;
    }

    @NonNull
    @Override
    public Editor remove(@NonNull String key) {
        mmkv.remove(key);
        mmkv.remove(key.concat(TYPE_SUFFIX));
        return this;
    }

    @NonNull
    @Override
    public Editor clear() {
        mmkv.clear();
        return this;
    }

    @Override
    public boolean commit() {
        return mmkv.commit();
    }

    @Override
    public void apply() {
        mmkv.apply();
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return true;
    }
}
