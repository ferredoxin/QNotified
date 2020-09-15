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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.*;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.List;

import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import me.kyuubiran.hook.RemoveCameraButton;
import nil.nadph.qnotified.activity.SettingsActivity;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.hook.rikka.CustomSplash;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.___WindowIsTranslucent;
import nil.nadph.qnotified.util.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTION_RESERVED;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTIVITY_PROXY_ACTION;
import static nil.nadph.qnotified.util.Initiator._StartupDirector;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

/*TitleKit:Lcom/tencent/mobileqq/widget/navbar/NavBarCommon*/


@SuppressWarnings("rawtypes")
public class MainHook {

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
        protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method)
                argt = ((Method) m).getParameterTypes();
            else if (m instanceof Constructor)
                argt = ((Constructor) m).getParameterTypes();
            else argt = new Class[0];
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) ret.append(",\n");
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder("↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=").append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };
    public static final XC_MethodHook invokeInterceptor = new XC_MethodHook(200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException, IllegalArgumentException {
            Member m = param.method;
            StringBuilder ret = new StringBuilder(m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(");
            Class[] argt;
            if (m instanceof Method)
                argt = ((Method) m).getParameterTypes();
            else if (m instanceof Constructor)
                argt = ((Constructor) m).getParameterTypes();
            else argt = new Class[0];
            for (int i = 0; i < argt.length; i++) {
                if (i != 0) ret.append(",\n");
                ret.append(param.args[i]);
            }
            ret.append(")=").append(param.getResult());
            Utils.logi(ret.toString());
            ret = new StringBuilder("↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=").append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            logi(ret.toString());
            Utils.dumpTrace();
        }
    };
    private static MainHook SELF;
    public static WeakReference<Activity> splashActivityRef;


    boolean third_stage_inited = false;

    private MainHook() {
    }

    public static MainHook getInstance() {
        if (SELF == null) SELF = new MainHook();
        return SELF;
    }

    public static XC_MethodHook.Unhook findAndHookMethodIfExists(Class<?> clazz, String methodName, Object...
            parameterTypesAndCallback) {
        try {
            return findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Throwable e) {
            log(e);
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethodIfExists(String clazzName, ClassLoader cl, String
            methodName, Object... parameterTypesAndCallback) {
        try {
            return findAndHookMethod(clazzName, cl, methodName, parameterTypesAndCallback);
        } catch (Throwable e) {
            log(e);
            return null;
        }
    }

    public static void startProxyActivity(Context ctx, int action) {
        Intent intent = new Intent(ctx, ActProxyMgr.getActivityByAction(action));
        intent.putExtra(ACTIVITY_PROXY_ACTION, action);
        intent.putExtra("fling_action_key", 2);
        intent.putExtra("fling_code_key", ctx.hashCode());
        ctx.startActivity(intent);
    }

    public static void startProxyActivity(Context ctx, Class<?> clz) {
        Intent intent = new Intent(ctx, clz);
        intent.putExtra(ACTIVITY_PROXY_ACTION, ACTION_RESERVED);
        intent.putExtra("fling_action_key", 2);
        intent.putExtra("fling_code_key", ctx.hashCode());
        ctx.startActivity(intent);
    }

    public static void openProfileCard(Context ctx, long uin) {
        try {
            Parcelable allInOne = (Parcelable) new_instance(load("com/tencent/mobileqq/activity/ProfileActivity$AllInOne"), "" + uin, 35, String.class, int.class);
            Intent intent = new Intent(ctx, load("com/tencent/mobileqq/activity/FriendProfileCardActivity"));
            intent.putExtra("AllInOne", allInOne);
            ctx.startActivity(intent);
        } catch (Exception e) {
            log(e);
        }
    }

    /**
     * A屏黑主题,自用
     */
    public static void deepDarkTheme() {
        if (!SyncUtils.isMainProcess()) return;
        if (getLongAccountUin() != 1041703712) return;
        try {
            Class clz = load("com/tencent/mobileqq/activity/FriendProfileCardActivity");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0).setBackgroundColor(0xFF000000);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        View frame = ctx.findViewById(android.R.id.content);
                                        frame.setBackgroundColor(0xFF000000);
                                        View dk0 = ctx.findViewById(ctx.getResources().getIdentifier("dk0", "id", ctx.getPackageName()));
                                        if (dk0 != null) dk0.setBackgroundColor(0x00000000);
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
            clz = load("com.tencent.mobileqq.activity.ChatSettingForTroop");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0).setBackgroundColor(0xFF000000);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FrameLayout frame = ctx.findViewById(android.R.id.content);
                                        frame.getChildAt(0).setBackgroundColor(0xFF000000);
                                        ViewGroup list = ctx.findViewById(ctx.getResources().getIdentifier("common_xlistview", "id", ctx.getPackageName()));
                                        list.getChildAt(0).setBackgroundColor(0x00000000);
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
            clz = load("com.tencent.mobileqq.activity.TroopMemberListActivity");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0)/*.getChildAt(0)*/.setBackgroundColor(0xFF000000);
                }
            });
        } catch (Exception e) {
            log(e);
        }
    }

    public void performHook(Context ctx, Object step) {
        SyncUtils.initBroadcast(ctx);
//        if (SyncUtils.getProcessType() == SyncUtils.PROC_MSF) {
//            Debug.waitForDebugger();
//        }
        try {
            Class<?> _NewRuntime = Initiator.load("com.tencent.mobileqq.startup.step.NewRuntime");
            Method[] methods = _NewRuntime.getDeclaredMethods();
            Method doStep = null;
            if (methods.length == 1) {
                doStep = methods[0];
            } else {
                for (Method m : methods) {
                    if (Modifier.isProtected(m.getModifiers()) || m.getName().equals("doStep")) {
                        doStep = m;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(doStep, new XC_MethodHook(52) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Utils.$access$set$sAppRuntimeInit(true);
                    //logi("NewRuntime/I after doStep");
                    if (SyncUtils.isMainProcess()) {
                        // fix error in :video, and QZone启动失败
                        LicenseStatus.sDisableCommonHooks = LicenseStatus.isLoadingDisabled() || LicenseStatus.isBlacklisted() || LicenseStatus.isSilentGone();
                    }
                }
            });
        } catch (Throwable e) {
            loge("NewRuntime/E hook failed: " + e);
            Utils.$access$set$sAppRuntimeInit(true);
        }
        BaseDelayableHook.allowEarlyInit(RevokeMsgHook.get());
        BaseDelayableHook.allowEarlyInit(MuteQZoneThumbsUp.get());
        BaseDelayableHook.allowEarlyInit(MuteAtAllAndRedPacket.get());
        BaseDelayableHook.allowEarlyInit(GagInfoDisclosure.get());
        BaseDelayableHook.allowEarlyInit(CustomSplash.get());
        BaseDelayableHook.allowEarlyInit(RemoveCameraButton.get());
        if (SyncUtils.isMainProcess()) {
            ConfigItems.removePreviousCacheIfNecessary();
            injectStartupHookForMain(ctx);
            Class loadData = load("com/tencent/mobileqq/startup/step/LoadData");
            Method doStep = null;
            for (Method method : loadData.getDeclaredMethods()) {
                if (method.getReturnType().equals(boolean.class) && method.getParameterTypes().length == 0) {
                    doStep = method;
                    break;
                }
            }
            XposedBridge.hookMethod(doStep, new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (third_stage_inited) return;
                    Class director = _StartupDirector();
                    Object dir = iget_object_or_null(param.thisObject, "mDirector", director);
                    if (dir == null) dir = iget_object_or_null(param.thisObject, "a", director);
                    if (dir == null) dir = getFirstNSFByType(param.thisObject, director);
                    if (SyncUtils.isMainProcess()) {
                        ResUtils.loadThemeByArsc(getApplication(), false);
                    }
                    InjectDelayableHooks.step(dir);
                    onAppStartupForMain();
                    third_stage_inited = true;
                    //startFakeString();
                }
            });
        } else {
            if (LicenseStatus.hasUserAcceptEula()) {
                Class director = _StartupDirector();
                Object dir = iget_object_or_null(step, "mDirector", director);
                if (dir == null) dir = iget_object_or_null(step, "a", director);
                if (dir == null) dir = getFirstNSFByType(step, director);
                InjectDelayableHooks.step(dir);
            }
        }
    }

//    public static ClassLoader targetLoader = null;
//
//    public static void startFakeString() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(3_000);
//                } catch (InterruptedException e) {
//                }
//                if (SyncUtils.getProcessType() != 1) return;
//                Map sHookedMethodCallbacks = (Map) sget_object(XposedBridge.class, "sHookedMethodCallbacks");
//                a:
//                for (Object ent : sHookedMethodCallbacks.entrySet()) {
//                    Object[] elem = (Object[]) iget_object_or_null(((Map.Entry) ent).getValue(), "elements");
//                    for (Object cb : elem) {
//                        Class hook = cb.getClass();
//                        if (hook.getName().contains(_clz_name_)) {
//                            targetLoader = hook.getClassLoader();
//                            break a;
////                            try {
////                                Class de = cl.loadClass(_decoder_.replace(" ", ""));
////                                Method[] ms = de.getDeclaredMethods();
////                                Method m = null;
////                                for (Method mi : ms) {
////                                    if (mi.getReturnType().equals(String.class)) {
////                                        m = mi;
////                                        m.setAccessible(true);
////                                    }
////                                }
////                                StringBuilder fout = new StringBuilder();
////                                for (int i = 0; i < 4000; i++) {
////                                    String ret = null;
////                                    try {
////                                        ret = (String) m.invoke(null, i);
////                                    } catch (Exception e) {
////                                        ret = null;
////                                    }
////                                    ret = Utils.en(ret);
////                                    fout.append(ret);
////                                    fout.append('\n');
////                                }
////                                FileOutputStream f2out = new FileOutputStream(_out_);
////                                f2out.write(fout.toString().getBytes());
////                                f2out.flush();
////                                f2out.close();
////                            } catch (Exception e) {
////                                log(e);
////                            }
//                        }
//                    }
//                }
//                Natives.load();
//                long loadAddr = -1;
//                try {
//                    FileInputStream fin = new FileInputStream("/proc/self/maps");
//                    BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
//                    String map;
//                    while ((map = reader.readLine()) != null) {
//                        if (map.contains(_so_name_)) {
//                            long start = Long.parseLong(map.split(" ")[0].split("-")[0], 16);
//                            if (start > 0) {
//                                if (loadAddr > 0) {
//                                    loadAddr = Math.min(loadAddr, start);
//                                } else {
//                                    loadAddr = start;
//                                }
//                            }
//                        }
//                    }
//                    fin.close();
//                    byte[] buf = new byte[64];
//                    if (loadAddr > 0) {
//                        int ps = Natives.getpagesize();
//                        long addr = loadAddr + offset;
//                        long delta = addr % ps;
//                        long pstart = addr - delta;
//                        int ret = Natives.mprotect(pstart, (int) (delta + 16), Natives.PROT_EXEC | Natives.PROT_WRITE | Natives.PROT_READ);
//                        Natives.mread(addr, 64, buf);
//                        byte[] patch = new byte[]{0x70, 0x47};
//                        Natives.mwrite(addr, 2, patch);
//                    }
//
//                    File tmpin = new File("/tmp/index1.txt");
//                    StringBuilder sb = new StringBuilder();
//                    if (tmpin.exists()) {
//
//
//                        reader = new BufferedReader(new InputStreamReader(new FileInputStream(tmpin)));
//                        while ((map = reader.readLine()) != null) {
//                            if (map.contains(",")) {
//                                String[] parts = map.split(",");
//                                String clz = parts[0];
//                                String name = parts[1];
//                                String str1 = parts[2];
//                                String str2 = parts[3];
//                                String ret = null;
//                                try {
//                                    Method m = targetLoader.loadClass(_decode_class_).getDeclaredMethod(_m_);
//                                    m.setAccessible(true);
//                                    ret = Utils.en((String) m.invoke(null,_argv_));
//                                } catch (Exception e) {
//                                    ret = e.toString();
//                                }
//                                sb.append(clz).append(',').append(name).append(',').append(ret).append('\n');
//                            }
//                        }
//                        reader.close();
//                    }
//                    File outFile = new File("/tmp/out" + Math.random() + ".txt");
//                    outFile.createNewFile();
//                    FileOutputStream fout = new FileOutputStream(outFile);
//                    fout.write(sb.toString().getBytes());
//                    fout.flush();
//                    fout.close();
//                } catch (Exception e) {
//                    log(e);
//                }
//
//            }
//        }).start();
//    }

    @MainProcess
    private void injectStartupHookForMain(Context ctx) {
        injectModuleResources(ctx.getApplicationContext().getResources());
        initForStubActivity(ctx);
        initForJumpActivityEntry(ctx);
        asyncStartFindClass();
        if (LicenseStatus.sDisableCommonHooks) return;
        if (LicenseStatus.hasUserAcceptEula()) hideMiniAppEntry();
    }

    private static String sModulePath = null;

    @MainProcess
    @SuppressLint("PrivateApi")
    public static void injectModuleResources(Resources res) {
        if (res == null) return;
        try {
            res.getString(R.string.res_inject_success);
            return;
        } catch (Resources.NotFoundException ignored) {
        }
        try {
            if (sModulePath == null) {
                String modulePath = null;
                BaseDexClassLoader pcl = (BaseDexClassLoader) MainHook.class.getClassLoader();
                Object pathList = iget_object_or_null(pcl, "pathList");
                Object[] dexElements = (Object[]) iget_object_or_null(pathList, "dexElements");
                for (Object element : dexElements) {
                    File file = (File) iget_object_or_null(element, "path");
                    if (file == null || file.isDirectory())
                        file = (File) iget_object_or_null(element, "zip");
                    if (file == null || file.isDirectory())
                        file = (File) iget_object_or_null(element, "file");
                    if (file != null && !file.isDirectory()) {
                        String path = file.getPath();
                        if (modulePath == null || !modulePath.contains("nil.nadph.qnotified")) {
                            modulePath = path;
                        }
                    }
                }
                if (modulePath == null) {
                    throw new RuntimeException("get module path failed, loader=" + MainHook.class.getClassLoader());
                }
                sModulePath = modulePath;
            }
            AssetManager assets = res.getAssets();
            @SuppressLint("DiscouragedPrivateApi")
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            int cookie = (int) addAssetPath.invoke(assets, sModulePath);
            try {
                logd("injectModuleResources: " + res.getString(R.string.res_inject_success));
            } catch (Resources.NotFoundException e) {
                loge("Fatal: injectModuleResources: test injection failure!");
                loge("injectModuleResources: cookie=" + cookie + ", path=" + sModulePath + ", loader=" + MainHook.class.getClassLoader());
                long length = -1;
                boolean read = false;
                boolean exist = false;
                boolean isDir = false;
                try {
                    File f = new File(sModulePath);
                    exist = f.exists();
                    isDir = f.isDirectory();
                    length = f.length();
                    read = f.canRead();
                } catch (Throwable e2) {
                    log(e2);
                }
                loge("sModulePath: exists = " + exist + ", isDirectory = " + isDir + ", canRead = " + read + ", fileLength = " + length);
            }
        } catch (Exception e) {
            log(e);
        }
    }

    private boolean __stub_hooked = false;

    @MainProcess
    @SuppressLint("PrivateApi")
    private void initForStubActivity(Context ctx) {
        if (__stub_hooked) return;
        try {
            Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz_ActivityThread.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object sCurrentActivityThread = currentActivityThread.invoke(null);
            Field mInstrumentation = clazz_ActivityThread.getDeclaredField("mInstrumentation");
            mInstrumentation.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation) mInstrumentation.get(sCurrentActivityThread);
            mInstrumentation.set(sCurrentActivityThread, new MyInstrumentation(instrumentation));
            //End of Instrumentation
            Field field_mH = clazz_ActivityThread.getDeclaredField("mH");
            field_mH.setAccessible(true);
            Handler oriHandler = (Handler) field_mH.get(sCurrentActivityThread);
            Field field_mCallback = Handler.class.getDeclaredField("mCallback");
            field_mCallback.setAccessible(true);
            Handler.Callback current = (Handler.Callback) field_mCallback.get(oriHandler);
            if (current == null || !current.getClass().getName().equals(MyH.class.getName())) {
                field_mCallback.set(oriHandler, new MyH(current));
            }
            //End of Handler
            Class activityManagerClass;
            Field gDefaultField;
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerClass.getDeclaredField("gDefault");
            } catch (Exception err1) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager");
                    gDefaultField = activityManagerClass.getDeclaredField("IActivityManagerSingleton");
                } catch (Exception err2) {
                    logi("WTF: Unable to get IActivityManagerSingleton");
                    log(err1);
                    log(err2);
                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(gDefault);
            Object proxy = Proxy.newProxyInstance(
                    Initiator.getPluginClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(mInstance));
            mInstanceField.set(gDefault, proxy);
            //End of IActivityManager
            try {
                Class activityTaskManagerClass = Class.forName("android.app.ActivityTaskManager");
                Field fIActivityTaskManagerSingleton = activityTaskManagerClass.getDeclaredField("IActivityTaskManagerSingleton");
                fIActivityTaskManagerSingleton.setAccessible(true);
                Object singleton = fIActivityTaskManagerSingleton.get(null);
                singletonClass.getMethod("get").invoke(singleton);
                Object mDefaultTaskMgr = mInstanceField.get(singleton);
                Object proxy2 = Proxy.newProxyInstance(
                        Initiator.getPluginClassLoader(),
                        new Class[]{Class.forName("android.app.IActivityTaskManager")},
                        new IActivityManagerHandler(mDefaultTaskMgr));
                mInstanceField.set(singleton, proxy2);
            } catch (Exception err3) {
                //log(err3);
                //ignore
            }
            //End of IActivityTaskManager
            __stub_hooked = true;
        } catch (Exception e) {
            log(e);
        }
    }

    public static final String JUMP_ACTION_CMD = "qn_jump_action_cmd";
    public static final String JUMP_ACTION_TARGET = "qn_jump_action_target";
    public static final String JUMP_ACTION_START_ACTIVITY = "nil.nadph.qnotified.START_ACTIVITY";
    public static final String JUMP_ACTION_SETTING_ACTIVITY = "nil.nadph.qnotified.SETTING_ACTIVITY";
    public static final String JUMP_ACTION_REQUEST_SKIP_DIALOG = "nil.nadph.qnotified.REQUEST_SKIP_DIALOG";

    private boolean __jump_act_init = false;

    @MainProcess
    @SuppressLint("PrivateApi")
    private void initForJumpActivityEntry(Context ctx) {
        if (__jump_act_init) return;
        try {
            Class<?> clz = load("com.tencent.mobileqq.activity.JumpActivity");
            if (clz == null) {
                logi("class JumpActivity not found.");
                return;
            }
            Method doOnCreate = clz.getDeclaredMethod("doOnCreate", Bundle.class);
            XposedBridge.hookMethod(doOnCreate, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity activity = (Activity) param.thisObject;
                    Intent intent = activity.getIntent();
                    String cmd;
                    if (intent == null || (cmd = intent.getStringExtra(JUMP_ACTION_CMD)) == null)
                        return;
                    if (JUMP_ACTION_SETTING_ACTIVITY.equals(cmd)) {
                        if (LicenseStatus.sDisableCommonHooks) {
                            long uin = Utils.getLongAccountUin();
                            if (uin > 10000) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            ExfriendManager.getCurrent().doUpdateUserStatusFlags();
                                        } catch (final Exception e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                        }
                                    }
                                }).start();
                            }
                        } else {
                            Intent realIntent = new Intent(intent);
                            realIntent.setComponent(new ComponentName(activity, SettingsActivity.class));
                            activity.startActivity(realIntent);
                        }
                    } else if (JUMP_ACTION_START_ACTIVITY.equals(cmd)) {
                        String target = intent.getStringExtra(JUMP_ACTION_TARGET);
                        if (!TextUtils.isEmpty(target)) {
                            try {
                                Class<?> activityClass = Class.forName(target);
                                Intent realIntent = new Intent(intent);
                                realIntent.setComponent(new ComponentName(activity, activityClass));
                                activity.startActivity(realIntent);
                            } catch (Exception e) {
                                logi("Unable to start Activity: " + e.toString());
                            }
                        }
                    }
                }
            });
            __jump_act_init = true;
        } catch (Exception e) {
            log(e);
        }
    }

    public static class IActivityManagerHandler implements InvocationHandler {
        private final Object mOrigin;

        IActivityManagerHandler(Object origin) {
            mOrigin = origin;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("startActivity".equals(method.getName())) {
                int index = -1;
                for (int i = 0; i < args.length; i++) {
                    if (args[i] instanceof Intent) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    Intent raw = (Intent) args[index];
                    ComponentName component = raw.getComponent();
                    Context hostApp = Utils.getApplication();
                    //log("startActivity, rawIntent=" + raw);
                    if (hostApp != null && component != null
                            && hostApp.getPackageName().equals(component.getPackageName())
                            && (component.getClassName().startsWith("nil.nadph.qnotified.")
                            || component.getClassName().startsWith("me.zpp0196.qqpurify.activity.")
                            || component.getClassName().startsWith("me.singleneuron."))) {
                        boolean isTranslucent = false;
                        try {
                            Class<?> targetActivity = Class.forName(component.getClassName());
                            if (targetActivity != null && (___WindowIsTranslucent.class.isAssignableFrom(targetActivity))) {
                                isTranslucent = true;
                            }
                        } catch (ClassNotFoundException ignored) {
                        }
                        Intent wrapper = new Intent();
                        wrapper.setClassName(component.getPackageName(),
                                isTranslucent ? ActProxyMgr.STUB_TRANSLUCENT_ACTIVITY : ActProxyMgr.STUB_DEFAULT_ACTIVITY);
                        wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, raw);
                        args[index] = wrapper;
                        //log("startActivity, wrap intent with " + wrapper);
                    }
                }
            }
            try {
                return method.invoke(mOrigin, args);
            } catch (InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static class MyInstrumentation extends Instrumentation {
        private final Instrumentation mBase;

        public MyInstrumentation(Instrumentation base) {
            this.mBase = base;
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            try {
                //log("newActivity: " + className);
                return mBase.newActivity(cl, className, intent);
            } catch (Exception e) {
                if (className.startsWith("nil.nadph.qnotified.")
                        || className.startsWith("me.zpp0196.qqpurify.activity.")
                        || className.startsWith("me.singleneuron.")) {
                    return (Activity) Initiator.class.getClassLoader().loadClass(className).newInstance();
                }
                throw e;
            }
        }

        @Override
        public void onCreate(Bundle arguments) {
            mBase.onCreate(arguments);
        }

        @Override
        public void start() {
            mBase.start();
        }

        @Override
        public void onStart() {
            mBase.onStart();
        }

        @Override
        public boolean onException(Object obj, Throwable e) {
            return mBase.onException(obj, e);
        }

        @Override
        public void sendStatus(int resultCode, Bundle results) {
            mBase.sendStatus(resultCode, results);
        }

        @Override
        public void addResults(Bundle results) {
            mBase.addResults(results);
        }

        @Override
        public void finish(int resultCode, Bundle results) {
            mBase.finish(resultCode, results);
        }

        @Override
        public void setAutomaticPerformanceSnapshots() {
            mBase.setAutomaticPerformanceSnapshots();
        }

        @Override
        public void startPerformanceSnapshot() {
            mBase.startPerformanceSnapshot();
        }

        @Override
        public void endPerformanceSnapshot() {
            mBase.endPerformanceSnapshot();
        }

        @Override
        public void onDestroy() {
            mBase.onDestroy();
        }

        @Override
        public Context getContext() {
            return mBase.getContext();
        }

        @Override
        public ComponentName getComponentName() {
            return mBase.getComponentName();
        }

        @Override
        public Context getTargetContext() {
            return mBase.getTargetContext();
        }


        @Override
        public String getProcessName() {
            return mBase.getProcessName();
        }

        @Override
        public boolean isProfiling() {
            return mBase.isProfiling();
        }

        @Override
        public void startProfiling() {
            mBase.startProfiling();
        }

        @Override
        public void stopProfiling() {
            mBase.stopProfiling();
        }

        @Override
        public void setInTouchMode(boolean inTouch) {
            mBase.setInTouchMode(inTouch);
        }

        @Override
        public void waitForIdle(Runnable recipient) {
            mBase.waitForIdle(recipient);
        }

        @Override
        public void waitForIdleSync() {
            mBase.waitForIdleSync();
        }

        @Override
        public void runOnMainSync(Runnable runner) {
            mBase.runOnMainSync(runner);
        }

        @Override
        public Activity startActivitySync(Intent intent) {
            return mBase.startActivitySync(intent);
        }

        @Override
        public Activity startActivitySync(Intent intent, Bundle options) {
            return super.startActivitySync(intent, options);
        }

        @Override
        public void addMonitor(ActivityMonitor monitor) {
            mBase.addMonitor(monitor);
        }

        @Override
        public ActivityMonitor addMonitor(IntentFilter filter, ActivityResult result, boolean block) {
            return mBase.addMonitor(filter, result, block);
        }

        @Override
        public ActivityMonitor addMonitor(String cls, ActivityResult result, boolean block) {
            return mBase.addMonitor(cls, result, block);
        }

        @Override
        public boolean checkMonitorHit(ActivityMonitor monitor, int minHits) {
            return mBase.checkMonitorHit(monitor, minHits);
        }

        @Override
        public Activity waitForMonitor(ActivityMonitor monitor) {
            return mBase.waitForMonitor(monitor);
        }

        @Override
        public Activity waitForMonitorWithTimeout(ActivityMonitor monitor, long timeOut) {
            return mBase.waitForMonitorWithTimeout(monitor, timeOut);
        }

        @Override
        public void removeMonitor(ActivityMonitor monitor) {
            mBase.removeMonitor(monitor);
        }

        @Override
        public boolean invokeMenuActionSync(Activity targetActivity, int id, int flag) {
            return mBase.invokeMenuActionSync(targetActivity, id, flag);
        }

        @Override
        public boolean invokeContextMenuAction(Activity targetActivity, int id, int flag) {
            return mBase.invokeContextMenuAction(targetActivity, id, flag);
        }

        @Override
        public void sendStringSync(String text) {
            mBase.sendStringSync(text);
        }

        @Override
        public void sendKeySync(KeyEvent event) {
            mBase.sendKeySync(event);
        }

        @Override
        public void sendKeyDownUpSync(int key) {
            mBase.sendKeyDownUpSync(key);
        }

        @Override
        public void sendCharacterSync(int keyCode) {
            mBase.sendCharacterSync(keyCode);
        }

        @Override
        public void sendPointerSync(MotionEvent event) {
            mBase.sendPointerSync(event);
        }

        @Override
        public void sendTrackballEventSync(MotionEvent event) {
            mBase.sendTrackballEventSync(event);
        }

        @Override
        public Application newApplication(ClassLoader cl, String className, Context context) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
            return mBase.newApplication(cl, className, context);
        }

        @Override
        public void callApplicationOnCreate(Application app) {
            mBase.callApplicationOnCreate(app);
        }

        @Override
        public Activity newActivity(Class<?> clazz, Context context, IBinder token, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, Object lastNonConfigurationInstance) throws IllegalAccessException, InstantiationException {
            return mBase.newActivity(clazz, context, token, application, intent, info, title, parent, id, lastNonConfigurationInstance);
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle) {
            if (icicle != null) {
                String className = activity.getClass().getName();
                if (className.startsWith("me.zpp0196.qqpurify.activity.")
                        || className.startsWith("me.singleneuron.")) {
                    icicle.setClassLoader(MainHook.class.getClassLoader());
                }
            }
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle);
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
            if (icicle != null) {
                String className = activity.getClass().getName();
                if (className.startsWith("me.zpp0196.qqpurify.activity.")
                        || className.startsWith("me.singleneuron.")) {
                    icicle.setClassLoader(MainHook.class.getClassLoader());
                }
            }
            injectModuleResources(activity.getResources());
            mBase.callActivityOnCreate(activity, icicle, persistentState);
        }

        @Override
        public void callActivityOnDestroy(Activity activity) {
            mBase.callActivityOnDestroy(activity);
        }

        @Override
        public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
            mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState);
        }


        @Override
        public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
            mBase.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
        }

        @Override
        public void callActivityOnPostCreate(Activity activity, Bundle savedInstanceState) {
            mBase.callActivityOnPostCreate(activity, savedInstanceState);
        }

        @Override
        public void callActivityOnPostCreate(Activity activity, @Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
            mBase.callActivityOnPostCreate(activity, savedInstanceState, persistentState);
        }

        @Override
        public void callActivityOnNewIntent(Activity activity, Intent intent) {
            mBase.callActivityOnNewIntent(activity, intent);
        }

        @Override
        public void callActivityOnStart(Activity activity) {
            mBase.callActivityOnStart(activity);
        }

        @Override
        public void callActivityOnRestart(Activity activity) {
            mBase.callActivityOnRestart(activity);
        }

        @Override
        public void callActivityOnResume(Activity activity) {
            mBase.callActivityOnResume(activity);
        }

        @Override
        public void callActivityOnStop(Activity activity) {
            mBase.callActivityOnStop(activity);
        }

        @Override
        public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
            mBase.callActivityOnSaveInstanceState(activity, outState);
        }

        @Override
        public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
            mBase.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
        }

        @Override
        public void callActivityOnPause(Activity activity) {
            mBase.callActivityOnPause(activity);
        }

        @Override
        public void callActivityOnUserLeaving(Activity activity) {
            mBase.callActivityOnUserLeaving(activity);
        }

        @Override
        public void startAllocCounting() {
            mBase.startAllocCounting();
        }

        @Override
        public void stopAllocCounting() {
            mBase.stopAllocCounting();
        }

        @Override
        public Bundle getAllocCounts() {
            return mBase.getAllocCounts();
        }

        @Override
        public Bundle getBinderCounts() {
            return mBase.getBinderCounts();
        }

        @Override
        public UiAutomation getUiAutomation() {
            return mBase.getUiAutomation();
        }

        @Override
        public UiAutomation getUiAutomation(int flags) {
            return mBase.getUiAutomation(flags);
        }

        @Override
        public TestLooperManager acquireLooperManager(Looper looper) {
            return mBase.acquireLooperManager(looper);
        }

    }

    public static class MyH implements Handler.Callback {
        private final Handler.Callback mDefault;

        public MyH(Handler.Callback def) {
            mDefault = def;
        }

        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 100) { // LAUNCH_ACTIVITY
                try {
                    Object record = msg.obj;
                    Field field_intent = record.getClass().getDeclaredField("intent");
                    field_intent.setAccessible(true);
                    Intent intent = (Intent) field_intent.get(record);
                    //log("handleMessage/100: " + intent);
                    Bundle bundle = null;
                    try {
                        Field fExtras = Intent.class.getDeclaredField("mExtras");
                        fExtras.setAccessible(true);
                        bundle = (Bundle) fExtras.get(intent);
                    } catch (Exception e) {
                        log(e);
                    }
                    if (bundle != null) {
                        bundle.setClassLoader(Initiator.getHostClassLoader());
                        //we do NOT have a custom Bundle, but the host may have
                        if (intent.hasExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT)) {
                            Intent realIntent = intent.getParcelableExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT);
                            field_intent.set(record, realIntent);
                            //log("unwrap, real=" + realIntent);
                        }
                    }
                } catch (Exception e) {
                    log(e);
                }
            } else if (msg.what == 159) {
                // EXECUTE_TRANSACTION
                Object clientTransaction = msg.obj;
                try {
                    if (clientTransaction != null) {
                        Method getCallbacks = Class.forName("android.app.servertransaction.ClientTransaction").getDeclaredMethod("getCallbacks");
                        getCallbacks.setAccessible(true);
                        List clientTransactionItems = (List) getCallbacks.invoke(clientTransaction);
                        if (clientTransactionItems != null && clientTransactionItems.size() > 0) {
                            for (Object item : clientTransactionItems) {
                                Class c = item.getClass();
                                if (c.getName().contains("LaunchActivityItem")) {
                                    Field fmIntent = c.getDeclaredField("mIntent");
                                    fmIntent.setAccessible(true);
                                    Intent wrapper = (Intent) fmIntent.get(item);
                                    Bundle bundle = null;
                                    try {
                                        Field fExtras = Intent.class.getDeclaredField("mExtras");
                                        fExtras.setAccessible(true);
                                        bundle = (Bundle) fExtras.get(wrapper);
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                    if (bundle != null) {
                                        bundle.setClassLoader(Initiator.getHostClassLoader());
                                        if (wrapper.hasExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT)) {
                                            Intent realIntent = wrapper.getParcelableExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT);
                                            fmIntent.set(item, realIntent);
                                            //log("unwrap, real=" + realIntent);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log(e);
                }
            }
            if (mDefault != null) {
                return mDefault.handleMessage(msg);
            }
            return false;
        }
    }

    private void hideMiniAppEntry() {
        try {
            if (Utils.isTim(getApplication())) return;
        } catch (Throwable ignored) {
        }
        try {
            ConfigManager cache = ConfigManager.getCache();
            if (ConfigManager.getDefaultConfig().getBooleanOrFalse(ConfigItems.qn_hide_msg_list_miniapp)) {
                int lastVersion = cache.getIntOrDefault("qn_hide_msg_list_miniapp_version_code", 0);
                if (getHostVersionCode32() == lastVersion) {
                    String methodName = cache.getString("qn_hide_msg_list_miniapp_method_name");
                    findAndHookMethod(Initiator._Conversation(), methodName, XC_MethodReplacement.returnConstant(null));
                } else {
                    Class con = Initiator._Conversation();
                    for (Method m : con.getDeclaredMethods()) {
                        Class[] ps = m.getParameterTypes();
                        if (ps != null && ps.length > 0) continue;
                        if (!m.getReturnType().equals(void.class)) continue;
                        String name = m.getName();
                        if (name.length() > 1) continue;
                        char c = name.charAt(0);
                        if ('F' <= c && c < 'a')
                            XposedBridge.hookMethod(m, new XC_MethodReplacement(30) {
                                @Override
                                protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                                    try {
                                        Method m = (Method) param.method;
                                        m.setAccessible(true);
                                        XposedBridge.invokeOriginalMethod(m, param.thisObject, param.args);
                                    } catch (InvocationTargetException e) {
                                        if (!(e.getCause() instanceof UnsupportedOperationException)) {
                                            log(e);
                                        }
                                    } catch (Throwable t) {
                                        log(t);
                                    }
                                    return null;
                                }
                            });
                    }
					/*try {
					 findAndHookMethod(load("com.tencent.mobileqq.app.FrameFragment"), "createTabContent", String.class, new XC_MethodReplacement(39) {
					 @Override
					 protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
					 try {
					 Method m = (Method) param.method;
					 m.setAccessible(true);
					 XposedBridge.invokeOriginalMethod(m, param.thisObject, param.args);
					 } catch (UnsupportedOperationException e) {
					 } catch (Throwable t) {
					 log(t);
					 }
					 return null;
					 }
					 });
					 } catch (Exception e) {}*/

                    Class tmp;
                    Class miniapp = null;
                    if (Utils.getHostVersionCode32() >= 1312) {
                        //for 8.2.6
                        miniapp = load("com/tencent/mobileqq/mini/entry/MiniAppDesktop");
                        if (miniapp == null) {
                            tmp = load("com/tencent/mobileqq/mini/entry/MiniAppDesktop$1");
                            if (tmp != null) miniapp = tmp.getDeclaredField("this$0").getType();
                        }
                    } else {
                        //for 818
                        try {
                            miniapp = load("com.tencent.mobileqq.mini.entry.desktop.MiniAppDesktopLayout");
                            if (miniapp == null) {
                                tmp = load("com.tencent.mobileqq.mini.entry.desktop.MiniAppDesktopLayout$1");
                                if (tmp != null) miniapp = tmp.getDeclaredField("this$0").getType();
                            }
                            if (miniapp == null) {
                                tmp = load("com.tencent.mobileqq.mini.entry.desktop.MiniAppDesktopLayout$2");
                                if (tmp != null) miniapp = tmp.getDeclaredField("this$0").getType();
                            }
                        } catch (Exception ignored) {
                        }
                        //for older
                        if (miniapp == null)
                            miniapp = load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter");
                        if (miniapp == null)
                            miniapp = load("com/tencent/mobileqq/mini/entry/MiniAppEntryAdapter$1").getDeclaredField("this$0").getType();
                    }
                    XposedBridge.hookAllConstructors(miniapp, new XC_MethodHook(60) {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            String methodName = null;
                            StackTraceElement[] stacks = new Throwable().getStackTrace();
                            for (int i = 0; i < stacks.length; i++) {
                                if (stacks[i].getClassName().contains("Conversation")) {
                                    methodName = stacks[i].getMethodName();
                                    break;
                                }
                            }
                            if (methodName == null)
                                throw new NullPointerException("Failed to get Conversation.?() to hide MiniApp!");
                            ConfigManager cache = ConfigManager.getCache();
                            cache.putString("qn_hide_msg_list_miniapp_method_name", methodName);
                            cache.getAllConfig().put("qn_hide_msg_list_miniapp_version_code", getHostVersionCode32());
                            cache.save();
                            param.setThrowable(new UnsupportedOperationException("MiniAppEntry disabled"));
                        }
                    });
                }
            }
        } catch (Throwable e) {
            log(e);
        }
    }

    private void asyncStartFindClass() {
        if (DexKit.loadClassFromCache(DexKit.C_DIALOG_UTIL) == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ignored) {
                    }
                    DexKit.doFindClass(DexKit.C_DIALOG_UTIL);
                }
            }).start();
        if (DexKit.loadClassFromCache(DexKit.C_FACADE) == null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException ignored) {
                    }
                    DexKit.doFindClass(DexKit.C_FACADE);
                }
            }).start();
    }

    /**
     * dummy method, for development and test only
     */
    public static void onAppStartupForMain() {
        if (!isAlphaVersion()) return;
        deepDarkTheme();
    }

}
