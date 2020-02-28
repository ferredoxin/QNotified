package nil.nadph.qnotified.hook;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.*;

public class RoundAvatarHook extends BaseDelayableHook {
    public static final String qn_round_avatar = "qn_round_avatar";
    private static final RoundAvatarHook self = new RoundAvatarHook();
    private boolean inited = false;

    RoundAvatarHook() {
    }

    public static RoundAvatarHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method a = null, b = null;
            Class clz = DexKit.doFindClass(DexKit.C_SIMPLE_UI_UTIL);
            for (Method m : clz.getDeclaredMethods()) {
                if (!boolean.class.equals(m.getReturnType())) continue;
                Class[] argt = m.getParameterTypes();
                if (argt.length != 1) continue;
                if (String.class.equals(argt[0])) {
                    if (m.getName().equals("a")) a = m;
                    if (m.getName().equals("b")) b = m;
                }
            }
            XC_MethodHook hook = new XC_MethodHook(43) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (!ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_round_avatar)) return;
                    } catch (Throwable e) {
                        log(e);
                    }
                    param.setResult(false);
                }
            };
            if (b != null) {
                XposedBridge.hookMethod(b, hook);
            } else {
                XposedBridge.hookMethod(a, hook);
            }
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_SIMPLE_UI_UTIL};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isValid() {
        Application app = getApplication();
        return app == null || !isTim(app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_round_avatar, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_round_avatar);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}