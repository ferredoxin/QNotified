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
package nil.nadph.qnotified;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Natives;
import nil.nadph.qnotified.util.Utils;

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
    boolean sec_stage_inited = false;

    private StartupHook() {
    }

    public void doInit(ClassLoader rtLoader) throws Throwable {
        if (first_stage_inited) return;
        //checkClassLoaderIsolation();
        try {
            XC_MethodHook startup = new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (sec_stage_inited) return;
                        Utils.checkLogFlag();
                        Context ctx;
                        Class<?> clz = param.thisObject.getClass().getClassLoader().loadClass("com.tencent.common.app.BaseApplicationImpl");
                        final Field f = hasField(clz, "sApplication");
                        if (f == null) ctx = (Context) sget_object(clz, "a", clz);
                        else ctx = (Context) f.get(null);
                        ClassLoader classLoader = ctx.getClassLoader();
                        if (classLoader == null)
                            throw new AssertionError("ERROR: classLoader == null");
                        if ("true".equals(System.getProperty(QN_FULL_TAG))) {
                            logi("Err:QNotified reloaded??");
                            //I don't know... What happened?
                            return;
                            //System.exit(-1);
                            //QNotified updated(in HookLoader mode),kill QQ to make user restart it.
                        }
                        System.setProperty(QN_FULL_TAG, "true");
                        Initiator.init(classLoader);
                        try {
                            Natives.load(ctx);
                        } catch (Throwable e3) {
                            Utils.log(e3);
                        }
                        if (Utils.getBuildTimestamp() < 0) return;
                        MainHook.getInstance().performHook(ctx, param.thisObject);
                        sec_stage_inited = true;
                        deleteDirIfNecessary(ctx);
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            };
            Class<?> loadDex = rtLoader.loadClass("com.tencent.mobileqq.startup.step.LoadDex");
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
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.qfix.QFixApplication", rtLoader, "attachBaseContext", Context.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam param) throws Throwable {
                deleteDirIfNecessary((Context) param.args[0]);
            }
        });
    }

    static void deleteDirIfNecessary(Context ctx) {
        try {
            if (Build.VERSION.SDK_INT >= 24) {
                deleteFile(new File(ctx.getDataDir(), "app_qqprotect"));
            }
            if (new File(ctx.getFilesDir(), "qn_disable_hot_patch").exists()) {
                deleteFile(ctx.getFileStreamPath("hotpatch"));
            }
        } catch (Throwable e) {
            log(e);
        }
    }

    public static StartupHook getInstance() {
        if (SELF == null) SELF = new StartupHook();
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
        Log.e("QNdump", "currentThread.getContextClassLoader(): " + Thread.currentThread().getContextClassLoader());
        Log.e("QNdump", "Context.class: " + Context.class.getClassLoader());
    }
}
