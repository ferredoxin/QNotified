/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
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
        if (R.string.res_inject_success >>> 24 == 0x7f) {
            XposedBridge.log("package id must NOT be 0x7f, reject loading...");
            return;
        }
        //dumpProcessInfo(lpparam.isFirstApplication);
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

//    public static void dumpProcessInfo(boolean i) {
//        int pid = android.os.Process.myPid();
//        String name = "unknown";
//        BufferedReader cmdlineReader = null;
//        try {
//            cmdlineReader = new BufferedReader(new InputStreamReader(
//                    new FileInputStream(
//                            "/proc/" + pid + "/cmdline"),
//                    "iso-8859-1"));
//            int c;
//            StringBuilder processName = new StringBuilder();
//            while ((c = cmdlineReader.read()) > 0) {
//                processName.append((char) c);
//            }
//            name = processName.toString();
//            if (name.contains(":")) name = name.split(":")[1];
//            else name = "main";
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (cmdlineReader != null) {
//                try {
//                    cmdlineReader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        android.util.Log.i("Xposed", "doInit(i=" + i + ") called @ " + pid + ":" + name);
//        android.util.Log.i("QNdump", "doInit(i=" + i + ") called @ " + pid + ":" + name);
//    }
}
