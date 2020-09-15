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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import nil.nadph.qnotified.util.Utils;

public class DebugDrawable extends Drawable {
    Paint paint = new Paint();
    float[] sDebugLines;
    int i1, i8;

    public DebugDrawable(Context ctx) {
        i8 = Utils.dip2px(ctx, 8);
        i1 = Utils.dip2px(ctx, 1);
        paint.setAntiAlias(false);
    }

    private static void drawRectCorners(Canvas canvas, int x1, int y1, int x2, int y2, Paint paint,
                                        int lineLength, int lineWidth) {
        drawCorner(canvas, paint, x1, y1, lineLength, lineLength, lineWidth);
        drawCorner(canvas, paint, x1, y2, lineLength, -lineLength, lineWidth);
        drawCorner(canvas, paint, x2, y1, -lineLength, lineLength, lineWidth);
        drawCorner(canvas, paint, x2, y2, -lineLength, -lineLength, lineWidth);
    }

    private static void drawCorner(Canvas c, Paint paint, int x1, int y1, int dx, int dy, int lw) {
        fillRect(c, paint, x1, y1, x1 + dx, y1 + lw * sign(dy));
        fillRect(c, paint, x1, y1, x1 + lw * sign(dx), y1 + dy);
    }

    private static void fillRect(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2) {
        if (x1 != x2 && y1 != y2) {
            if (x1 > x2) {
                int tmp = x1;
                x1 = x2;
                x2 = tmp;
            }
            if (y1 > y2) {
                int tmp = y1;
                y1 = y2;
                y2 = tmp;
            }
            canvas.drawRect(x1, y1, x2, y2, paint);
        }
    }

    private static int sign(int x) {
        return (x >= 0) ? 1 : -1;
    }

    @Override
    public void draw(Canvas canvas) {
        // Draw optical bounds
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        drawRect(canvas, paint, 0, 0, getBounds().width() - 1, getBounds().height() - 1);
		/*
		 for (int i = 0; i < getChildCount(); i++) {
		 View c = getChildAt(i);
		 if (c.getVisibility() != View.GONE) {
		 Insets insets = c.getOpticalInsets();
		 *
		 drawRect(canvas, paint,
		 c.getLeft() + insets.left,
		 c.getTop() + insets.top,
		 c.getRight() - insets.right - 1,
		 c.getBottom() - insets.bottom - 1);
		 }
		 }
		 *
		 // Draw margins

		 paint.setColor(Color.argb(63, 255, 0, 255));
		 paint.setStyle(Paint.Style.FILL);

		 onDebugDrawMargins(canvas, paint);*/

        // Draw clip bounds

        paint.setColor(Color.rgb(63, 127, 255));
        paint.setStyle(Paint.Style.FILL);
        int lineLength = i8;
        int lineWidth = i1;
        drawRectCorners(canvas, 0, 0, getBounds().width(), getBounds().height(),/* c.getLeft(), c.getTop(), c.getRight(), c.getBottom(),*/
                paint, lineLength, lineWidth);
    }

    private void drawRect(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2) {
        if (sDebugLines == null) {
            sDebugLines = new float[16];
        }

        sDebugLines[0] = x1;
        sDebugLines[1] = y1;
        sDebugLines[2] = x2;
        sDebugLines[3] = y1;

        sDebugLines[4] = x2;
        sDebugLines[5] = y1;
        sDebugLines[6] = x2;
        sDebugLines[7] = y2;

        sDebugLines[8] = x2;
        sDebugLines[9] = y2;
        sDebugLines[10] = x1;
        sDebugLines[11] = y2;

        sDebugLines[12] = x1;
        sDebugLines[13] = y2;
        sDebugLines[14] = x1;
        sDebugLines[15] = y1;

        canvas.drawLines(sDebugLines, paint);
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
