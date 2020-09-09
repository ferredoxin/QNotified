package nil.nadph.qnotified.util.internal;

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
            M_XCM_beforeHookedMethod = XC_MethodHook.class.getDeclaredMethod("beforeHookedMethod", XC_MethodHook.MethodHookParam.class);
            M_XCM_beforeHookedMethod.setAccessible(true);
            M_XCM_afterHookedMethod = XC_MethodHook.class.getDeclaredMethod("afterHookedMethod", XC_MethodHook.MethodHookParam.class);
            M_XCM_afterHookedMethod.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new UnsupportedOperationException("MethodHookParam.returnEarly not found, API: " + XposedBridge.getXposedVersion());
        } catch (NoSuchMethodException e) {
            throw new UnsupportedOperationException("MethodHookParam.<init>() not found, API: " + XposedBridge.getXposedVersion());
        }
    }


    public static XC_MethodHook.MethodHookParam createParam(XC_MethodHook hook, Method method, Object thisObj, Object... argv) {
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
        if (hook == null || param == null) return false;
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
        if (hook == null || param == null) return;
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
        public HookHolder() {
        }

        public HookHolder(XC_MethodHook h, Method m) {
            hook = h;
            method = m;
        }

        public XC_MethodHook hook;
        public Method method;
    }

}
