/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.startup;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import me.singleneuron.util.HookStatue;
import nil.nadph.qnotified.R;

/**
 * Xposed entry class DO NOT MODIFY ANY CODE HERE UNLESS NECESSARY. DO NOT INVOKE ANY METHOD THAT
 * MAY GET IN TOUCH WITH KOTLIN HERE. DO NOT TOUCH ANDROIDX OR KOTLIN HERE, WHATEVER DIRECTLY OR
 * INDIRECTLY. THIS CLASS SHOULD ONLY CALL {@code StartupHook.getInstance().doInit()} AND RETURN
 * GRACEFULLY. OTHERWISE SOMETHING MAY HAPPEN BECAUSE OF A NON-STANDARD PLUGIN CLASSLOADER.
 *
 * @author kinit
 */
public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {

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
        switch (lpparam.packageName) {
            case PACKAGE_NAME_SELF: {
                XposedHelpers
                    .findAndHookMethod(HookStatue.class.getName(), lpparam.classLoader, "isEnabled",
                        XC_MethodReplacement.returnConstant(true));
                break;
            }
            case PACKAGE_NAME_TIM:
            case PACKAGE_NAME_QQ:
            case PACKAGE_NAME_QQ_LITE: {
                StartupHook.getInstance().doInit(lpparam.classLoader);
                break;
            }
            case PACKAGE_NAME_QQ_INTERNATIONAL: {
                //coming...
                break;
            }
            default:
                break;
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        StartupInfo.modulePath = startupParam.modulePath;
    }
}
