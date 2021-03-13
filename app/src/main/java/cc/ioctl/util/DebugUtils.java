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
package cc.ioctl.util;

import static nil.nadph.qnotified.util.Utils.logi;

import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import nil.nadph.qnotified.util.Utils;

/**
 * Handy utils used for debug/development env, not to use in production.
 */
public class DebugUtils {

    public static final XC_MethodHook dummyHook = new XC_MethodHook(200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
        }
    };
    public static final XC_MethodHook invokeRecord = new XC_MethodHook(200) {
        @Override
        protected void afterHookedMethod(MethodHookParam param)
            throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(
                m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName()
                    : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method) {
                argt = ((Method) m).getParameterTypes();
            } else if (m instanceof Constructor) {
                argt = ((Constructor) m).getParameterTypes();
            } else {
                argt = new Class[0];
            }
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) {
                    ret.append(",\n");
                }
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder(
                "↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=")
                    .append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };
    public static final XC_MethodHook invokeInterceptor = new XC_MethodHook(200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param)
            throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(
                m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName()
                    : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method) {
                argt = ((Method) m).getParameterTypes();
            } else if (m instanceof Constructor) {
                argt = ((Constructor) m).getParameterTypes();
            } else {
                argt = new Class[0];
            }
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) {
                    ret.append(",\n");
                }
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder(
                "↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=")
                    .append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };


}
