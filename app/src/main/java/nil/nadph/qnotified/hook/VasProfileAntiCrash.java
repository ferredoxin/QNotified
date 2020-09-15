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
package nil.nadph.qnotified.hook;

import android.util.JsonReader;
import android.util.Log;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

/**
 * Not an important hook.
 * Provide limited anti-crash feature for VasProfileCard, esp DIY card.
 */
public class VasProfileAntiCrash extends BaseDelayableHook {

    private static final VasProfileAntiCrash self = new VasProfileAntiCrash();
    private boolean inited = false;

    private VasProfileAntiCrash() {
    }

    public static VasProfileAntiCrash get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            int versionCode32 = (int) Utils.getHostVersionCode();
            //switch only support int, not long
            switch (versionCode32) {
                case (int) QQVersion.QQ_8_4_1: {
                    doHook("azfl");
                    break;
                }
                case (int) QQVersion.QQ_8_4_5: {
                    doHook("azxy");
                    break;
                }
                case (int) QQVersion.QQ_8_4_8: {
                     doHook("aymn");
                     break;
                 }
                default: {
                    doHook(null);
                }
            }
            inited = true;
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
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
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
