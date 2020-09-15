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
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

import com.tencent.mobileqq.app.IphoneTitleBarActivity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.CliOper;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressWarnings("deprecation")
@SuppressLint("Registered")
public class IphoneTitleBarActivityCompat extends IphoneTitleBarActivity {

    @Override
    public boolean doOnCreate(Bundle bundle) {
        boolean ret = super.doOnCreate(bundle);
        try {
            ResUtils.initTheme(this);
            try {
                AppCompatDelegate.setDefaultNightMode(ResUtils.isInNightMode() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            } catch (Throwable e) {
                log(e);
            }
            Object exlist_mFlingHandler = new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"), this, Activity.class);
            iput_object(this, "mFlingHandler", exlist_mFlingHandler);
        } catch (Throwable e) {
            log(e);
        }
        CliOper.enterModuleActivity(Utils.getShort$Name(this));
        return ret;
    }

    //@Override actually
    public boolean isWrapContent() {
        return true;
    }

    @SuppressLint("ResourceType")
    public void setContentBackgroundDrawable(Drawable d) {
        try {
            findViewById(16908290).setBackgroundDrawable(d);
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
            Field f;
            try {
                f = cl.getDeclaredField("rightViewText");
            } catch (NoSuchFieldException ex) {
                //WTF!!! it's 0202 now, still using QQ<6.5.5???
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
        Field f;
        try {
            f = cl.getDeclaredField("leftViewText");
        } catch (NoSuchFieldException ex) {
            //WTF!!! it's 0202 now, still using QQ<6.5.5???
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
            if (text != null) {
                btn.setText(text);
                btn.setVisibility(View.VISIBLE);
                btn.setOnClickListener(l);
            } else {
                btn.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
            if (windowState != null) {
                windowState.setClassLoader(IphoneTitleBarActivityCompat.class.getClassLoader());
            }
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public ClassLoader getClassLoader() {
        return IphoneTitleBarActivityCompat.class.getClassLoader();
    }
}
