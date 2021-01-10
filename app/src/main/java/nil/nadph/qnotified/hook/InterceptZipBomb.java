package nil.nadph.qnotified.hook;

import java.io.File;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.BugUtils;
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
                        Toasts.show(Utils.getApplication(), String.format("已拦截 %s ,解压后大小异常: %s",
                                file.getPath(), BugUtils.getSizeString(sizeSum)));
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }
}
