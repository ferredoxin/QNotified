/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package nil.nadph.qnotified.startup;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Startup hook for QQ/TIM They should act differently according to the process they belong to. I
 * don't want to cope with them any more, enjoy it as long as possible. DO NOT INVOKE ANY METHOD
 * THAT MAY GET IN TOUCH WITH KOTLIN HERE. DO NOT MODIFY ANY CODE HERE UNLESS NECESSARY.
 *
 * @author cinit
 */
public class StartupHook {

    public static final String QN_FULL_TAG = "qn_full_tag";
    public static StartupHook SELF;
    static boolean sec_static_stage_inited = false;
    private boolean first_stage_inited = false;

    private StartupHook() {
    }

    /**
     * Entry point for static or dynamic initialization. NOTICE: Do NOT change the method name or
     * signature.
     *
     * @param ctx         Application context for host
     * @param step        Step instance
     * @param lpwReserved null, not used
     * @param bReserved   false, not used
     */
    public static void execStartupInit(Context ctx, Object step, String lpwReserved,
        boolean bReserved) {
        if (sec_static_stage_inited) {
            return;
        }
        ClassLoader classLoader = ctx.getClassLoader();
        if (classLoader == null) {
            throw new AssertionError("ERROR: classLoader == null");
        }
        if ("true".equals(System.getProperty(QN_FULL_TAG))) {
            XposedBridge.log("Err:QNotified reloaded??");
            //I don't know... What happened?
            return;
        }
        System.setProperty(QN_FULL_TAG, "true");
        injectClassLoader(classLoader);
        StartupRoutine.execPostStartupInit(ctx, step, lpwReserved, bReserved);
        sec_static_stage_inited = true;
        deleteDirIfNecessaryNoThrow(ctx);
    }

    private static void injectClassLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            throw new NullPointerException("classLoader == null");
        }
        try {
            Field fParent = ClassLoader.class.getDeclaredField("parent");
            fParent.setAccessible(true);
            ClassLoader mine = StartupHook.class.getClassLoader();
            ClassLoader curr = (ClassLoader) fParent.get(mine);
            if (curr == null) {
                curr = XposedBridge.class.getClassLoader();
            }
            if (!curr.getClass().getName().equals(HybridClassLoader.class.getName())) {
                fParent.set(mine, new HybridClassLoader(curr, classLoader));
            }
        } catch (Exception e) {
            log_e(e);
        }
    }

    static void deleteDirIfNecessaryNoThrow(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                deleteFile(new File(ctx.getDataDir(), "app_qqprotect"));
            }
            if (new File(ctx.getFilesDir(), "qn_disable_hot_patch").exists()) {
                deleteFile(ctx.getFileStreamPath("hotpatch"));
            }
        } catch (Throwable e) {
            log_e(e);
        }
    }

    public static StartupHook getInstance() {
        if (SELF == null) {
            SELF = new StartupHook();
        }
        return SELF;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean deleteFile(File file) {
        if (!file.exists()) {
            return false;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (File deleteFile : listFiles) {
                    deleteFile(deleteFile);
                }
            }
            file.delete();
        }
        return !file.exists();
    }

    static void log_e(Throwable th) {
        if (th == null) {
            return;
        }
        String msg = Log.getStackTraceString(th);
        Log.e("QNdump", msg);
        try {
            XposedBridge.log(th);
        } catch (NoClassDefFoundError e) {
            Log.e("Xposed", msg);
            Log.e("EdXposed-Bridge", msg);
        }
    }

    private static void checkClassLoaderIsolation() {
        Class<?> stub;
        try {
            stub = Class.forName("com.tencent.common.app.BaseApplicationImpl");
        } catch (ClassNotFoundException e) {
            Log.d("QNdump", "checkClassLoaderIsolation success");
            return;
        }
        Log.e("QNdump", "checkClassLoaderIsolation failure!");
        Log.e("QNdump", "HostApp: " + stub.getClassLoader());
        Log.e("QNdump", "Module: " + StartupHook.class.getClassLoader());
        Log.e("QNdump", "Module.parent: " + StartupHook.class.getClassLoader().getParent());
        Log.e("QNdump", "XposedBridge: " + XposedBridge.class.getClassLoader());
        Log.e("QNdump", "SystemClassLoader: " + ClassLoader.getSystemClassLoader());
        Log.e("QNdump", "currentThread.getContextClassLoader(): " + Thread.currentThread()
            .getContextClassLoader());
        Log.e("QNdump", "Context.class: " + Context.class.getClassLoader());
    }

    public void doInit(ClassLoader rtLoader) throws Throwable {
        if (first_stage_inited) {
            return;
        }
        try {
            XC_MethodHook startup = new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Context app;
                        Class<?> clz = param.thisObject.getClass().getClassLoader()
                            .loadClass("com.tencent.common.app.BaseApplicationImpl");
                        Field fsApp = null;
                        for (Field f : clz.getDeclaredFields()) {
                            if (f.getType() == clz) {
                                fsApp = f;
                                break;
                            }
                        }
                        if (fsApp == null) {
                            throw new NoSuchFieldException(
                                "field BaseApplicationImpl.sApplication not found");
                        }
                        app = (Context) fsApp.get(null);
                        execStartupInit(app, param.thisObject, null, false);
                    } catch (Throwable e) {
                        log_e(e);
                        throw e;
                    }
                }
            };
            Class<?> loadDex = rtLoader.loadClass("com.tencent.mobileqq.startup.step.LoadDex");
            Method[] ms = loadDex.getDeclaredMethods();
            Method m = null;
            for (Method method : ms) {
                if (method.getReturnType().equals(boolean.class)
                    && method.getParameterTypes().length == 0) {
                    m = method;
                    break;
                }
            }
            XposedBridge.hookMethod(m, startup);
            first_stage_inited = true;
        } catch (Throwable e) {
            if ((e + "").contains("com.bug.zqq")) {
                return;
            }
            if ((e + "").contains("com.google.android.webview")) {
                return;
            }
            log_e(e);
            throw e;
        }
        try {
            XposedHelpers
                .findAndHookMethod(rtLoader.loadClass("com.tencent.mobileqq.qfix.QFixApplication"),
                    "attachBaseContext", Context.class, new XC_MethodHook() {
                        public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            deleteDirIfNecessaryNoThrow((Context) param.args[0]);
                        }
                    });
        } catch (ClassNotFoundException ignored) {
        }
    }


}
