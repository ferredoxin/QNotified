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
package nil.nadph.qnotified.ui;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class InterceptLayout extends LinearLayout {

    private OnTouchListener mTouchInterceptor = null;
    private OnKeyListener mKeyInterceptor = null;

    public InterceptLayout(Context context) {
        super(context);
    }

    public InterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static InterceptLayout setupRudely(View v) {
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = 0;
        ViewGroup.LayoutParams currlp = v.getLayoutParams();
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i) == v) {
                index = i;
                break;
            }
        }
        parent.removeView(v);
        InterceptLayout layout = new InterceptLayout(v.getContext());
        ViewGroup.LayoutParams lpOuter;
        LinearLayout.LayoutParams lpInner;
        if (currlp == null) {
            lpOuter = new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lpInner = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        } else if (currlp instanceof ViewGroup.MarginLayoutParams) {
            lpOuter = currlp;
            lpInner = new LayoutParams(currlp.width, currlp.height);
            lpInner.bottomMargin = ((MarginLayoutParams) currlp).bottomMargin;
            lpInner.topMargin = ((MarginLayoutParams) currlp).topMargin;
            lpInner.leftMargin = ((MarginLayoutParams) currlp).leftMargin;
            lpInner.rightMargin = ((MarginLayoutParams) currlp).rightMargin;
            ((MarginLayoutParams) currlp).bottomMargin = ((MarginLayoutParams) currlp).topMargin
                = ((MarginLayoutParams) currlp).leftMargin = ((MarginLayoutParams) currlp).rightMargin = 0;
            lpOuter.height = lpOuter.width = WRAP_CONTENT;
        } else {
            lpOuter = new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            lpInner = new LinearLayout.LayoutParams(currlp.width, currlp.height);
        }
        layout.addView(v, lpInner);
        parent.addView(layout, index, lpOuter);
        return layout;
    }

    public OnKeyListener getKeyInterceptor() {
        return mKeyInterceptor;
    }

    public void setKeyInterceptor(OnKeyListener mKeyInterceptor) {
        this.mKeyInterceptor = mKeyInterceptor;
    }

    public OnTouchListener getTouchInterceptor() {
        return mTouchInterceptor;
    }

    public void setTouchInterceptor(OnTouchListener mTouchInterceptor) {
        this.mTouchInterceptor = mTouchInterceptor;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchInterceptor != null && mTouchInterceptor.onTouch(this, ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mKeyInterceptor != null && mKeyInterceptor.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }
}
