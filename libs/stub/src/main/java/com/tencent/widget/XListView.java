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

package com.tencent.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class XListView extends ViewGroup {

    public XListView(Context context) {
        this(context, (AttributeSet) null);
    }

    public XListView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842868);
    }

    public XListView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        throw new RuntimeException("Stub!");
    }

    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        throw new RuntimeException("Stub!");
    }

    public void setOverScrollDistance(int i) {
        throw new RuntimeException("Stub!");
    }

    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        throw new RuntimeException("Stub!");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        throw new RuntimeException("Stub!");
    }

    public void setEnsureOverScrollStatusToIdleWhenRelease(boolean z) {
        throw new RuntimeException("Stub!");
    }

    public void setOverScrollHeight(int i2) {
        throw new RuntimeException("Stub!");
    }

    public void setAdapter(ListAdapter listAdapter) {
        throw new RuntimeException("Stub!");
    }

    public void setDivider(Drawable drawable) {
        throw new RuntimeException("Stub!");
    }
}
