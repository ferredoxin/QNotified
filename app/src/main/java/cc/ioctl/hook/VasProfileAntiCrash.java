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
package cc.ioctl.hook;

import android.util.JsonReader;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

/**
 * Not an important hook.
 * Provide limited anti-crash feature for VasProfileCard, esp DIY card.
 */
public class VasProfileAntiCrash extends CommonDelayableHook {

    private static final VasProfileAntiCrash self = new VasProfileAntiCrash();

    private VasProfileAntiCrash() {
        super("__NOT_USED__");
    }

    public static VasProfileAntiCrash get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            String className = null;
            try {
                className = ConfigTable.INSTANCE.getConfig(VasProfileAntiCrash.class.getSimpleName());
            } catch (Exception e) {
                Utils.log(e);
            }
            doHook(className);
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private void doHook(String className) {
        try {

            XposedBridge.hookAllMethods(JsonReader.class, "nextLong", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!param.hasThrowable()) return;
                    if (!Log.getStackTraceString(param.getThrowable()).contains("FriendProfileCardActivity")) return;
                    param.setResult(0L);
                }
            });
        } catch (Exception e) {
            //ignore
        }
        if (className == null) return;
        Class<?> Card = Initiator.load("com.tencent.mobileqq.data.Card");
        for (Method m : Initiator.load(className).getDeclaredMethods()) {
            Class<?>[] argt;
            if (Modifier.isStatic(m.getModifiers()) && m.getName().equals("a")
                    && m.getReturnType() == long.class && (argt = m.getParameterTypes()).length == 1
                    && argt[0] == Card) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (param.hasThrowable()) {
                            param.setResult(0L);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
