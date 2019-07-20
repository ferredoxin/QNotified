package nil.nadph.qnotified;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.*;

import android.app.*;
import android.os.*;
import android.content.*;
import java.io.*;

public class HookEntry implements IXposedHookLoadPackage{

	public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";//coming...
	public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        if(lpparam.packageName.equals(PACKAGE_NAME_SELF)){
			XposedHelpers.findAndHookMethod("nil.nadph.qnotified.Utils",lpparam.classLoader,"getActiveModuleVersion",XC_MethodReplacement.returnConstant(Utils.CURRENT_MODULE_VERSION));
        }else if(lpparam.packageName.equals(PACKAGE_NAME_QQ)){
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
        }
		/*if(lpparam.packageName.equals(PACKAGE_NAME_TIM)){
		 new TIMMainHook().handleLoadPackage(lpparam);
		 }*/
    }
}
