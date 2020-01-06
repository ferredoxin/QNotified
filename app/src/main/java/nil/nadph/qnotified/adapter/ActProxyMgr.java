package nil.nadph.qnotified.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.util.QThemeKit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Stack;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

/**
 * ActivityProxyManager
 */
public class ActProxyMgr extends XC_MethodHook {

    public static final String STUB_ACTIVITY = "com/tencent/mobileqq/activity/photo/CameraPreviewActivity";
    public static final String ACTIVITY_PROXY_ID_TAG = "qn_act_proxy_id";
    public static final String ACTIVITY_PROXY_ACTION = "qn_act_proxy_action";
    public static final int ACTION_EXFRIEND_LIST = 1;
    public static final int ACTION_ADV_SETTINGS = 2;
    public static final int ACTION_ABOUT = 3;
    public static final int ACTION_SHELL = 4;
    public static final int ACTION_MUTE_AT_ALL = 5;
    public static final int ACTION_MUTE_RED_PACKET = 6;
    public static final int ACTION_DONATE_ACTIVITY = 7;
    public static final int ACTION_TROUBLESHOOT_ACTIVITY = 8;
    public static final int ACTION_FRIENDLIST_EXPORT_ACTIVITY = 9;

    /**
     * HashSet mThreads<Long threadId> :Ids of threads which is calling invokeSuper
     * You may wonder what this field is for,
     * This is to suppress Xposed's endless hook recursion
     * beforeHookMethod->invokeSuper->beforeHookedMethod->invokeSuper->...->StackOverflow :p
     * see isInfiniteLoop():Z
     **/
    private HashMap<Long, Stack<Member>> mThreadStack = new HashMap<>();
    private HashMap<Member, StackBreakHook> hooks = new HashMap<>();

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        if (ActProxyMgr.isInfiniteLoop()) return;
        final Activity self = (Activity) param.thisObject;
        Bundle b = self.getIntent().getExtras();
        if (b == null) return;
        int id = b.getInt(ACTIVITY_PROXY_ID_TAG, -1);
        int action = b.getInt(ACTIVITY_PROXY_ACTION, -1);
        if (id <= 0 || action <= 0) return;
        param.setResult(null);
        ActivityAdapter aa;
        Method method = (Method) param.method;
        try {
            if (method.getName().equals("onCreate") && param.args.length == 1) {
                Method m = load("mqq/app/AppActivity").getDeclaredMethod("onCreate", Bundle.class);
                m.setAccessible(true);
                try {
                    ActProxyMgr.invokeSuper(self, m, param.args);
                } catch (ActProxyMgr.BreakUnaughtException ignored) {
                }
                aa = createActivityAdapter(action, self);
                try {
                    QThemeKit.initTheme(self);
                    Object exlist_mFlingHandler = new_instance(load("com/tencent/mobileqq/activity/fling/FlingGestureHandler"), self, Activity.class);
                    iput_object(self, "mFlingHandler", exlist_mFlingHandler);
                } catch (Throwable e) {
                    log(e);
                }
                aa.doOnPostCreate((Bundle) param.args[0]);
                self.getWindow().getDecorView().setTag(aa);
            } else {
                aa = (ActivityAdapter) self.getWindow().getDecorView().getTag();
                switch (method.getName()) {
                    case "doOnDestroy":
                        Method m = getSuperMethod(self.getClass(), "doOnDestroy");
                        m.setAccessible(true);
                        try {
                            ActProxyMgr.invokeSuper(self, m);
                        } catch (ActProxyMgr.BreakUnaughtException ignored) {
                        }
                        aa.doOnPostDestory();
                        break;
                    case "doOnPause":
                        m = getSuperMethod(self.getClass(), "doOnPause");
                        m.setAccessible(true);
                        try {
                            ActProxyMgr.invokeSuper(self, m);
                        } catch (ActProxyMgr.BreakUnaughtException ignored) {
                        }
                        aa.doOnPostPause();
                        break;
                    case "doOnResume":
                        m = getSuperMethod(self.getClass(), "doOnResume");
                        m.setAccessible(true);
                        try {
                            ActProxyMgr.invokeSuper(self, m);
                        } catch (ActProxyMgr.BreakUnaughtException ignored) {
                        }
                        aa.doOnPostResume();
                        break;
                    case "doOnActivityResult":
                        m = getSuperMethod(self.getClass(), "doOnActivityResult", int.class, int.class, Intent.class);
                        m.setAccessible(true);
                        try {
                            ActProxyMgr.invokeSuper(self, m, param.args);
                        } catch (ActProxyMgr.BreakUnaughtException ignored) {
                        }
                        aa.doOnPostActivityResult((int) param.args[0], (int) param.args[1], (Intent) param.args[2]);
                        break;
                    case "isWrapContent":
                        param.setResult(aa.isWrapContent());
                        break;
                    default:
                        throw new UnsupportedOperationException("Unexpected method: " + method.getName());
                }
            }
        } catch (Throwable th) {
            param.setThrowable(th);
            log(th);
        }
    }

    private ActivityAdapter createActivityAdapter(int action, Activity activity) {
        switch (action) {
            case ACTION_EXFRIEND_LIST:
                return new ExfriendListAdapter(activity);
            case ACTION_ADV_SETTINGS:
                return new SettingsAdapter(activity);
            case ACTION_MUTE_AT_ALL:
            case ACTION_MUTE_RED_PACKET:
                return new TroopSelectAdapter(activity, action);
            case ACTION_ABOUT:
                return new AboutActivity(activity);
            case ACTION_DONATE_ACTIVITY:
                return new DonateActivity(activity);
            case ACTION_TROUBLESHOOT_ACTIVITY:
                return new TroubleshootActivity(activity);
            case ACTION_FRIENDLIST_EXPORT_ACTIVITY:
                return new FriendlistExportActivity(activity);
            default:
                throw new UnsupportedOperationException("Unknown action " + action);
        }
    }

    public static void setContentBackgroundDrawable(Activity activity, Drawable d) {
        try {
            ((View) activity.findViewById(16908290)).setBackgroundDrawable(d);
        } catch (NullPointerException e) {
            log(e);
        }
    }

    private int next_uuid = 1;

    public static int next() {
        return instance.next_uuid++;
    }

    public static ActProxyMgr getInstance() {
        return instance;
    }

    public static boolean isInfiniteLoop() {
        try {
            long tid = Thread.currentThread().getId();
            Stack<Member> s = instance.mThreadStack.get(tid);
            return !s.empty();
        } catch (Throwable e) {
            //.log(e);
            return false;
        }
    }

    /**
     * invoke the super method, for Context life-cycle only
     *
     * @param obj:     thisObject,should be null if it's a static method
     * @param smethod: the SUPER method(you must get it from its super class) you want to invoke
     * @param args:
     * @return the return value(if it has,or null)
     * @throws InvocationTargetException TODO: Replace this fragile method with JNI or other method
     * @deprecated
     */
    @Deprecated
    public static Object invokeSuper(android.app.Activity obj, Method smethod, Object... args) throws Throwable {
        Object ret;
        try {
            synchronized (instance) {
                StackBreakHook hook = instance.hooks.get(smethod);
                if (hook == null) {
                    hook = new StackBreakHook(smethod);
                    instance.hooks.put(smethod, hook);
                    XposedBridge.hookMethod(smethod, hook);
                }
                smethod.setAccessible(true);
                Stack<Member> stack = instance.mThreadStack.get(Thread.currentThread().getId());
                if (stack == null) {
                    stack = new Stack<>();
                    instance.mThreadStack.put(Thread.currentThread().getId(), stack);
                }
                //log("invokeSuper("+smethod.getName()+"),tid="+Thread.currentThread().getId()+",stack_before="+stack);
                stack.push(smethod);
            }
            smethod.invoke(obj, args);
            //XposedBridge.invokeOriginalMethod(smethod,obj,args);This doesn't work!!!
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof StackBreak) {
                //Utils.log("success!");
                //return ((StackBreak)e.getCause()).returnValue;
            } else {
                throw e;
            }
        } finally {
            synchronized (instance) {
                /*if(unhook!=null)unhook.unhook();*/
                Stack<Member> s = instance.mThreadStack.get(Thread.currentThread().getId());
                ret = s.pop();
            }
        }
        return ret;
    }

    private static final ActProxyMgr instance = new ActProxyMgr();


    public static class StackBreak extends Throwable {/* This is NOT Exception,NOT to be caught! */

        public StackBreak(Object ret) {
            returnValue = ret;
        }

        public Object returnValue;
    }

    public static class BreakUnaughtException extends Exception {
    }

    private static class StackBreakHook extends XC_MethodHook {
        public Member method;

        public StackBreakHook(Member m) {
            super(65535);
            method = m;
        }

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            try {
                //Utils.log("setEx");
                if (param.hasThrowable()) return;//Sth has already went wrong;don't cope with it
                long threadId = Thread.currentThread().getId();//make sure shit won't happen
                synchronized (instance) {
                    Stack s = instance.mThreadStack.get(threadId);
                    //log("invokeSuper_end("+method.getName()+"),tid="+Thread.currentThread().getId()+",stack="+s);
                    if (s.empty()) return;
                    if (!method.equals(s.peek())) return;
                    StackBreak sb = new StackBreak(param.getResult());
                    s.pop();
                    s.push(param.getResult());
                    param.setThrowable(sb);
                }
            } catch (Throwable e) {
                log(e);
                throw e;
            }
        }

    }
}
