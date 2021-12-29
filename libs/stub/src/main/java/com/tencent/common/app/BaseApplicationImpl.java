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

package com.tencent.common.app;

import android.content.Intent;
import android.os.Bundle;
import mqq.app.AppActivity;
import mqq.app.AppRuntime;
import mqq.app.MobileQQ;

/* compiled from: ProGuard */
public class BaseApplicationImpl extends MobileQQ {

//    public static BaseApplicationImpl a() {
//        throw new RuntimeException("Stub!");
//    }

    public AppRuntime a() {
        throw new RuntimeException("Stub!");
    }

    public AppRuntime createRuntime(String str) {
        throw new RuntimeException("Stub!");
    }

    public int getAppId(String str) {
        throw new RuntimeException("Stub!");
    }

    public String getBootBroadcastName(String str) {
        if (str.equals("com.tencent.mobileqq")) {
            return "com.tencent.mobileqq.broadcast.qq";
        }
        if (str.equals("com.tencent.mobileqq:video")) {
            return "com.tencent.av.ui.VChatActivity";
        }
        return "";
    }

    public boolean isNeedMSF(String str) {
        throw new RuntimeException("Stub!");
    }

    public void onCreate() {
        super.onCreate();
        throw new RuntimeException("Stub!");
    }

    public boolean onActivityCreate(AppActivity appActivity, Intent intent) {
        throw new RuntimeException("Stub!");
    }

    public void onActivityFocusChanged(AppActivity appActivity, boolean z) {
        throw new RuntimeException("Stub!");
    }

    public void onRecver(String str) {
        throw new RuntimeException("Stub!");
    }

    public void startActivity(Intent intent) {
        throw new RuntimeException("Stub!");
    }

    public void startActivity(Intent intent, Bundle bundle) {
        throw new RuntimeException("Stub!");
    }
}
