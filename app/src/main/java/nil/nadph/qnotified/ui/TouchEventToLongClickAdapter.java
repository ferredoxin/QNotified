package nil.nadph.qnotified.ui;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import static nil.nadph.qnotified.util.Utils.log;

abstract public class TouchEventToLongClickAdapter implements View.OnTouchListener, View.OnLongClickListener, Runnable {
    private long mDownTime = -1;
    private float mX, mY;
    private int THRESHOLD = 500;

    private View val$mView;

    {
        try {
            THRESHOLD = ViewConfiguration.getLongPressTimeout();
        } catch (Throwable e) {
            log(e);
        }
    }

    public TouchEventToLongClickAdapter setLongPressTimeout(int ms) {
        this.THRESHOLD = ms;
        return this;
    }

    public TouchEventToLongClickAdapter setLongPressTimeoutFactor(float f) {
        this.THRESHOLD *= f;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x, y;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownTime = System.currentTimeMillis();
                mX = event.getX();
                mY = event.getY();
                val$mView = v;
                v.removeCallbacks(this);
                v.postDelayed(this, THRESHOLD);
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getX();
                y = event.getY();
                if (x < 0 || y < 0 || x > v.getWidth() || y > v.getHeight()) {
                    mDownTime = -1;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                mDownTime = -1;
                break;
            case MotionEvent.ACTION_UP:
                if (mDownTime < 0) break;
                long curr = System.currentTimeMillis();
                if (curr - mDownTime > THRESHOLD) {
                    mDownTime = -1;
                    break;
                }
        }
        return false;
    }

    @Override
    public void run() {
        if (mDownTime < 0) return;
        long curr = System.currentTimeMillis();
        if (curr - mDownTime > THRESHOLD) {
            mDownTime = -1;
            onLongClick(val$mView);
        }
    }
}

