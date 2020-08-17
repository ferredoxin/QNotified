package me.singleneuron.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

public class NewRoundHeadInternal {

    public static void hook(boolean isEnabled) throws Exception {
        Class roundHeadClass = Class.forName("bfsw");
        XposedHelpers.findAndHookMethod(roundHeadClass,"a",byte.class,new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (LicenseStatus.sDisableCommonHooks) return;
                if (!isEnabled) return;
                //Utils.logd("NewRoundHead Started");
                param.setResult((byte)param.args[0]);
            }
        });
    }

}
