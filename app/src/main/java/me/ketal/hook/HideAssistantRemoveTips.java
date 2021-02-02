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
package me.ketal.hook;

import android.content.Context;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.hook.CommonDelayableHook;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;

public class HideAssistantRemoveTips extends CommonDelayableHook {
    public static final HideAssistantRemoveTips INSTANCE = new HideAssistantRemoveTips();

    protected HideAssistantRemoveTips() {
        super("ketal_hide_assistant_removetips");
    }

    @Override
    protected boolean initOnce() {
        try {
            Class<?> clazz = load("com.tencent.mobileqq.activity.ChatActivityUtils");
            XposedHelpers.findAndHookMethod(clazz, "a", Context.class, String.class, View.OnClickListener.class, View.OnClickListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    if (isEnabled())
                        param.setResult(null);
                }
            });
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
