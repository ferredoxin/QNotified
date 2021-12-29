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

package mqq.app;

import android.app.Activity;
import android.os.Bundle;

public class BaseActivity extends Activity {

    protected static int sResumeCount = 0;
    protected boolean mIsShadow;
    private AppRuntime app;
    private boolean isResume;

    protected boolean isShadow() {
        return false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        onCreateNoRuntime(savedInstanceState);
        if (!isLatecyWaitRuntime()) {
            waitAppRuntime();
        }
        super.onCreate(savedInstanceState);
    }

    protected boolean isLatecyWaitRuntime() {
        return false;
    }

    protected void onCreateNoRuntime(Bundle savedInstanceState) {
        this.mIsShadow = isShadow();
        if (!this.mIsShadow) {
            super.onCreate(savedInstanceState);
        }
        throw new RuntimeException("Stub!");
    }

    public AppRuntime waitAppRuntime() {
        throw new RuntimeException("Stub!");
    }

    protected void onStart() {
        super.onStart();
        throw new RuntimeException("Stub!");
    }

    protected void onStop() {
        if (!this.mIsShadow) {
            super.onStop();
        }
        throw new RuntimeException("Stub!");
    }

    protected void onResume() {
        if (!this.mIsShadow) {
            super.onResume();
        }
        int i = sResumeCount + 1;
        sResumeCount = i;
        if (i > 0 && this.app != null) {
            this.app.isBackground_Pause = false;
        }
        this.isResume = true;
    }

    protected void onPause() {
        if (!this.mIsShadow) {
            super.onPause();
        }
        int i = sResumeCount - 1;
        sResumeCount = i;
        if (i <= 0 && this.app != null) {
            this.app.isBackground_Pause = true;
        }
        this.isResume = false;
    }

    protected void onDestroy() {
        if (!this.mIsShadow) {
            super.onDestroy();
        }
        throw new RuntimeException("Stub!");
    }

    public final AppRuntime getAppRuntime() {
        return this.app;
    }

    void setAppRuntime(AppRuntime app2) {
        this.app = app2;
    }

    protected void finalize() throws Throwable {
        super.finalize();
        throw new RuntimeException("Stub!");
    }

    protected void onAccountChanged() {
    }

    protected void onAccoutChangeFailed() {
    }

    protected void onLogout(Constants.LogoutReason reason) {
        finish();
    }

    public final void superFinish() {
        super.finish();
    }

    public final boolean isResume() {
        return this.isResume;
    }
}
