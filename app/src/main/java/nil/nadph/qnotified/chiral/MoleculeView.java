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
package nil.nadph.qnotified.chiral;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.text.BoringLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;


public class MoleculeView extends View {

    protected Molecule molecule;
    protected int mGravity = Gravity.CENTER;
    protected int mPaddingLeft, mPaddingRight, mPaddingTop, mPaddingBottom;
    protected float textSize;
    private int textColor = 0xFF000000;
    protected Paint paint = new Paint();
    protected float scaleFactor = 1;
    private Rect mViewRect = new Rect(), mDrawRect = new Rect();

    private float[] labelWidth;
    private float labelHeight;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (molecule != null && molecule.atomCount() > 0) {
            if (scaleFactor == 0) calcScaleFactor(getWidth(), getHeight());
            mViewRect.set(0, 0, getWidth(), getHeight());
            Gravity.apply(mGravity, (int) (scaleFactor * molecule.rangeX() + textSize),
                    (int) (scaleFactor * molecule.rangeY() + textSize), mViewRect, mDrawRect);
            float dx = mDrawRect.left + textSize / 2;
            float dy = mDrawRect.top + textSize / 2;
            float mx = molecule.minX();
            float my = molecule.minY();
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            if (labelWidth == null || labelWidth.length < molecule.atomCount()) {
                labelWidth = new float[molecule.atomCount()];
            }
            paint.setTextAlign(Paint.Align.CENTER);
            Molecule.Atom atom, p1, p2;
            Molecule.Bond bond;
            for (int i = 0; i < molecule.atomCount(); i++) {
                atom = molecule.getAtom(i + 1);
                labelWidth[i] = paint.measureText(atom.element);
                canvas.drawText(atom.element, dx + scaleFactor * (atom.x - mx), dy + scaleFactor * (atom.y - my) + distance, paint);
            }
            for (int i = 0; i < molecule.bondCount(); i++) {
                bond = molecule.getBond(i + 1);
                p1 = molecule.getAtom(bond.from);
                p2 = molecule.getAtom(bond.to);
                drawBond(canvas, dx + scaleFactor * (p1.x - mx), dy + scaleFactor * (p1.y - my), dx + scaleFactor * (p2.x - mx),
                        dy + scaleFactor * (p2.y - my), labelHeight, labelWidth[bond.from - 1], labelWidth[bond.to - 1], bond.type);
            }
        }
    }

    private void drawBond(Canvas canvas, float x1, float y1, float x2, float y2, float height, float w1, float w2, int type) {
        float[] ret = new float[2];
        float rad = (float) Math.atan2(y2 - y1, x2 - x1);
        calcLinePointConfined(x1, y1, x2, y2, w1, height, ret);
        float basex1 = ret[0];
        float basey1 = ret[1];
        calcLinePointConfined(x2, y2, x1, y1, w2, height, ret);
        float delta = textSize / 6;
        float basex2 = ret[0];
        float basey2 = ret[1];
        float dx = (float) (Math.sin(rad) * delta);
        float dy = (float) (Math.cos(rad) * delta);
        switch (type) {
            case 1:
                canvas.drawLine(basex1, basey1, basex2, basey2, paint);
                break;
            case 2:
                canvas.drawLine(basex1 + dx / 2, basey1 + dy / 2, basex2 + dx / 2, basey2 + dy / 2, paint);
                canvas.drawLine(basex1 - dx / 2, basey1 - dy / 2, basex2 - dx / 2, basey2 - dy / 2, paint);
                break;
            case 3:
                canvas.drawLine(basex1, basey1, basex2, basey2, paint);
                canvas.drawLine(basex1 + dx, basey1 + dy, basex2 + dx, basey2 + dy, paint);
                canvas.drawLine(basex1 - dx, basey1 - dy, basex2 - dx, basey2 - dy, paint);
                break;
        }
    }

    public static void calcLinePointConfined(float x, float y, float x2, float y2, float w, float h, float[] out) {
        float k = (float) Math.atan2(h, w);
        float sigx = Math.signum(x2 - x);
        float sigy = Math.signum(y2 - y);
        float absRad = (float) Math.atan2(Math.abs(y2 - y), Math.abs(x2 - x));
        if (absRad > k) {
            out[0] = (float) (x + sigx * h / 2 / Math.tan(absRad));
            out[1] = y + sigy * h / 2;
        } else {
            out[0] = x + sigx * w / 2;
            out[1] = (float) (y + sigy * w / 2 * Math.tan(absRad));
        }
    }

    public void setTextSize(float size) {
        setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setTextSize(int unit, float size) {
        Context c = getContext();
        Resources r;
        if (c == null) {
            r = Resources.getSystem();
        } else {
            r = c.getResources();
        }
        textSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        paint.setTextSize(textSize);
        invalidate();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setMolecule(Molecule molecule) {
        this.molecule = molecule;
        calcScaleFactor(getWidth(), getHeight());
        invalidate();
    }

    public Molecule getMolecule() {
        return molecule;
    }

    private void calcScaleFactor(int width, int height) {
        if (width * height == 0) return;
        if (molecule == null) {
            scaleFactor = 0;
            return;
        }
        width -= mPaddingLeft + mPaddingRight + textSize;
        height -= mPaddingTop + mPaddingBottom + textSize;
        float rx = molecule.rangeX();
        float ry = molecule.rangeY();
        if (rx * ry == 0) {
            if (rx == ry) {
                scaleFactor = 1;
            } else if (rx != 0) {
                scaleFactor = width / rx;
            } else {
                scaleFactor = height / ry;
            }
        } else {
            scaleFactor = Math.min(width / rx, height / ry);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        boolean fromexisting = false;
        final float widthLimit = (widthMode == MeasureSpec.AT_MOST)
                ? (float) widthSize : Float.MAX_VALUE;

        if (widthMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            width = widthSize;
        } else {


        }

        int want = width - getCompoundPaddingLeft() - getCompoundPaddingRight();
        int unpaddedWidth = want;

        if (mHorizontallyScrolling) want = VERY_WIDE;

        int hintWant = want;
        int hintWidth = (mHintLayout == null) ? hintWant : mHintLayout.getWidth();

        if (mLayout == null) {
            makeNewLayout(want, hintWant, boring, hintBoring,
                    width - getCompoundPaddingLeft() - getCompoundPaddingRight(), false);
        } else {
            final boolean layoutChanged = (mLayout.getWidth() != want) || (hintWidth != hintWant)
                    || (mLayout.getEllipsizedWidth()
                    != width - getCompoundPaddingLeft() - getCompoundPaddingRight());

            final boolean widthChanged = (mHint == null) && (mEllipsize == null)
                    && (want > mLayout.getWidth())
                    && (mLayout instanceof BoringLayout
                    || (fromexisting && des >= 0 && des <= want));

            final boolean maximumChanged = (mMaxMode != mOldMaxMode) || (mMaximum != mOldMaximum);

            if (layoutChanged || maximumChanged) {
                if (!maximumChanged && widthChanged) {
                    mLayout.increaseWidthTo(want);
                } else {
                    makeNewLayout(want, hintWant, boring, hintBoring,
                            width - getCompoundPaddingLeft() - getCompoundPaddingRight(), false);
                }
            } else {
                // Nothing has changed
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            // Parent has told us how big to be. So be it.
            height = heightSize;
            mDesiredHeightAtMeasure = -1;
        } else {
            int desired = getDesiredHeight();

            height = desired;
            mDesiredHeightAtMeasure = desired;

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(desired, heightSize);
            }
        }

        int unpaddedHeight = height - getCompoundPaddingTop() - getCompoundPaddingBottom();
        if (mMaxMode == LINES && mLayout.getLineCount() > mMaximum) {
            unpaddedHeight = Math.min(unpaddedHeight, mLayout.getLineTop(mMaximum));
        }


        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calcScaleFactor(w, h);
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        mPaddingLeft = left;
        mPaddingRight = right;
        mPaddingTop = top;
        mPaddingBottom = bottom;
        super.setPadding(left, top, right, bottom);
    }

    public void setGravity(int gravity) {
        if ((gravity & Gravity.RELATIVE_HORIZONTAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.START;
        }
        if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
            gravity |= Gravity.TOP;
        }
        if (gravity != mGravity) {
            invalidate();
        }
        mGravity = gravity;
    }

    public int getGravity() {
        return mGravity;
    }

    private void initInternal(Context ctx) {
        setTextSize(18);
    }

    public MoleculeView(Context context) {
        super(context);
        initInternal(context);
    }

    public MoleculeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInternal(context);
    }

    public MoleculeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInternal(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MoleculeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initInternal(context);
    }
}
