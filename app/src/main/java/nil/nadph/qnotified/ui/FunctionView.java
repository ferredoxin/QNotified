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
