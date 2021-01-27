package nil.nadph.qnotified.hook;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

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
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
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
