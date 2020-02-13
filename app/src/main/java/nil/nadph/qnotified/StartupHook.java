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
                        Context ctx = null;
                        Class clz = param.thisObject.getClass().getClassLoader().loadClass("com.tencent.common.app.BaseApplicationImpl");
                        final Field f = hasField(clz, "sApplication");
                        if (f == null) ctx = (Context) sget_object(clz, "a", clz);
                        else ctx = (Context) f.get(null);
                        ClassLoader classLoader = ctx.getClassLoader();
                        if (classLoader == null) throw new AssertionError("ERROR: classLoader == null");
                        Initiator.init(classLoader);
                        if ("true".equals(System.getProperty(QN_FULL_TAG))) {
                            log("Err:QNotified reloaded??");
                            //I don't know... What happened?
                            return;
                            //System.exit(-1);
                            //QNotified updated(in HookLoader mode),kill QQ to make user restart it.
                        }
                        MainHook.getInstance().performHook(ctx);
                        System.setProperty(QN_FULL_TAG, "true");
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
            //findAndHookMethodIfExists("com.tencent.common.app.QFixApplicationImpl", lpparam.classLoader, "isAndroidNPatchEnable", XC_MethodReplacement.returnConstant(500, false));
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
