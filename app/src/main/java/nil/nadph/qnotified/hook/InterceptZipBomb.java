package nil.nadph.qnotified.hook;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

public class InterceptZipBomb extends CommonDelayableHook {
    public static final InterceptZipBomb INSTANCE = new InterceptZipBomb();

    private InterceptZipBomb() {
        super("bug_intercept_zip_bomb", SyncUtils.PROC_MAIN, true, new DexDeobfStep(DexKit.C_ZipUtils_biz));
    }

    @Override
    protected boolean initOnce() {
        try {
            XposedBridge.hookMethod(DexKit.doFindClass(DexKit.C_ZipUtils_biz)
                    .getMethod("a", File.class, String.class), new XC_MethodHook(51) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    File file = (File) param.args[0];
                    ZipFile zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    long sizeSum = 0;
                    while (entries.hasMoreElements()) {
                        sizeSum += entries.nextElement().getSize();
                    }
                    zipFile.close();
                    if (sizeSum >= 104550400) {
                        param.setResult(null);
                        Toasts.show(Utils.getApplication(), String.format("已拦截 %s ,解压后大小异常: %s", file.getPath(), getSizeString(sizeSum)));
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    /**
     * From com.bug.zqq
     *
     * @param size in bytes
     * @return A human readable string for the file size
     */
    @NonNull
    public static String getSizeString(long size) {
        if (size < 0) {
            return "0B";
        }
        if (size < 1024) {
            return size + "B";
        }
        LinkedHashMap<Long, String> map = new LinkedHashMap<>();
        map.put(1152921504606846976L, "EB");
        map.put(1125899906842624L, "PB");
        map.put(1099511627776L, "TB");
        map.put(1073741824L, "GB");
        map.put(1048576L, "MB");
        map.put(1024L, "KB");
        for (Map.Entry<Long, String> entry : map.entrySet()) {
            long longValue = (Long) entry.getKey();
            String str = (String) entry.getValue();
            if (size >= longValue) {
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
