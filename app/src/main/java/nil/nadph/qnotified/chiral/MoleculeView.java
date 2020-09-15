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
package nil.nadph.qnotified.chiral;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.Iterator;

import nil.nadph.qnotified.util.IndexFrom;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;


public class MoleculeView extends View {

    protected Molecule molecule;
    protected int mGravity = Gravity.CENTER;
    protected float fontSize;
    protected boolean mAutoTextSize = true;
    private int textColor = 0xFF000000;
    protected Paint paint = new Paint();
    protected float scaleFactor = 1;
    private final Rect mViewRect = new Rect();
    private final Rect mDrawRect = new Rect();
    protected HashSet<Integer> selectedChiral = new HashSet<>();

    private float[] labelTop;
    private float[] labelLeft;
    private float[] labelRight;
    private float[] labelBottom;
    private final Rect mTmpRect = new Rect();


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (molecule != null && molecule.atomCount() > 0) {
            long begin = System.currentTimeMillis();
            if (scaleFactor == 0) calcScaleFactor(getWidth(), getHeight());
            mViewRect.set(0, 0, getWidth(), getHeight());
            Gravity.apply(mGravity, (int) (scaleFactor * molecule.rangeX() + fontSize * 2),
                    (int) (scaleFactor * molecule.rangeY() + fontSize * 2), mViewRect, mDrawRect);
            float dx = mDrawRect.left + fontSize;
            float dy = mDrawRect.bottom - fontSize;
            float mx = molecule.minX();
            float my = molecule.minY();
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float distance = (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            if (labelTop == null || labelTop.length < molecule.atomCount()) {
                labelTop = new float[molecule.atomCount()];
                labelBottom = new float[molecule.atomCount()];
                labelLeft = new float[molecule.atomCount()];
                labelRight = new float[molecule.atomCount()];
            }
            paint.setColor(textColor);
            paint.setStrokeWidth(fontSize / 12);
            paint.setTextAlign(Paint.Align.CENTER);
            Molecule.Atom atom, p1, p2;
            Molecule.Bond bond;
            for (int i = 0; i < molecule.atomCount(); i++) {
                atom = molecule.getAtom(i + 1);
                paint.setTextSize(fontSize);
                if (atom.element.equals("C") && atom.charge == 0 && atom.unpaired == 0 &&
                        (atom.showFlag & Molecule.SHOW_FLAG_EXPLICIT) == 0) {
                    labelLeft[i] = labelRight[i] = 0;
                    labelTop[i] = labelBottom[i] = 0;
                    if (selectedChiral.contains(i + 1)) {
                        paint.getTextBounds("*", 0, 1, mTmpRect);
                        float r = mTmpRect.height() / 4f + mTmpRect.width() / 4f;
                        float cx, cy;
                        if (atom.spareSpace == Molecule.DIRECTION_BOTTOM) {
                            cx = dx + scaleFactor * (atom.x - mx);
                            cy = dy - scaleFactor * (atom.y - my) + 2 * r;
                        } else if (atom.spareSpace == Molecule.DIRECTION_LEFT) {
                            cx = dx + scaleFactor * (atom.x - mx) - 2 * r;
                            cy = dy - scaleFactor * (atom.y - my);
                        } else if (atom.spareSpace == Molecule.DIRECTION_TOP) {
                            cx = dx + scaleFactor * (atom.x - mx);
                            cy = dy - scaleFactor * (atom.y - my) - 2 * r;
                        } else {//DIRECTION_RIGHT
                            cx = dx + scaleFactor * (atom.x - mx) + 2 * r;
                            cy = dy - scaleFactor * (atom.y - my);
                        }
                        canvas.drawText("*", cx, cy + distance, paint);
                    }
                } else {
                    labelLeft[i] = labelRight[i] = paint.measureText(atom.element) / 2;
                    labelTop[i] = (-fontMetrics.ascent) / 2;
                    labelBottom[i] = (fontMetrics.descent / 2 - fontMetrics.ascent) / 2;
                    canvas.drawText(atom.element, dx + scaleFactor * (atom.x - mx), dy - scaleFactor * (atom.y - my) + distance, paint);
                    if (selectedChiral.contains(i + 1)) {
                        float sWidth = paint.measureText("*");
                        canvas.drawText("*", dx + scaleFactor * (atom.x - mx) - labelLeft[i] - sWidth / 2f, dy - scaleFactor * (atom.y - my) + distance, paint);
                        labelLeft[i] += sWidth;
                    }
                    if (atom.charge != 0) {
                        int c = atom.charge;
                        String text;
                        if (c > 0) {
                            if (c == 1) text = "+";
                            else text = c + "+";
                        } else {
                            if (c == -1) text = "-";
                            else text = -c + "-";
                        }
                        paint.setTextSize(fontSize / 2);
                        float chgwidth = paint.measureText(text);
                        Paint.FontMetrics chgFontMetrics = paint.getFontMetrics();
                        float chgdis = (chgFontMetrics.bottom - chgFontMetrics.top) / 2 - chgFontMetrics.bottom;
                        canvas.drawText(text, dx + scaleFactor * (atom.x - mx) + labelRight[i] + chgwidth / 2,
                                dy - scaleFactor * (atom.y - my) + fontMetrics.top / 3 + chgdis, paint);
                    }
                    if (atom.hydrogenCount > 0) {
                        int hCount = atom.hydrogenCount;
                        float hNumWidth = 0;
                        if (hCount > 1) {
                            paint.setTextSize(fontSize / 2);
                            hNumWidth = paint.measureText("" + hCount);
                        }
                        paint.setTextSize(fontSize);
                        float hWidth = paint.measureText("H");
                        float hcx, hcy;
                        if (atom.spareSpace == Molecule.DIRECTION_BOTTOM) {
                            hcx = dx + scaleFactor * (atom.x - mx);
                            hcy = dy - scaleFactor * (atom.y - my) - fontMetrics.ascent;
                            labelBottom[i] += -fontMetrics.ascent;
                        } else if (atom.spareSpace == Molecule.DIRECTION_LEFT) {
                            hcx = dx + scaleFactor * (atom.x - mx) - labelLeft[i] - hWidth / 2 - hNumWidth;
                            labelLeft[i] += hWidth + hNumWidth / 2 * 2;
                            hcy = dy - scaleFactor * (atom.y - my);
                        } else if (atom.spareSpace == Molecule.DIRECTION_TOP) {
                            hcx = dx + scaleFactor * (atom.x - mx);
                            hcy = dy - scaleFactor * (atom.y - my) + fontMetrics.ascent;
                            labelTop[i] += -fontMetrics.ascent;
                        } else {//DIRECTION_RIGHT
                            hcx = dx + scaleFactor * (atom.x - mx) + labelRight[i] + hWidth / 2;
                            labelRight[i] += hWidth + hNumWidth / 2 * 2;
                            hcy = dy - scaleFactor * (atom.y - my);
                        }
                        canvas.drawText("H", hcx, hcy + distance, paint);
                        if (hCount > 1) {
                            paint.setTextSize(fontSize / 2);
                            canvas.drawText("" + hCount, hcx + hWidth / 2 + hNumWidth / 2, hcy - fontMetrics.top / 2, paint);
                        }
                    }
                }
            }
            for (int i = 0; i < molecule.bondCount(); i++) {
                bond = molecule.getBond(i + 1);
                p1 = molecule.getAtom(bond.from);
                p2 = molecule.getAtom(bond.to);
                drawBond(canvas, dx + scaleFactor * (p1.x - mx), dy - scaleFactor * (p1.y - my), dx + scaleFactor * (p2.x - mx),
                        dy - scaleFactor * (p2.y - my), bond.type, bond.from - 1, bond.to - 1);
            }
//            long delta = System.currentTimeMillis() - begin;
//            paint.setTextAlign(Paint.Align.LEFT);
//            paint.setTextSize(fontSize / 2);
//            canvas.drawText(delta + "ms", fontSize / 2, fontSize / 1.25f, paint);
//            paint.setTextSize(fontSize);
        }
    }

    private void drawBond(Canvas canvas, float x1, float y1, float x2, float y2, int type, @IndexFrom(0) int idx1, @IndexFrom(0) int idx2) {
        float[] ret = new float[2];
        float rad = (float) Math.atan2(y2 - y1, x2 - x1);
        calcLinePointConfined(x1, y1, x2, y2, labelLeft[idx1], labelRight[idx1], labelTop[idx1], labelBottom[idx1], ret);
        float basex1 = ret[0];
        float basey1 = ret[1];
        calcLinePointConfined(x2, y2, x1, y1, labelLeft[idx2], labelRight[idx2], labelTop[idx2], labelBottom[idx2], ret);
        float delta = fontSize / 6;
        float basex2 = ret[0];
        float basey2 = ret[1];
        float dx = (float) (Math.sin(rad) * delta);
        float dy = (float) (Math.cos(rad) * delta);
        switch (type) {
            case 1:
                canvas.drawLine(basex1, basey1, basex2, basey2, paint);
                break;
            case 2:
                canvas.drawLine(basex1 + dx / 2, basey1 - dy / 2, basex2 + dx / 2, basey2 - dy / 2, paint);
                canvas.drawLine(basex1 - dx / 2, basey1 + dy / 2, basex2 - dx / 2, basey2 + dy / 2, paint);
                break;
            case 3:
                canvas.drawLine(basex1, basey1, basex2, basey2, paint);
                canvas.drawLine(basex1 + dx, basey1 - dy, basex2 + dx, basey2 - dy, paint);
                canvas.drawLine(basex1 - dx, basey1 + dy, basex2 - dx, basey2 + dy, paint);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isClickable() && event.getAction() == MotionEvent.ACTION_UP && isPressed()) {
            float wx = (event.getX() - mDrawRect.left - fontSize) / scaleFactor + molecule.minX();
            float wy = (getHeight() - event.getY() - mDrawRect.top - fontSize) / scaleFactor + molecule.minY();
            int index = molecule.getAtomIndexNear(wx, wy, fontSize / scaleFactor);
            if (index > 0) {
                switchChiral(index);
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean switchChiral(@IndexFrom(1) int N) {
        boolean z;
        if (selectedChiral.contains(N)) {
            z = false;
            selectedChiral.remove(N);
        } else {
            z = true;
            selectedChiral.add(N);
        }
        invalidate();
        return z;
    }

    public int getSelectedChiralCount() {
        return selectedChiral.size();
    }

    public void unselectedAllChiral() {
        selectedChiral.clear();
        invalidate();
    }

    public void selectChiral(int N, boolean selected) {
        if (selected) selectedChiral.add(N);
        else selectedChiral.remove(N);
        invalidate();
    }

    public int[] getSelectedChiral() {
        int[] ret = new int[selectedChiral.size()];
        Iterator<Integer> it = selectedChiral.iterator();
        for (int i = 0; i < ret.length; i++) {
            if (it.hasNext()) ret[i] = it.next();
        }
        return ret;
    }

    public void setSelectedChiral(@Nullable int[] chiral) {
        selectedChiral.clear();
        if (chiral != null) {
            for (int i : chiral) {
                selectedChiral.add(i);
            }
        }
        invalidate();
    }

    public static void calcLinePointConfined(float x, float y, float x2, float y2, float left, float right, float top, float bottom, float[] out) {
        float w = x2 > x ? right : left;
        float h = y2 < y ? top : bottom;
        float k = (float) Math.atan2(h, w);
        float sigx = Math.signum(x2 - x);
        float sigy = Math.signum(y2 - y);
        float absRad = (float) Math.atan2(Math.abs(y2 - y), Math.abs(x2 - x));
        if (absRad > k) {
            out[0] = (float) (x + sigx * h / Math.tan(absRad));
            out[1] = y + sigy * h;
        } else {
            out[0] = x + sigx * w;
            out[1] = (float) (y + sigy * w * Math.tan(absRad));
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
        fontSize = TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
        paint.setTextSize(fontSize);
        mAutoTextSize = false;
        invalidate();
    }

    public float getTextSize() {
        return fontSize;
    }

    public void setAutoTextSize() {
        mAutoTextSize = true;
        calcScaleFactor(getWidth(), getHeight());
        invalidate();
    }

    public boolean isAutoTextSize() {
        return mAutoTextSize;
    }

    public void setMolecule(@Nullable Molecule molecule) {
        this.molecule = molecule;
        selectedChiral.clear();
        calcScaleFactor(getWidth(), getHeight());
        requestLayout();
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
        float rx = molecule.rangeX();
        float ry = molecule.rangeY();
        if (mAutoTextSize) {
            fontSize = molecule.getAverageBondLength() / 1.8f * ((rx + ry == 0) ? 1 : (rx * ry == 0 ? (ry == 0 ? width / rx :
                    height / ry) : Math.min(width / rx, height / ry)));
            paint.setTextSize(fontSize);
        }
        width -= getPaddingLeft() + getPaddingRight() + fontSize * 2;
        height -= getPaddingTop() + getPaddingBottom() + fontSize * 2;
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
        final float widthLimit = (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.EXACTLY)
                ? (float) widthSize : Float.MAX_VALUE;
        final float heightLimit = (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.EXACTLY)
                ? (float) heightSize : Float.MAX_VALUE;
        float rx = 0, ry = 0;
        if (molecule != null) {
            rx = molecule.rangeX();
            ry = molecule.rangeY();
        }
        float scale = -1;
        if (rx != 0 && widthLimit != Float.MAX_VALUE) {
            scale = (widthLimit - fontSize * 2 - getPaddingLeft() - getPaddingRight()) / rx;
        }
        if (ry != 0 && heightLimit != Float.MAX_VALUE) {
            if (scale == -1)
                scale = (heightLimit - fontSize * 2 - getPaddingTop() - getPaddingBottom()) / ry;
            else
                scale = Math.min(scale, (heightLimit - fontSize * 2 - getPaddingTop() - getPaddingBottom()) / ry);
        }
        if (molecule != null && scale > 0) {
            float avl = molecule.getAverageBondLength();
            if (avl > 0) {
                float ref = Utils.dip2px(getContext(), 40);
                if (avl * scale > ref) {
                    scale = ref / avl;
                }
            }
        }
        if (scale == -1) scale = 1;//sigh
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {
            width = (int) (scale * rx + fontSize * 2 + getPaddingLeft() + getPaddingRight());
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = (int) (scale * ry + fontSize * 2 + getPaddingBottom() + getPaddingTop());
        }
        calcScaleFactor(width, height);
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
        setClickable(true);
        paint.setAntiAlias(true);
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
