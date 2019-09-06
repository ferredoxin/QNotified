package nil.nadph.qnotified.record;

import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.util.Utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static nil.nadph.qnotified.record.Table.*;

public class ConfigManager {
    private static ConfigManager SELF;
    private File file;
    private HashMap<String, Object> config;

    public ConfigManager(File f) throws IOException {
        file = f;
        if (!file.exists()) file.createNewFile();
        config = new HashMap<>();
        reload();
    }

    public static ConfigManager getDefault() throws IOException {
        if (SELF == null)
            SELF = new ConfigManager(new File(Utils.getApplication().getFilesDir().getAbsolutePath() + "/qnotified_config.dat"));
        return SELF;
    }

    public Object getOrDefault(String key, Object def) {
        if (!config.containsKey(key)) config.put(key, def);
        return config.get(key);
    }

    public boolean getBooleanOrFalse(String key) {
        if (!config.containsKey(key)) config.put(key, false);
        try {
            return ((Boolean) config.get(key)).booleanValue();
        } catch (ClassCastException e) {
            return false;
        }
    }

    public boolean getBooleanOrDefault(String key, boolean def) {
        if (!config.containsKey(key)) config.put(key, def);
        try {
            return ((Boolean) config.get(key)).booleanValue();
        } catch (ClassCastException e) {
            return def;
        }
    }

    public int getIntOrDefault(String key, int def) {
        if (!config.containsKey(key)) config.put(key, def);
        try {
            return ((Integer) config.get(key)).intValue();
        } catch (ClassCastException e) {
            return def;
        }
    }

    public String getString(String key) {
        return (String) config.get(key);
    }

    public void putString(String key, String val) {
        config.put(key, val);
    }

    public HashMap<String, Object> getAllConfig() {
        return config;
    }

    /**
     * ?(0xFE)QNC I_version I_size I_RAW_reserved 16_md5 DATA
     */
    public void reload() throws IOException {
        synchronized (this) {
            FileInputStream fin = null;
            fin = new FileInputStream(file);
            if (fin.available() == 0) return;
            config.clear();
            DataInputStream in = new DataInputStream(fin);
            in.skip(4);//flag
            int ver = in.readInt();
            int file_size = in.readInt();
            readIRaw(in);//ignore
            byte[] md5 = new byte[16];
            if (in.read(md5, 0, 16) < 16) throw new IOException("Failed to read md5");
            String key;
            a:
            while (in.available() > 0) {
                int _type = in.read();
                if (_type < 0 || _type > 255) throw new IOException("Unexpected type:" + _type + ",version:" + ver);
                key = readIStr(in);
                switch ((byte) _type) {
                    case TYPE_NULL:
                        config.put(key, null);
                        break;
                    case TYPE_BYTE:
                        config.put(key, (byte) in.read());
                        break;
                    case TYPE_BOOL:
                        config.put(key, in.read() != 0);
                        break;
                    case TYPE_CODEPOINT:
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
                    case TYPE_ISTR:
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
                        throw new IOException("Unexpected type:" + _type + ",name:\"" + key + "\",version:" + ver);
                }
            }
        }
    }


    public void save() throws IOException {
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
            out.writeInt(ExfriendManager._VERSION_CURRENT);//ver
            out.writeInt(dat.length);
            out.writeInt(0);//reserved
            out.write(md5, 0, 16);
            out.write(dat, 0, dat.length);
            out.flush();
            fout.flush();
            out.close();
            fout.close();
        }
    }

    public long getLongOrDefault(String key, long i) {
        if (!config.containsKey(key)) config.put(key, i);
        try {
            return ((Long) config.get(key)).longValue();
        } catch (ClassCastException e) {
            return i;
        }
    }
}
