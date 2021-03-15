package nil.nadph.qnotified.startup;

import android.util.Log;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.BuildConfig;

class LogUtil {

    static void loge(String str) {
        Log.e("QNdump", str);
        try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.e("Xposed", str);
            Log.e("EdXposed-Bridge", str);
        }
    }

    static void logd(String str) {
        if (BuildConfig.DEBUG) {
            try {
                Log.d("QNdump", str);
                XposedBridge.log(str);
            } catch (NoClassDefFoundError e) {
                Log.d("Xposed", str);
                Log.d("EdXposed-Bridge", str);
            }
        }
    }

    static void logi(String str) {
        try {
            Log.i("QNdump", str);
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.i("Xposed", str);
            Log.i("EdXposed-Bridge", str);
        }
    }

    static void logw(String str) {
        Log.i("QNdump", str);
        try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.w("Xposed", str);
            Log.w("EdXposed-Bridge", str);
        }
    }

    static void log(Throwable th) {
        if (th == null) {
            return;
        }
        String msg = Log.getStackTraceString(th);
        Log.e("QNdump", msg);
        try {
            XposedBridge.log(th);
        } catch (NoClassDefFoundError e) {
            Log.e("Xposed", msg);
            Log.e("EdXposed-Bridge", msg);
        }
    }

}
