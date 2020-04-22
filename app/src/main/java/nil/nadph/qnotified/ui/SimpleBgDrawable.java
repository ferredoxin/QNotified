/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class SimpleBgDrawable extends Drawable {
    private int iColor;
    private int iEdgeColor;
    private int iEdgeWidth;
    private Paint mPaint;

    public SimpleBgDrawable(int color, int edgeColor, int edgeWidth) {
        iColor = color;
        iEdgeColor = edgeColor;
        iEdgeWidth = edgeWidth;
        mPaint = new Paint();
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        int i = iEdgeWidth;
        int w = getBounds().width();
        int h = getBounds().height();
        if (iEdgeWidth > 0) {
            mPaint.setColor(iEdgeColor);
            canvas.drawRect(0, 0, w, i, mPaint);
            canvas.drawRect(0, h - i, w, h, mPaint);
            canvas.drawRect(0, i, i, h - i, mPaint);
            canvas.drawRect(w - i, i, w, h - i, mPaint);
        }
        mPaint.setColor(iColor);
        canvas.drawRect(i, i, w - i, h - i, mPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        //throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        //throw new UnsupportedOperationException("Stub!");
    }

    @Override
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }

}
