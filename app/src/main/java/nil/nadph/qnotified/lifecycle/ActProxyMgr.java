/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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
package nil.nadph.qnotified.lifecycle;

import androidx.annotation.NonNull;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import org.intellij.lang.annotations.MagicConstant;

/**
 * This class is used to cope with Activity
 */
public class ActProxyMgr {

    public static final String STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";
    public static final String STUB_TRANSLUCENT_ACTIVITY = "cooperation.qlink.QlinkStandardDialogActivity";
    @MagicConstant
    public static final String ACTIVITY_PROXY_INTENT = "qn_act_proxy_intent";

    private ActProxyMgr() {
        throw new AssertionError("No instance for you!");
    }

    // NOTICE: ** If you have created your own package, add it to proguard-rules.pro.**

    public static boolean isModuleProxyActivity(@NonNull String className) {
        if (className == null) {
            return false;
        }
        return className.startsWith("nil.nadph.qnotified.")
            || className.startsWith("cc.ioctl.activity.")
            || className.startsWith("me.zpp0196.qqpurify.activity.")
            || className.startsWith("me.singleneuron.")
            || className.startsWith("me.ketal.ui.activity.")
            || className.startsWith("com.rymmmmm.activity.")
            || className.startsWith("cn.lliiooll.activity.")
            || className.startsWith("ltd.nextalone.activity.");
    }

    public static boolean isModuleBundleClassLoaderRequired(@NonNull String className) {
        if (!isModuleProxyActivity(className)) {
            return false;
        }
        try {
            Class<?> clazz = Class.forName(className);
            return !IphoneTitleBarActivityCompat.class.isAssignableFrom(clazz);
        } catch (ClassNotFoundException cnfe) {
            return false;
        }
    }
}
