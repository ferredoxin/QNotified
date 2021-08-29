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

package nil.nadph.qnotified.util;

import android.os.Looper;
import java.util.Locale;
import nil.nadph.qnotified.BuildConfig;

public class StrictCheckMode {

    private static final boolean sStrictModeEnabled = BuildConfig.DEBUG;

    public static boolean isOnUiThread() {
        return Looper.getMainLooper() == Looper.myLooper();
    }

    public static boolean warnStrictModeIfUiThread() {
        if (sStrictModeEnabled && isOnUiThread()) {
            Throwable t = new Throwable("Blocking UI Thread detected in strict mode");
            Utils.log(t);
            StackTraceElement caller = t.getStackTrace()[2];
            Toasts.show(null, "Warning: blocking UI Thread detected in strict mode\n"
                    + String.format(Locale.ROOT, "%s.%s(%s:%d)",
                caller.getClassName(), caller.getMethodName(), caller.getFileName(), caller.getLineNumber()),
                Toasts.LENGTH_LONG);
            return true;
        }
        return false;
    }
}
