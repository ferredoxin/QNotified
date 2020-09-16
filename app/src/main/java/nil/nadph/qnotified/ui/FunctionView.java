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
import android.view.View;


public class FunctionView extends View {

    private int mColor;

    private float mTextSize;//px

    public FunctionView(Context context) {
        super(context);
    }

    public void setTextColor(int color) {
        mColor = color;
        invalidate();
    }

    public void setTextSizePx(int size) {
        mTextSize = size;
        invalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //FIXME: too lazy, not implement
    }

    @Override
    protected void onDraw(Canvas canvas) {

    }
}
