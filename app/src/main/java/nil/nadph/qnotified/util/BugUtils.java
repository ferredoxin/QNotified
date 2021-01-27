package nil.nadph.qnotified.util;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utils stolen from com.bug.zqq
 *
 * @author BUG
 */
public class BugUtils {

    /**
     * @param size in bytes
     * @return A human readable string for the size
     */
    @NonNull
    public static String getSizeString(long size) {
        if (size < 0) {
            return "-1";
        }
        if (size < 1024) {
            return size + "B";
        }
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        map.put(1152921504606846976L, "EiB");
        map.put(1125899906842624L, "PiB");
        map.put(1099511627776L, "TiB");
        map.put(1073741824L, "GiB");
        map.put(1048576L, "MiB");
        map.put(1024L, "KiB");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long longValue = (Long) entry.getKey();
            String str = (String) entry.getValue();
            if (size >= longValue) {
                @SuppressLint("DefaultLocale")
                String format = String.format("%.2f", ((double) size) / ((double) longValue));
                int indexOf = format.indexOf(".00");
                if (indexOf != -1) {
                    return format.substring(0, indexOf) + str;
                }
                return format + str;
            }
        }
        return "0B";
    }
}
