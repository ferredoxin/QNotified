package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;
import com.tencent.mobileqq.app.IphoneTitleBarActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Utils.log;

@SuppressWarnings("deprecation")
@SuppressLint("Registered")
public class IphoneTitleBarActivityCompat extends IphoneTitleBarActivity {

    public boolean isWrapContent() {
        return true;
    }

    @SuppressLint("ResourceType")
    public void setContentBackgroundDrawable(Drawable d) {
        try {
            ((View) findViewById(16908290)).setBackgroundDrawable(d);
        } catch (NullPointerException e) {
            log(e);
        }
    }

    @Override
    public View getRightTextView() {
        try {
            return super.getRightTextView();
        } catch (NoSuchMethodError e) {
            Class<IphoneTitleBarActivity> cl = IphoneTitleBarActivity.class;
            Field f = null;
            try {
                f = cl.getDeclaredField("rightViewText");
            } catch (NoSuchFieldException ex) {
                //WTF!!! it's 9103 now, still using QQ<6.5.5???
                Field l = null, r = null;
                for (Field fs : cl.getDeclaredFields()) {
                    if (!Modifier.isPublic(fs.getModifiers())) continue;
                    if (fs.getName().length() != 1) continue;
                    if (l == null) {
                        l = fs;
                    } else {
                        r = fs;
                        break;
                    }
                }
                f = r;
            }
            if (f != null) {
                f.setAccessible(true);
                try {
                    return (View) f.get(this);
                } catch (IllegalAccessException ex2) {
                    log(ex2);
                    return null;
                }
            }
            return null;
        }
    }

    public View getLeftTextView() {
        Class<IphoneTitleBarActivity> cl = IphoneTitleBarActivity.class;
        Field f = null;
        try {
            f = cl.getDeclaredField("leftViewText");
        } catch (NoSuchFieldException ex) {
            //WTF!!! it's 9103 now, still using QQ<6.5.5???
            Field l = null, r = null;
            for (Field fs : cl.getDeclaredFields()) {
                if (!Modifier.isPublic(fs.getModifiers())) continue;
                if (fs.getName().length() != 1) continue;
                if (l == null) {
                    l = fs;
                } else {
                    r = fs;
                    break;
                }
            }
            f = l;
        }
        if (f != null) {
            f.setAccessible(true);
            try {
                return (View) f.get(this);
            } catch (IllegalAccessException ex2) {
                log(ex2);
                return null;
            }
        }
        return null;
    }

    public void setRightButton(String text, View.OnClickListener l) {
        TextView btn = (TextView) getRightTextView();
        if (btn != null) {
            btn.setText(text);
            btn.setVisibility(View.VISIBLE);
            btn.setOnClickListener(l);
        }
    }

}
