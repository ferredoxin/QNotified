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

import android.content.Intent;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.NonNull;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

//屏蔽Diy卡片
public class IgnoreDiyCard extends CommonDelayableHook {
    private static final IgnoreDiyCard self = new IgnoreDiyCard();

    private IgnoreDiyCard() {
        super("rq_ignore_diy_card");
    }

    @NonNull
    public static IgnoreDiyCard get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : load("com.tencent.mobileqq.activity.FriendProfileCardActivity").getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (HostInformationProviderKt.getHostInformationProvider().getVersionCode32() <= 1406) {
                    if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(void.class)) {
                        if (argt.length != 2) continue;
                        if (argt[1] != boolean.class) continue;
                        if (argt[0].getSuperclass() != Object.class) continue;
                    } else continue;
                } else {
                    if (m.getName().equals("b") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(void.class)) {
                        if (argt.length != 1) continue;
                        if (argt[0].getSuperclass() == Intent.class) continue;
                        if (argt[0].getSuperclass() != Object.class) continue;
                    } else continue;
                }
                XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        if (!isEnabled()) return;
                        Class<?> _ProfileCardInfo = ((Method) param.method).getParameterTypes()[0];
                        Object info = iget_object_or_null(param.thisObject, "a", _ProfileCardInfo);
                        if (info != null) {
                            Class<?> _Card = load("com.tencent.mobileqq.data.Card");
                            Object card = iget_object_or_null(info, "a", _Card);
                            if (card != null) {
                                Field f = _Card.getField("lCurrentStyleId");
                                if (f.getLong(card) == 22 || f.getLong(card) == 21) {
                                    f.setLong(card, 0);
                                }
                            } else {
                                loge("IgnoreDiyCard/W but info.<Card> == null");
                            }
                        } else {
                            loge("IgnoreDiyCard/W but info == null");
                        }
                    }
                });
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}

