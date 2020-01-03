package nil.nadph.qnotified.util;

import android.util.Log;

public class BugCollector {

    public static void onThrowable(Throwable th) {
        try {
            long time = System.currentTimeMillis();
            String logstr = Log.getStackTraceString(th);
            int hash = logstr.hashCode();


        } catch (Throwable ignored) {
        }
    }

    public static void deobfCallback() {

    }
}
