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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.ReflexUtil.iput_object;

//去除群聊送礼物广告
public class RemoveSendGiftAd extends CommonDelayableHook {
    private static final RemoveSendGiftAd self = new RemoveSendGiftAd();

    public static RemoveSendGiftAd get() {
        return self;
    }


    public RemoveSendGiftAd() {
        super("rq_remove_send_gift_ad");
    }

    @Override
    public boolean initOnce() {
        try {
            final Class<?> _TroopGiftPanel = Initiator.load("com.tencent.biz.troopgift.TroopGiftPanel");
            for (Method m : _TroopGiftPanel.getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("onClick") && argt.length == 1 && !Modifier.isStatic(m.getModifiers())) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            iput_object(param.thisObject, "f", Boolean.TYPE, true);
                        }
                    });
                }
            }
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }
}
