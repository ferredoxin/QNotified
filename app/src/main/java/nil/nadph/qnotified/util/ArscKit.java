package nil.nadph.qnotified.util;

import android.content.Context;
import nil.nadph.qnotified.record.ConfigManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

import static nil.nadph.qnotified.util.Utils.log;

public class ArscKit {

    private static final String CACHED_RES_ID_NAME_PREFIX = "cached_res_id_name_";
    private static final String CACHED_RES_ID_CODE_PREFIX = "cached_res_id_code_";

    //FailureZero
    public static int getIdentifier(Context ctx, String type, String name, boolean allowSearch) {
        if (name == null) return 0;
        if (name.contains("@")) {
            String[] arr = name.split("@");
            name = arr[arr.length - 1];
        }
        if (type == null && name.contains("/")) {
            String[] arr = name.split("/");
            type = arr[0];
            name = arr[arr.length - 1];
        }
        try {
            return Integer.parseInt(name);
        } catch (NumberFormatException ignored) {
        }
        if (ctx == null) ctx = Utils.getApplication();
        String pkg = ctx.getPackageName();
        int ret = ctx.getResources().getIdentifier(name, type, pkg);
        if (ret != 0) return ret;
        //ResId is obfuscated, try to get it from cache.
        ConfigManager cfg = ConfigManager.getDefault();
        ret = cfg.getIntOrDefault(CACHED_RES_ID_NAME_PREFIX + type + "/" + name, 0);
        int oldcode = cfg.getIntOrDefault(CACHED_RES_ID_CODE_PREFIX + type + "/" + name, -1);
        int currcode = Utils.getHostVersionCode();
        if (ret != 0 && (oldcode == currcode)) {
            return ret;
        }
        //parse thr ARSC to find it.
        if (!allowSearch) return 0;
        ret = enumArsc(pkg, type, name);
        if (ret != 0) {
            cfg.getAllConfig().put(CACHED_RES_ID_NAME_PREFIX + type + "/" + name, ret);
            cfg.getAllConfig().put(CACHED_RES_ID_CODE_PREFIX + type + "/" + name, currcode);
            try {
                cfg.save();
            } catch (IOException e) {
                log(e);
            }
        }
        return ret;
    }

    private static int enumArsc(String pkgname, String type, String name) {
        Enumeration<URL> urls = null;
        try {
            urls = (Enumeration<URL>) Utils.invoke_virtual(Initiator.getClassLoader(), "findResources", "resources.arsc", String.class);
        } catch (Throwable e) {
            log(e);
        }
        if (urls == null) {
            log(new RuntimeException("Error! Enum<URL<resources.arsc>> == null, loader = " + Initiator.getClassLoader()));
            return 0;
        }
        InputStream in;
        byte[] buf = new byte[4096];
        byte[] content;
        int ret = 0;
        ArrayList<String> rets = new ArrayList<String>();
        while (urls.hasMoreElements()) {
            try {
                in = urls.nextElement().openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int ii;
                while ((ii = in.read(buf)) != -1) {
                    baos.write(buf, 0, ii);
                }
                in.close();
                content = baos.toByteArray();
                ret = seekInArsc(content, pkgname, type, name);
                if (ret != 0) return ret;
            } catch (IOException e) {
                log(e);
            }
        }
        //404
        return 0;
    }

    //FailureZero
    private static int seekInArsc(byte[] p, String pkgname, String type, String name) {
        throw new RuntimeException("Stub!");
    }


    public static int readLe32(byte[] xml, int pos) {
        return (xml[pos]) & 0xff | (xml[pos + 1] << 8) & 0x0000ff00 | (xml[pos + 2] << 16) & 0x00ff0000 | ((xml[pos + 3] << 24) & 0xff000000);
    }

    public static short readLe16(byte[] xml, int pos) {
        return (short) ((xml[pos]) & 0xff | (xml[pos + 1] << 8) & 0xff00);
    }

}
