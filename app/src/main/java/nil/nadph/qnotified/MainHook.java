package nil.nadph.qnotified;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
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
            Utils.log(ret.toString());
            ret = new StringBuilder("↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n");
            Field[] fs = m.getDeclaringClass().getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                fs[i].setAccessible(true);
                ret.append(i < fs.length - 1 ? "├" : "↓").append(fs[i].getName()).append("=").append(Utils.en_toStr(fs[i].get(param.thisObject))).append("\n");
            }
            log(ret.toString());
            Utils.dumpTrace();
        }
    };
    private static MainHook SELF;
    public static WeakReference<Activity> splashActivityRef;


    private boolean third_stage_inited = false;

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
            log(e.toString());
            return null;
        }
    }

    public static XC_MethodHook.Unhook findAndHookMethodIfExists(String clazzName, ClassLoader cl, String
            methodName, Object... parameterTypesAndCallback) {
        try {
            return findAndHookMethod(clazzName, cl, methodName, parameterTypesAndCallback);
        } catch (Throwable e) {
            log(e.toString());
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

    public static void deepDarkTheme() {
        if (!SyncUtils.isMainProcess()) return;
        if (getLongAccountUin() != 1041703712) return;
        try {
            Class clz = load("com/tencent/mobileqq/activity/FriendProfileCardActivity");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = (FrameLayout) ctx.findViewById(android.R.id.content);
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
                    FrameLayout frame = (FrameLayout) ctx.findViewById(android.R.id.content);
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
                                        FrameLayout frame = (FrameLayout) ctx.findViewById(android.R.id.content);
                                        frame.getChildAt(0).setBackgroundColor(0xFF000000);
                                        ViewGroup list = (ViewGroup) ctx.findViewById(ctx.getResources().getIdentifier("common_xlistview", "id", ctx.getPackageName()));
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
                    FrameLayout frame = (FrameLayout) ctx.findViewById(android.R.id.content);
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
        if (SyncUtils.isMainProcess()) {
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
                    if (SyncUtils.isMainProcess()) {
                        ResUtils.loadThemeByArsc(getApplication(), false);
                    }
                    InjectDelayableHooks.step(dir);
                    onAppStartupForMain();
                    third_stage_inited = true;
                }
            });
        } else {
            Class director = _StartupDirector();
            Object dir = iget_object_or_null(step, "mDirector", director);
            if (dir == null) dir = iget_object_or_null(step, "a", director);
            InjectDelayableHooks.step(dir);
        }

    }

    public static void startFakeString() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(10_000);
//                } catch (InterruptedException e) {
//                }
//                if (SyncUtils.getProcessType() != 1) return;
//                Map sHookedMethodCallbacks = (Map) sget_object(XposedBridge.class, "sHookedMethodCallbacks");
//                a:
//                for (Map.Entry ent : sHookedMethodCallbacks.entrySet()) {
//                    Object[] elem = (Object[]) iget_object_or_null(ent.getValue(), "elements");
//                    for (Object cb : elem) {
//                        Class hook = cb.getClass();
//                        if (hook.getName().contains(_target_)) {
//                            ClassLoader cl = hook.getClassLoader();
//                            try {
//                                Class de = cl.loadClass(_decoder_.replace(" ", ""));
//                                Method[] ms = de.getDeclaredMethods();
//                                Method m = null;
//                                for (Method mi : ms) {
//                                    if (mi.getReturnType().equals(String.class)) {
//                                        m = mi;
//                                        m.setAccessible(true);
//                                    }
//                                }
//                                StringBuilder fout = new StringBuilder();
//                                for (int i = 0; i < 4000; i++) {
//                                    String ret = null;
//                                    try {
//                                        ret = (String) m.invoke(null, i);
//                                    } catch (Exception e) {
//                                        ret = null;
//                                    }
//                                    ret = Utils.en(ret);
//                                    fout.append(ret);
//                                    fout.append('\n');
//                                }
//                                FileOutputStream f2out = new FileOutputStream(_out_);
//                                f2out.write(fout.toString().getBytes());
//                                f2out.flush();
//                                f2out.close();
//                            } catch (Exception e) {
//                                log(e);
//                            }
//                        }
//                    }
//                }
//            }
//        }).start();
    }

    @MainProcess
    private void injectStartupHookForMain(Context ctx) {
        initForStubActivity(ctx);
        asyncStartFindClass();
        hideMiniAppEntry();
    }

    public boolean __stub_hooked = false;

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
                    log("WTF: Unable to get IActivityManagerSingleton");
                    log(err1);
                    log(err2);
                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            Object mInstance = mInstanceField.get(gDefault);
            Object proxy = Proxy.newProxyInstance(
                    mInstance.getClass().getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(mInstance));
            mInstanceField.set(gDefault, proxy);
            //End of IActivityManager
            __stub_hooked = true;
        } catch (Exception e) {
            log(e);
        }
    }

    public static class IActivityManagerHandler implements InvocationHandler {
        private Object mOrigin;

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
                    log("startActivity, rawIntent=" + raw);
                    if (component != null &&
                            component.getClassName().startsWith("nil.nadph.qnotified.")) {
                        Intent wrapper = new Intent();
                        wrapper.setClassName(component.getPackageName(), ActProxyMgr.STUB_ACTIVITY);
                        wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, raw);
                        args[index] = wrapper;
                        log("startActivity, wrap intent with " + wrapper);
                    }
                }
            }
            return method.invoke(mOrigin, args);
        }
    }

    public static class MyInstrumentation extends Instrumentation {
        private Instrumentation base;

        public MyInstrumentation(Instrumentation base) {
            this.base = base;
            try {
                for (Field f : Instrumentation.class.getDeclaredFields()) {
                    f.setAccessible(true);
                    f.set(this, f.get(base));
                }
            } catch (Exception e) {
                log(e);
            }
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            try {
                log("newActivity: " + className);
                return base.newActivity(cl, className, intent);
            } catch (Exception e) {
                if (className.startsWith("nil.nadph.qnotified.")) {
                    return (Activity) Initiator.class.getClassLoader().loadClass(className).newInstance();
                }
                throw e;
            }
        }
    }

    public static class MyH implements Handler.Callback {
        private Handler.Callback mDefault;

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
                    log("handleMessage/100: " + intent);
                    Bundle bundle = null;
                    try {
                        Field fExtras = Intent.class.getDeclaredField("mExtras");
                        fExtras.setAccessible(true);
                        bundle = (Bundle) fExtras.get(intent);
                    } catch (Exception e) {
                        log(e);
                    }
                    if (bundle != null) {
                        bundle.setClassLoader(Initiator.getClassLoader());
                        if (intent.hasExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT)) {
                            Intent realIntent = intent.getParcelableExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT);
                            field_intent.set(record, realIntent);
                            log("unwrap, real=" + realIntent);
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
        } catch (Exception ignored) {
        }
        try {
            ConfigManager cache = ConfigManager.getCache();
            if (ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_hide_msg_list_miniapp)) {
                int lastVersion = cache.getIntOrDefault("qn_hide_msg_list_miniapp_version_code", 0);
                if (getHostInfo(getApplication()).versionCode == lastVersion) {
                    String methodName = cache.getString("qn_hide_msg_list_miniapp_method_name");
                    findAndHookMethod(load("com/tencent/mobileqq/activity/Conversation"), methodName, XC_MethodReplacement.returnConstant(null));
                } else {
                    Class con = load("com/tencent/mobileqq/activity/Conversation");
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
                    if (Utils.getHostVersionCode() >= 1312) {
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
                            cache.getAllConfig().put("qn_hide_msg_list_miniapp_version_code", getHostInfo(getApplication()).versionCode);
                            cache.save();
                            param.setThrowable(new UnsupportedOperationException("MiniAppEntry disabled"));
                        }
                    });
                }
            }
        } catch (Exception e) {
            log(e);
        }
    }

    private void asyncStartFindClass() {
        if (DexKit.tryLoadOrNull(DexKit.C_DIALOG_UTIL) == null)
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
        if (DexKit.tryLoadOrNull(DexKit.C_FACADE) == null)
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
        XposedHelpers.findAndHookMethod(load("awwp"), "a", Context.class, View.class, boolean.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity ctx = (Activity) param.args[0];
                Object fmgr = invoke_virtual(ctx, "getSupportFragmentManager");
                Object fragment = invoke_virtual(fmgr, "findFragmentByTag", "com.tencent.mobileqq.activity.ChatFragment", String.class);
                Object chatpie = invoke_virtual(fragment, "a", load("com.tencent.mobileqq.activity.BaseChatPie"));
                Object ackb = iget_object_or_null(chatpie, "a", load("ackb"));
                Object ackc = iget_object_or_null(ackb, "a", load("ackc"));
                View v = (View) param.getResult();
                if (v != null) {
                    v.setOnLongClickListener((View.OnLongClickListener) ackc);
                }
            }

        });
    }

}
