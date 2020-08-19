package nil.nadph.qnotified.hook.rikka;

import android.os.Looper;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.*;

//去除小程序广告 需要手动点关闭
public class RemoveMiniProgramAd extends BaseDelayableHook {
    public static final String rq_remove_mini_program_ad = "rq_remove_mini_program_ad";
    private static final RemoveMiniProgramAd self = new RemoveMiniProgramAd();
    private boolean isInit = false;

    public static RemoveMiniProgramAd get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_ANY & ~(SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF | SyncUtils.PROC_QZONE
                | SyncUtils.PROC_PEAK | SyncUtils.PROC_VIDEO);
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            for (Method m : Initiator._GdtMvViewController().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("x") && argt.length == 0) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            Utils.iput_object(param.thisObject, "c", Boolean.TYPE, true);
                        }
                    });
                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_remove_mini_program_ad, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_remove_mini_program_ad);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
