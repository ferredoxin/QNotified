package nil.nadph.qnotified.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class InterceptLayout extends LinearLayout {

    private OnTouchListener mTouchInterceptor = null;
    private OnKeyListener mKeyInterceptor = null;

    public OnKeyListener getKeyInterceptor() {
        return mKeyInterceptor;
    }

    public OnTouchListener getTouchInterceptor() {
        return mTouchInterceptor;
    }

    public void setKeyInterceptor(OnKeyListener mKeyInterceptor) {
        this.mKeyInterceptor = mKeyInterceptor;
    }

    public void setTouchInterceptor(OnTouchListener mTouchInterceptor) {
        this.mTouchInterceptor = mTouchInterceptor;
    }

    public static InterceptLayout setupRudely(View v) {
        ViewGroup parent = (ViewGroup) v.getParent();
        int index = 0;
        ViewGroup.LayoutParams currlp = v.getLayoutParams();
        for (int i = 0; i < parent.getChildCount(); i++) {
            if (parent.getChildAt(i) == v) {
                index = i;
                break;
            }
        }
        parent.removeView(v);
        InterceptLayout layout = new InterceptLayout(v.getContext());
        if (currlp != null)
            layout.addView(v, new LinearLayout.LayoutParams(currlp.width, currlp.height));
        else
            layout.addView(v, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        parent.addView(layout, index, currlp);
        return layout;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mTouchInterceptor != null && mTouchInterceptor.onTouch(this, ev)) return true;
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mKeyInterceptor != null && mKeyInterceptor.onKey(this, event.getKeyCode(), event)) return true;
        return super.dispatchKeyEvent(event);
    }

    public InterceptLayout(Context context) {
        super(context);
    }

    public InterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
