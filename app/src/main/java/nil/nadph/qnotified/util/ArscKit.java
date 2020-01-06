package nil.nadph.qnotified.util;

import android.content.Context;
import nil.nadph.qnotified.record.ConfigManager;

import java.io.IOException;

import static nil.nadph.qnotified.util.Utils.log;

public class ArscKit {

    private static final String CACHED_RES_ID_NAME_PREFIX = "cached_res_id_name_";
    private static final String CACHED_RES_ID_CODE_PREFIX = "cached_res_id_code_";


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
        } catch (Exception ignored) {
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
        ret = searchForResId(ctx, type, name);
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

    private static int searchForResId(Context ctx, String str, String name) {

    }

}
