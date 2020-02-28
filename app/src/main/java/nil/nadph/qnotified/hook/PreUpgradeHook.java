package nil.nadph.qnotified.hook;

import android.os.Looper;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator._UpgradeController;
import static nil.nadph.qnotified.util.Utils.*;

public class PreUpgradeHook extends BaseDelayableHook {
    public static final String qh_pre_upgrade = "qh_pre_upgrade";
    private static final PreUpgradeHook self = new PreUpgradeHook();
    private boolean inited = false;

    private PreUpgradeHook() {
    }

    public static PreUpgradeHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            for (Method m : _UpgradeController().getDeclaredMethods()) {
                if (m.getParameterTypes().length != 0) continue;
                if (Modifier.isStatic(m.getModifiers())) continue;
                if (!m.getName().equals("a")) continue;
                if (m.getReturnType().getName().contains("UpgradeDetailWrapper")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook(43) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                        }
                    });
                    break;
                }
            }
            /*Method method1 = getMethod(_BannerManager(), "n", View.class);
            if (method1 != null) {
                XposedBridge.hookMethod(method1, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });
            }*/
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
        return new int[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qh_pre_upgrade, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qh_pre_upgrade);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}