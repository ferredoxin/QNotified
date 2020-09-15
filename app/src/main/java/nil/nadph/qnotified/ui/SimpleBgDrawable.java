/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.ui;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class SimpleBgDrawable extends Drawable {
    private final int iColor;
    private final int iEdgeColor;
    private final int iEdgeWidth;
    private final Paint mPaint;

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
    @SuppressWarnings("deprecation")
    public int getOpacity() {
        return android.graphics.PixelFormat.TRANSLUCENT;
    }

}
