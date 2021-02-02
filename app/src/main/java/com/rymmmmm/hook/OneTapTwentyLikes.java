/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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
package com.rymmmmm.hook;

import android.view.View;
import android.widget.ImageView;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;

import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.log;

//回赞界面一键20赞
public class OneTapTwentyLikes extends CommonDelayableHook {
    private static final OneTapTwentyLikes self = new OneTapTwentyLikes();

    public static OneTapTwentyLikes get() {
        return self;
    }

    protected OneTapTwentyLikes() {
        super("rq_one_tap_twenty_likes");
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator.load("com.tencent.mobileqq.activity.VisitorsActivity").getDeclaredMethods()) {
                if (m.getName().equals("onClick")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            View view = (View) param.args[0];
                            Object tag = view.getTag();
                            Object likeClickListener = iget_object_or_null(param.thisObject, "a", Initiator._VoteHelper());
                            Method onClick = likeClickListener.getClass().getDeclaredMethod("a", tag.getClass(), ImageView.class);
                            for (int i = 0; i < 20; i++) {
                                onClick.invoke(likeClickListener, tag, (ImageView) view);
                            }
                        }
                    });
                }
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
