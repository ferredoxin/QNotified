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
package nil.nadph.qnotified.ui.drawable;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class HcbBackgroundDrawable extends Drawable {

    private final Paint mPaint;
    private int color;

    public HcbBackgroundDrawable(int c) {
        mPaint = new Paint();
        color = c;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        int w = getBounds().width();
        int h = getBounds().height();
        canvas.drawColor(color);
        mPaint.setStrokeWidth(0);
        mPaint.setAntiAlias(false);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0.5f, 0.5f, w - 1.5f, 0.5f, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1.5f, 1.5f, w - 0.5f, 1.5f, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0.5f, 0.5f, 0.5f, h - 1.5f, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1.5f, 1.5f, 1.5f, h - 0.5f, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(w - 1.5f, 0.5f, w - 1.5f, h - 1.5f, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(w - 0.5f, 1.5f, w - 0.5f, h - 0.5f, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0.5f, h - 1.5f, w - 1.5f, h - 1.5f, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1.5f, h - 0.5f, w - 0.5f, h - 0.5f, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }
}
