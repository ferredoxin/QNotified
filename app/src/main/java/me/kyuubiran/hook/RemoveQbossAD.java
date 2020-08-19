package me.kyuubiran.hook;

import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.log;

//移除消息列表顶栏横幅广告
public class RemoveQbossAD extends BaseDelayableHook {
    public static final String kr_remove_qboss_ad = "kr_remove_qboss_ad";
    private static final RemoveQbossAD self = new RemoveQbossAD();
    private boolean isInit = false;

    public static RemoveQbossAD get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            for (Method m : Initiator._QbossADImmersionBannerManager().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getReturnType() == View.class && argt.length == 0 && !Modifier.isStatic(m.getModifiers())) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
//                            Toast.makeText(getApplication(), "屏蔽消息列表横幅广告成功!", android.widget.Toast.LENGTH_SHORT).show();
                            param.setResult(null);
                        }
                    });
                }
            }
            isInit = true;
            return true;
        } catch (Throwable t) {
            Utils.log(t);
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
            mgr.getAllConfig().put(kr_remove_qboss_ad, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_remove_qboss_ad);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
