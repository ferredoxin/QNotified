package nil.nadph.qnotified;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage {

    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi";
    public static final String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";


    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals(PACKAGE_NAME_SELF)) {
            XposedHelpers.findAndHookMethod("nil.nadph.qnotified.Utils", lpparam.classLoader, "getActiveModuleVersion", XC_MethodReplacement.returnConstant(Utils.QN_VERSION_NAME));
        } else if (lpparam.packageName.equals(PACKAGE_NAME_QQ)) {
            //log("Found QQ!");
			/*XposedHelpers.findAndHookMethod(Activity.class.getName(),lpparam.classLoader,"onCreate","android.os.Bundle",new XC_MethodHook(0){
			 protected void afterHookedMethod(MethodHookParam param) throws Throwable{
			 log("QQ/Initing ClzExp...");
			 ClazzExplorer.get().init((Activity)param.thisObject);
			 log("QQ/Init clzExp done.");
			 }
			 });
			 */
            new QQMainHook().handleLoadPackage(lpparam);
            //log("Handle QQ done.");
        } else if (lpparam.packageName.equals(PACKAGE_NAME_TIM)) {
            new QQMainHook().handleLoadPackage(lpparam);
        } else if (lpparam.packageName.equals(PACKAGE_NAME_QQ_LITE)) {
            new QQMainHook().handleLoadPackage(lpparam);
        } else if (lpparam.packageName.equals(PACKAGE_NAME_QQ_INTERNATIONAL)) {
            new QQMainHook().handleLoadPackage(lpparam);
        }
    }
}
