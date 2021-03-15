/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

import android.content.Intent;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;

//屏蔽Diy卡片
@FunctionEntry
public class IgnoreDiyCard extends CommonDelayableHook {

    public static final IgnoreDiyCard INSTANCE = new IgnoreDiyCard();

    private IgnoreDiyCard() {
        super("rq_ignore_diy_card");
    }

    @Override
    public boolean initOnce() {
        try {
            for (Method m : load("com.tencent.mobileqq.activity.FriendProfileCardActivity")
                .getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (HostInformationProviderKt.getHostInfo().getVersionCode32()
                    <= QQVersion.QQ_8_3_6) {
                    if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers()) && m
                        .getReturnType().equals(void.class)) {
                        if (argt.length != 2) {
                            continue;
                        }
                        if (argt[1] != boolean.class) {
                            continue;
                        }
                        if (argt[0].getSuperclass() != Object.class) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                } else {
                    if (m.getName().equals("b") && !Modifier.isStatic(m.getModifiers()) && m
                        .getReturnType().equals(void.class)) {
                        if (argt.length != 1) {
                            continue;
                        }
                        if (argt[0].getSuperclass() == Intent.class) {
                            continue;
                        }
                        if (argt[0].getSuperclass() != Object.class) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                }
                XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
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

