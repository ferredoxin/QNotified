package nil.nadph.qnotified.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

public class HighContrastBorder extends Drawable {
    private Paint mPaint;

    public HighContrastBorder() {
        mPaint = new Paint();
    }

    public Paint getPaint() {
        return mPaint;
    }

    @Override
    public void draw(Canvas canvas) {
        int w = getBounds().width();
        int h = getBounds().height();
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
