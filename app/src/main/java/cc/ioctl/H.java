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
package cc.ioctl;

import android.app.Application;
import androidx.annotation.NonNull;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;

/**
 * Helper class for getting host information. Keep it as simple as possible.
 */
public class H {

    private H() {
        throw new AssertionError("No instance for you!");
    }

    @NonNull
    public static Application getApplication() {
        return HostInformationProviderKt.getHostInfo().getApplication();
    }

    @NonNull
    public static String getPackageName() {
        return HostInformationProviderKt.getHostInfo().getPackageName();
    }

    @NonNull
    public static String getAppName() {
        return HostInformationProviderKt.getHostInfo().getHostName();
    }

    @NonNull
    public static String getVersionName() {
        return HostInformationProviderKt.getHostInfo().getVersionName();
    }

    public static int getVersionCode() {
        return HostInformationProviderKt.getHostInfo().getVersionCode32();
    }

    public static long getLongVersionCode() {
        return HostInformationProviderKt.getHostInfo().getVersionCode();
    }

    public static boolean isTIM() {
        return HostInformationProviderKt.getHostInfo().isTim();
    }

    public static boolean isQQ() {
        //Improve this method when supporting more clients.
        return !HostInformationProviderKt.getHostInfo().isTim();
    }
}
