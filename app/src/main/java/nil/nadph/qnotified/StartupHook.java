/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.*;

/**
 * Startup hook for QQ/TIM
 * They should act differently according to the process they belong to.
 * I don't want to cope with them any more, enjoy it as long as possible.
 */
public class StartupHook {
    public static final String QN_FULL_TAG = "qn_full_tag";
    public static StartupHook SELF;
    private boolean first_stage_inited = false;
    private boolean sec_stage_inited = false;

    private StartupHook() {
    }

    public void doInit(ClassLoader rtloader) throws Throwable {
        if (first_stage_inited) return;
        try {
            XC_MethodHook startup = new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (sec_stage_inited) return;
                        Utils.checkLogFlag();
                        Context ctx;
                        Class clz = param.thisObject.getClass().getClassLoader().loadClass("com.tencent.common.app.BaseApplicationImpl");
                        final Field f = hasField(clz, "sApplication");
                        if (f == null) ctx = (Context) sget_object(clz, "a", clz);
                        else ctx = (Context) f.get(null);
                        ClassLoader classLoader = ctx.getClassLoader();
                        if (classLoader == null) throw new AssertionError("ERROR: classLoader == null");
                        if ("true".equals(System.getProperty(QN_FULL_TAG))) {
                            log("Err:QNotified reloaded??");
                            //I don't know... What happened?
                            return;
                            //System.exit(-1);
                            //QNotified updated(in HookLoader mode),kill QQ to make user restart it.
                        }
                        System.setProperty(QN_FULL_TAG, "true");
                        Initiator.init(classLoader);
                        MainHook.getInstance().performHook(ctx, param.thisObject);
                        sec_stage_inited = true;
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            };
            Class<?> loadDex = rtloader.loadClass("com.tencent.mobileqq.startup.step.LoadDex");
            Method[] ms = loadDex.getDeclaredMethods();
            Method m = null;
            for (Method method : ms) {
                if (method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 0) {
                    m = method;
                    break;
                }
            }
            XposedBridge.hookMethod(m, startup);
            first_stage_inited = true;
        } catch (Throwable e) {
            if ((e + "").contains("com.bug.zqq")) return;
            if ((e + "").contains("com.google.android.webview")) return;
            log(e);
            throw e;
        }
    }

    public static StartupHook getInstance() {
        if (SELF == null) SELF = new StartupHook();
        return SELF;
    }
}
