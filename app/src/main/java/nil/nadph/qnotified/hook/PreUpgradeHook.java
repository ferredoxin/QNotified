package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator._UpgradeController;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.qh_pre_upgrade;

public class PreUpgradeHook extends BaseDelayableHook {
    private PreUpgradeHook() {
    }

    private static final PreUpgradeHook self = new PreUpgradeHook();

    public static PreUpgradeHook get() {
        return self;
    }

    private boolean inited = false;

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
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qh_pre_upgrade);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}