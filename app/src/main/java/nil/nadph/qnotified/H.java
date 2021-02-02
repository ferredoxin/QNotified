/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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

import android.app.Application;

import androidx.annotation.NonNull;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;

/**
 * Helper class for getting host information.
 * Keep it as simple as possible.
 */
public class H {

    private H() {
        throw new AssertionError("No instance for you!");
    }

    @NonNull
    public static Application getApplication() {
        return HostInformationProviderKt.getHostInformationProvider().getApplicationContext();
    }

    @NonNull
    public static String getPackageName() {
        return HostInformationProviderKt.getHostInformationProvider().getPackageName();
    }

    @NonNull
    public static String getAppName() {
        return HostInformationProviderKt.getHostInformationProvider().getHostName();
    }

    @NonNull
    public static String getVersionName() {
        return HostInformationProviderKt.getHostInformationProvider().getVersionName();
    }

    public static int getVersionCode() {
        return HostInformationProviderKt.getHostInformationProvider().getVersionCode32();
    }

    public static long getLongVersionCode() {
        return HostInformationProviderKt.getHostInformationProvider().getVersionCode();
    }

    public static boolean isTIM() {
        return HostInformationProviderKt.getHostInformationProvider().isTim();
    }

    public static boolean isQQ() {
        //Improve this method when supporting more clients.
        return !HostInformationProviderKt.getHostInformationProvider().isTim();
    }
}
