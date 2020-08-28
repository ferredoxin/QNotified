package nil.nadph.qnotified.hook.rikka;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.dialog.RikkaBaseApkFormatDialog;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

//重命名base.apk
public class BaseApk extends BaseDelayableHook {
    private static final BaseApk self = new BaseApk();
    private boolean isInit = false;

    public static BaseApk get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            final Class<?> _ItemManagerClz = Initiator.load("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item");
            for (Method m : Initiator._TroopFileUploadMgr().getDeclaredMethods()) {
                if (m.getName().equals("b") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(int.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 3 && argt[0] == long.class && argt[1] == _ItemManagerClz && argt[2] == Bundle.class) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) return;
                                if (!isEnabled()) return;
                                Object item = param.args[1];
                                Field localFile = XposedHelpers.findField(_ItemManagerClz, "LocalFile");
                                Field fileName = XposedHelpers.findField(_ItemManagerClz, "FileName");
                                if (fileName.get(item).equals("base.apk")) {
                                    PackageManager packageManager = Utils.getApplication().getPackageManager();
                                    PackageInfo packageArchiveInfo = packageManager.getPackageArchiveInfo((String) localFile.get(item), PackageManager.GET_ACTIVITIES);
                                    ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                    applicationInfo.sourceDir = (String) localFile.get(item);
                                    applicationInfo.publicSourceDir = (String) localFile.get(item);
                                    String format = RikkaBaseApkFormatDialog.getCurrentBaseApkFormat();
                                    if (format != null) {
                                        String result = format
                                                .replace("%n", applicationInfo.loadLabel(packageManager).toString())
                                                .replace("%p", applicationInfo.packageName)
                                                .replace("%v", packageArchiveInfo.versionName)
                                                .replace("%c", String.valueOf(Utils.getHostVersionCode()));
                                        fileName.set(item, result);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isEnabled() {
        return RikkaBaseApkFormatDialog.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //Unsupported.
    }
}
