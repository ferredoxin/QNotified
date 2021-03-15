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
package cc.ioctl.util.internal;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class XMethodHookDispatchUtil {

    private static final Field F_RETURN_EARLY;
    private static final Constructor<XC_MethodHook.MethodHookParam> M_PARAM_INIT;
    private static final Method M_XCM_beforeHookedMethod;
    private static final Method M_XCM_afterHookedMethod;

    static {
        try {
            F_RETURN_EARLY = XC_MethodHook.MethodHookParam.class.getDeclaredField("returnEarly");
            F_RETURN_EARLY.setAccessible(true);
            M_PARAM_INIT = XC_MethodHook.MethodHookParam.class.getDeclaredConstructor();
            M_PARAM_INIT.setAccessible(true);
            M_XCM_beforeHookedMethod = XC_MethodHook.class
                .getDeclaredMethod("beforeHookedMethod", XC_MethodHook.MethodHookParam.class);
            M_XCM_beforeHookedMethod.setAccessible(true);
            M_XCM_afterHookedMethod = XC_MethodHook.class
                .getDeclaredMethod("afterHookedMethod", XC_MethodHook.MethodHookParam.class);
            M_XCM_afterHookedMethod.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new UnsupportedOperationException(
                "MethodHookParam.returnEarly not found, API: " + XposedBridge.getXposedVersion());
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException(
                "MethodHookParam.<init>() not found, API: " + XposedBridge.getXposedVersion());
        }
    }


    public static XC_MethodHook.MethodHookParam createParam(XC_MethodHook hook, Method method,
        Object thisObj, Object... argv) {
        try {
            XC_MethodHook.MethodHookParam p = M_PARAM_INIT.newInstance();
            p.thisObject = thisObj;
            p.method = method;
            p.args = argv;
            return p;
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean callBeforeHook(XC_MethodHook hook, XC_MethodHook.MethodHookParam param) {
        if (hook == null || param == null) {
            return false;
        }
        try {
            M_XCM_beforeHookedMethod.invoke(hook, param);
            return (boolean) F_RETURN_EARLY.get(param);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            XposedBridge.log(e);
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static void callAfterHook(XC_MethodHook hook, XC_MethodHook.MethodHookParam param) {
        if (hook == null || param == null) {
            return;
        }
        try {
            M_XCM_afterHookedMethod.invoke(hook, param);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            XposedBridge.log(e);
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        } catch (Throwable e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    public static final class HookHolder {

        public XC_MethodHook hook;
        public Method method;

        public HookHolder() {
        }
        public HookHolder(XC_MethodHook h, Method m) {
            hook = h;
            method = m;
        }
    }

}
