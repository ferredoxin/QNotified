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

import static nil.nadph.qnotified.util.Utils.log;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

abstract public class TouchEventToLongClickAdapter implements View.OnTouchListener,
    View.OnLongClickListener, Runnable {

    private long mDownTime = -1;
    private float mX, mY;
    private int THRESHOLD = 500;

    private View val$mView;

    {
        try {
            THRESHOLD = ViewConfiguration.getLongPressTimeout();
        } catch (Throwable e) {
            log(e);
        }
    }

    public TouchEventToLongClickAdapter setLongPressTimeout(int ms) {
        this.THRESHOLD = ms;
        return this;
    }

    public TouchEventToLongClickAdapter setLongPressTimeoutFactor(float f) {
        this.THRESHOLD *= f;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x, y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                mX = event.getX();
                mY = event.getY();
                val$mView = v;
                v.removeCallbacks(this);
                v.postDelayed(this, THRESHOLD);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                if (x < 0 || y < 0 || x > v.getWidth() || y > v.getHeight()) {
                    mDownTime = -1;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mDownTime = -1;
                break;
        }
        return false;
    }

    @Override
    public void run() {
        if (mDownTime < 0) {
            return;
        }
        long curr = System.currentTimeMillis();
        if (curr - mDownTime > THRESHOLD) {
            mDownTime = -1;
            onLongClick(val$mView);
        }
    }
}

