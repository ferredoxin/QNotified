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
package cc.ioctl;

import android.app.Application;
import androidx.annotation.NonNull;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.util.Utils;

/**
 * Helper class for getting host information. Keep it as simple as possible.
 */
public class H {

    private H() {
        throw new AssertionError("No instance for you!");
    }

    @NonNull
    public static Application getApplication() {
        return HostInfo.getHostInfo().getApplication();
    }

    @NonNull
    public static String getPackageName() {
        return HostInfo.getHostInfo().getPackageName();
    }

    @NonNull
    public static String getAppName() {
        return HostInfo.getHostInfo().getHostName();
    }

    @NonNull
    public static String getVersionName() {
        return HostInfo.getHostInfo().getVersionName();
    }

    public static int getVersionCode() {
        return HostInfo.getHostInfo().getVersionCode32();
    }

    public static long getLongVersionCode() {
        return HostInfo.getHostInfo().getVersionCode();
    }

    public static boolean isTIM() {
        return HostInfo.isTim();
    }

    public static boolean isQQLite() {
        return Utils.PACKAGE_NAME_QQ_LITE.equals(getPackageName());
    }

    public static boolean isPlayQQ() {
        return !HostInfo.isPlayQQ();
    }

    public static boolean isQQ() {
        //Improve this method when supporting more clients.
        return !HostInfo.isTim();
    }
}
