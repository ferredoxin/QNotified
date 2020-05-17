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
        int w = getBounds().width() - 1;
        int h = getBounds().height() - 1;
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, w - 1, 0, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1, 1, w, 1, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0, 0, 0, h - 1, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1, 1, 1, h, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(w - 1, 0, w - 1, h - 1, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(w, 1, w, h, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawLine(0, h - 1, w - 1, h - 1, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawLine(1, h, w, h, mPaint);
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
