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

import android.util.Log;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.BuildConfig;

class LogUtil {

    static void loge(String str) {
        Log.e("QNdump", str);
        try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.e("Xposed", str);
            Log.e("EdXposed-Bridge", str);
        }
    }

    static void logd(String str) {
        if (BuildConfig.DEBUG) {
            try {
                Log.d("QNdump", str);
                XposedBridge.log(str);
            } catch (NoClassDefFoundError e) {
                Log.d("Xposed", str);
                Log.d("EdXposed-Bridge", str);
            }
        }
    }

    static void logi(String str) {
        try {
            Log.i("QNdump", str);
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.i("Xposed", str);
            Log.i("EdXposed-Bridge", str);
        }
    }

    static void logw(String str) {
        Log.i("QNdump", str);
        try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.w("Xposed", str);
            Log.w("EdXposed-Bridge", str);
        }
    }

    static void log(Throwable th) {
        if (th == null) {
            return;
        }
        String msg = Log.getStackTraceString(th);
        Log.e("QNdump", msg);
        try {
            XposedBridge.log(th);
        } catch (NoClassDefFoundError e) {
            Log.e("Xposed", msg);
            Log.e("EdXposed-Bridge", msg);
        }
    }

}
