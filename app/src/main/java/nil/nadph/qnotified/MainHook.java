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
import android.os.*;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.List;

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

//    public static void startFakeString() {
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
//    }

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
                    //log("startActivity, rawIntent=" + raw);
                    if (component != null &&
                            component.getClassName().startsWith("nil.nadph.qnotified.")) {
                        Intent wrapper = new Intent();
                        wrapper.setClassName(component.getPackageName(), ActProxyMgr.STUB_ACTIVITY);
                        wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, raw);
                        args[index] = wrapper;
                        //log("startActivity, wrap intent with " + wrapper);
                    }
                }
            }
            return method.invoke(mOrigin, args);
        }
    }

    @SuppressLint("NewApi")
    public static class MyInstrumentation extends Instrumentation {
        private Instrumentation mBase;

        public MyInstrumentation(Instrumentation base) {
            this.mBase = base;
        }

        @Override
        public Activity newActivity(ClassLoader cl, String className, Intent intent) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
            try {
                //log("newActivity: " + className);
                return mBase.newActivity(cl, className, intent);
            } catch (Exception e) {
                if (className.startsWith("nil.nadph.qnotified.")) {
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
            mBase.callActivityOnCreate(activity, icicle);
        }

        @Override
        public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
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
                        bundle.setClassLoader(Initiator.getPluginClassLoader());
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
                                        bundle.setClassLoader(Initiator.getPluginClassLoader());
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
        } catch (Exception ignored) {
        }
        try {
            ConfigManager cache = ConfigManager.getCache();
            if (ConfigManager.getDefaultConfig().getBooleanOrFalse(ConfigItems.qn_hide_msg_list_miniapp)) {
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
    }

}
