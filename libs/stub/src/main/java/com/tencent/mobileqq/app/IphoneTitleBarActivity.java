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

import static android.view.Window.FEATURE_CUSTOM_TITLE;

import android.content.Intent;
import android.graphics.Paint;
import android.view.View;

@Deprecated
public class IphoneTitleBarActivity extends BaseActivity {

    public static final int LAYER_TYPE_SOFTWARE = 1;

    public static void setLayerType(View view) {
        if (view != null) {
            try {
                view.getClass().getMethod("setLayerType", Integer.TYPE, Paint.class)
                    .invoke(view, 0, null);
            } catch (Exception ignored) {
            }
        }
    }

    protected void requestWindowFeature(Intent intent) {
        requestWindowFeature(FEATURE_CUSTOM_TITLE);
    }

    public void setContentView(int i) {
        throw new RuntimeException("Stub!");
    }

    public void setContentView(View view) {
        throw new RuntimeException("Stub!");
    }

    public void setTitle(CharSequence charSequence) {
        throw new RuntimeException("Stub!");
    }

    public void setTitle(CharSequence charSequence, String str) {
        throw new RuntimeException("Stub!");
    }

    public String getTextTitle() {
        throw new RuntimeException("Stub!");
    }

    public void setLeftViewName(Intent intent) {
        throw new RuntimeException("Stub!");
    }

    public void setLeftViewName(int i) {
        throw new RuntimeException("Stub!");
    }

    public void setLeftButton(int i, View.OnClickListener onClickListener) {
        throw new RuntimeException("Stub!");
    }

    public void setRightButton(int i, View.OnClickListener onClickListener) {
        throw new RuntimeException("Stub!");
    }

    @Deprecated
    public View getRightTextView() {
        throw new RuntimeException("Stub!");
    }

    public void removeWebViewLayerType() {
        throw new RuntimeException("Stub!");
    }
}
