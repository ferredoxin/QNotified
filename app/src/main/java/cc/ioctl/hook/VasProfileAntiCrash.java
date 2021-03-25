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

import static nil.nadph.qnotified.util.Utils.log;

import android.util.JsonReader;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XposedHelpers;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

/**
 * Not an important hook. Provide limited anti-crash feature for VasProfileCard, esp DIY card.
 */
@FunctionEntry
public class VasProfileAntiCrash extends CommonDelayableHook {

    public static final VasProfileAntiCrash INSTANCE = new VasProfileAntiCrash();

    private VasProfileAntiCrash() {
        super("__NOT_USED__");
    }

    @Override
    public boolean initOnce() {
        try {
            String className = null;
            try {
                className = ConfigTable.INSTANCE
                    .getConfig(VasProfileAntiCrash.class.getSimpleName());
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
                    if (!param.hasThrowable()) {
                        return;
                    }
                    if (!Log.getStackTraceString(param.getThrowable())
                        .contains("FriendProfileCardActivity")) {
                        return;
                    }
                    param.setResult(0L);
                }
            });
        } catch (Exception e) {
            //ignore
        }
        if (className == null) {
            return;
        }
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.hasThrowable()) {
                    param.setResult(0L);
                }
            }
        };
        Class<?> Card = Initiator.load("com.tencent.mobileqq.data.Card");
        if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_6_0)) {
            XposedHelpers.findAndHookMethod(
                Initiator.load(className),
                "getDiyTemplateVersion", Card, hook);
            return;
        }
        for (Method m : Initiator.load(className).getDeclaredMethods()) {
            Class<?>[] argt;
            if (Modifier.isStatic(m.getModifiers()) && m.getName().equals("a")
                && m.getReturnType() == long.class && (argt = m.getParameterTypes()).length == 1
                && argt[0] == Card) {
                XposedBridge.hookMethod(m, hook);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }
}
