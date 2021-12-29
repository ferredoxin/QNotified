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

package com.tencent.mobileqq.app;

import android.os.Bundle;
import com.tencent.common.app.AppInterface;
import com.tencent.common.app.BaseApplicationImpl;
import mqq.manager.Manager;

public class QQAppInterface extends AppInterface {

    public QQAppInterface(BaseApplicationImpl baseApplicationImpl, String str) {
        super(baseApplicationImpl, str);
    }

    public void start(boolean z2) {

    }

    protected void addManager(int i2, Manager manager) {

    }

    public void onRunningBackground(Bundle bundle) {
    }

    public Manager getManager(int i2) {
        throw new RuntimeException("Stub!");
    }

    public void onRunningForeground() {

    }

    protected Class[] getMessagePushServlets() {
        throw new RuntimeException("Stub!");
    }

    protected String[] getMessagePushSSOCommands() {
        throw new RuntimeException("Stub!");
    }

    protected boolean canAutoLogin(String str) {
        throw new RuntimeException("Stub!");
    }

    public void setAutoLogin(boolean z2) {

    }

    public void onDestroy() {
    }

    protected void finalize() {
        throw new RuntimeException("Stub!");
    }

    protected void userLogoutReleaseData() {
    }

    public void logout(boolean z2) {
    }

    public void onCreate(Bundle bundle) {

    }

}
