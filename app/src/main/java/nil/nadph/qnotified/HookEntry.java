package nil.nadph.qnotified;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import nil.nadph.qnotified.util.Utils;

public class HookEntry implements IXposedHookLoadPackage {
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi";
    public static final String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        switch (lpparam.packageName) {
            case PACKAGE_NAME_SELF:
                XposedHelpers.findAndHookMethod("nil.nadph.qnotified.util.Utils", lpparam.classLoader, "getActiveModuleVersion", XC_MethodReplacement.returnConstant(Utils.QN_VERSION_NAME));
                break;
            case PACKAGE_NAME_QQ:
            case PACKAGE_NAME_TIM:
                StartupHook.getInstance().doInit(lpparam.classLoader);
                break;
            case PACKAGE_NAME_QQ_INTERNATIONAL:
            case PACKAGE_NAME_QQ_LITE:
                //coming...
        }
    }
}
