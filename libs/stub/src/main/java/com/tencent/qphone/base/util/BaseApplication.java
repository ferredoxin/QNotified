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

package com.tencent.qphone.base.util;

import android.app.Application;
import android.content.Context;
import java.util.ArrayList;

public abstract class BaseApplication extends Application {

    public static int appnewavmsgicon = 0;
    public static int appnewmsgicon = 0;
    public static int defaultNotifSoundResourceId = 0;
    public static int devlockQuickloginIcon = 0;
    public static ArrayList exclusiveStreamList = new ArrayList();
    public static int qqlaunchicon = 0;
    public static int qqwifiicon = 0;
    static Context context;

    public static Context getContext() {
        return context;
    }

    public static int getQQNewMsgIcon() {
        return appnewmsgicon;
    }

    public static int getQQNewAVMsgIcon() {
        return appnewavmsgicon;
    }

    public static int getQQLaunchIcon() {
        return qqlaunchicon;
    }

    public static int getQQWiFiIcon() {
        return qqwifiicon;
    }

    public static int getDefaultNotifSoundResourceId() {
        return defaultNotifSoundResourceId;
    }

    public static int getDevlockQuickloginIcon() {
        return devlockQuickloginIcon;
    }

    public void onCreate() {
        super.onCreate();
        context = this;
    }

}
