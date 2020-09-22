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
package nil.nadph.qnotified.config;

import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static nil.nadph.qnotified.config.Table.*;
import static nil.nadph.qnotified.util.Utils.log;

public class ConfigManager implements SyncUtils.OnFileChangedListener, MultiConfigItem {
    //DataOutputStream should be BIG_ENDIAN, as is.
    public static final int BYTE_ORDER_STUB = 0x12345678;
    private static ConfigManager sDefConfig;
    private static ConfigManager sCache;
    private final File file;
    private ConcurrentHashMap<String, Object> config;
    private boolean dirty;
    private final int mFileTypeId;
    private final long mTargetUin;

    public ConfigManager(File f, int fileTypeId, long uin) throws IOException {
        file = f;
        mFileTypeId = fileTypeId;
        mTargetUin = uin;
        reinit();
    }

    public static ConfigManager getDefaultConfig() {
        try {
            if (sDefConfig == null) {
                sDefConfig = new ConfigManager(new File(Utils.getApplication().getFilesDir().getAbsolutePath() + "/qnotified_config.dat"), SyncUtils.FILE_DEFAULT_CONFIG, 0);
                SyncUtils.addOnFileChangedListener(sDefConfig);
            }
            return sDefConfig;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ConfigManager getCache() {
        try {
            if (sCache == null)
                sCache = new ConfigManager(new File(Utils.getApplication().getFilesDir().getAbsolutePath() + "/qnotified_cache.dat"), SyncUtils.FILE_CACHE, 0);
            SyncUtils.addOnFileChangedListener(sCache);
            return sCache;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //default
    @Override
    public boolean onFileChanged(int type, long uin, int what) {
        if (type == mFileTypeId) {
            dirty = true;
        }
        return false;
    }

    public void reinit() throws IOException {
        if (!file.exists()) file.createNewFile();
        config = new ConcurrentHashMap<String, Object>();
        reload();
    }

    public File getFile() {
        return file;
    }

    public Object getOrDefault(String key, Object def) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        if (!config.containsKey(key)) {
            return def;
        }
        return config.get(key);
    }

    public boolean getBooleanOrFalse(String key) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        if (!config.containsKey(key)) {
            return false;
        }
        try {
            return ((Boolean) config.get(key)).booleanValue();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean getBooleanOrDefault(String key, boolean def) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        if (!config.containsKey(key)) {
            return def;
        }
        try {
            return ((Boolean) config.get(key)).booleanValue();
        } catch (ClassCastException e) {
            return def;
        }
    }

    public int getIntOrDefault(String key, int def) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        if (!config.containsKey(key)) {
            return def;
        }
        try {
            return ((Integer) config.get(key)).intValue();
        } catch (ClassCastException e) {
            return def;
        }
    }

    public String getString(String key) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        return (String) config.get(key);
    }

    public String getStringOrDefault(String key, String defVal) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        String val = (String) config.get(key);
        if (val == null) val = defVal;
        return val;
    }

    @Nullable
    public Object getObject(@NonNull String key) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        return config.get(key);
    }

    public void putString(String key, String val) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        config.put(key, val);
    }

    public void putInt(String key, int val) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        config.put(key, val);
    }

    //@Deprecated
    public ConcurrentHashMap<String, Object> getAllConfig() {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        return config;
    }

    /**
     * ?(0xFE)QNC I_version I_size I_RAW_reserved 16_md5 DATA
     */
    public void reload() throws IOException {
        synchronized (this) {
            FileInputStream fin;
            fin = new FileInputStream(file);
            if (fin.available() == 0) return;
            config.clear();
            DataInputStream in = new DataInputStream(fin);
            in.skip(4);//flag
            int endian = in.readInt();
            int file_size = in.readInt();
            readIRaw(in);//ignore
            byte[] md5 = new byte[16];
            if (in.read(md5, 0, 16) < 16) throw new IOException("Failed to read md5");
            String key;
            a:
            while (in.available() > 0) {
                int _type = in.read();
                if (_type < 0 || _type > 255)
                    throw new IOException("Unexpected type:" + _type + ",version:" + endian);
                key = readIStr(in);
                switch ((byte) _type) {
                    case TYPE_VOID:
                        log(new RuntimeException("ConcurrentHashMap/reload: replace null with " + VOID_INSTANCE + " in [key=\"" + key + "\",type=TYPE_VOID] at " + file.getAbsolutePath()));
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
                        throw new IOException("Unexpected type:" + _type + ",name:\"" + key + "\",version:" + endian);
                }
            }
            dirty = false;
        }
    }

    //@Deprecated
    public void save() throws IOException {
        saveAndNotify(0);
    }

    public void saveAndNotify(int what) throws IOException {
        saveWithoutNotify();
        SyncUtils.onFileChanged(mFileTypeId, mTargetUin, what);
    }

    public void saveWithoutNotify() throws IOException {
        synchronized (this) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            Iterator<Map.Entry<String, Object>> it = config.entrySet().iterator();
            Map.Entry<String, Object> record;
            String fn;
            Object val;
            while (it.hasNext()) {
                record = it.next();
                fn = record.getKey();
                val = record.getValue();
                writeRecord(out, fn, val);
            }
            out.flush();
            out.close();
            baos.close();
            byte[] dat = baos.toByteArray();
            byte[] md5;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(dat);
                md5 = md.digest();
            } catch (NoSuchAlgorithmException e) {
                md5 = new byte[16];
            }
            FileOutputStream fout = new FileOutputStream(file);
            out = new DataOutputStream(fout);
            out.write(new byte[]{(byte) 0xFE, 'Q', 'N', 'C'});
            out.writeInt(BYTE_ORDER_STUB);
            out.writeInt(dat.length);
            out.writeInt(0);//reserved
            out.write(md5, 0, 16);
            out.write(dat, 0, dat.length);
            out.flush();
            fout.flush();
            out.close();
            fout.close();
            dirty = false;
        }
    }

    public long getLongOrDefault(String key, long i) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        if (!config.containsKey(key)) {
            return i;
        }
        try {
            return ((Long) config.get(key)).longValue();
        } catch (ClassCastException e) {
            return i;
        }
    }

    public void putBoolean(String key, boolean v) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        config.put(key, v);
    }

    public void putLong(String key, long v) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        config.put(key, v);
    }

    public void putObject(@NonNull String key, Object v) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        config.put(key, v);
    }

    @Nullable
    public Object remove(@NonNull String k) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        return config.remove(k);
    }

    @Nullable
    public boolean containsKey(@NonNull String k) {
        try {
            if (dirty) reload();
        } catch (Exception ignored) {
        }
        return config.containsKey(k);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean hasConfig(String name) {
        return containsKey(name);
    }

    @Override
    public boolean getBooleanConfig(String name) {
        return getBooleanOrDefault(name, false);
    }

    @Override
    public void setBooleanConfig(String name, boolean val) {
        putBoolean(name, val);
    }

    @Override
    public int getIntConfig(String name) {
        return getIntOrDefault(name, -1);
    }

    @Override
    public void setIntConfig(String name, int val) {
        putInt(name, val);
    }

    @Override
    public String getStringConfig(String name) {
        return getString(name);
    }

    @Override
    public void setStringConfig(String name, String val) {
        putString(name, val);
    }

    @Override
    public boolean sync() {
        try {
            save();
            return true;
        } catch (IOException e) {
            Utils.log(e);
            return false;
        }
    }
}
