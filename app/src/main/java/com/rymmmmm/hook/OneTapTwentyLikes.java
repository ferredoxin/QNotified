/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package com.rymmmmm.hook;

import static nil.nadph.qnotified.util.ReflexUtil.getFirstByType;
import static nil.nadph.qnotified.util.Utils.log;

import android.view.View;
import android.widget.ImageView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.annotation.UiItem;
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge;
import me.singleneuron.qn_kernel.tlb.UiRoutineKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//回赞界面一键20赞
@FunctionEntry
@UiItem
public class OneTapTwentyLikes extends CommonDelayAbleHookBridge {

    public static final OneTapTwentyLikes INSTANCE = new OneTapTwentyLikes();

    @Override
    public boolean initOnce() {
        try {
            for (Method m : Initiator.load("com.tencent.mobileqq.activity.VisitorsActivity")
                .getDeclaredMethods()) {
                if (m.getName().equals("onClick")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            View view = (View) param.args[0];
                            Object tag = view.getTag();
                            Object likeClickListener = getFirstByType(param.thisObject,
                                Initiator._VoteHelper());
                            Method onClick = likeClickListener.getClass()
                                .getDeclaredMethod("a", tag.getClass(), ImageView.class);
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

    private final UiSwitchPreference mUiSwitchPreference = this.new UiSwitchPreferenceItemFactory(
        "回赞界面一键20赞");

    @NotNull
    @Override
    public UiSwitchPreference getPreference() {
        return mUiSwitchPreference;
    }

    @Nullable
    @Override
    public String[] getPreferenceLocate() {
        return UiRoutineKt.get花Q();
    }
}
