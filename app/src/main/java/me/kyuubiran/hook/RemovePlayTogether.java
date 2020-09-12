package me.kyuubiran.hook;

import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;
import me.singleneuron.util.QQVersion;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Initiator.load;

//屏蔽群聊界面一起嗨
public class RemovePlayTogether extends BaseDelayableHook {
    public static final String kr_remove_play_together = "kr_remove_play_together";
    private static final RemovePlayTogether self = new RemovePlayTogether();
    private boolean isInit = false;

    public static RemovePlayTogether get() {
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
            String method = "h";
            if (Utils.getHostVersionCode() >= QQVersion.QQ_8_4_8) {
                //QQ 8.4.8 除了一起嗨按钮，同一个位置还有一个群打卡按钮。默认显示群打卡，如果已经打卡就显示一起嗨，两个按钮点击之后都会打开同一个界面，但是要同时hook两个
                XposedHelpers.findAndHookMethod(load("afqa"), "d", new XC_MethodHook(43) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        if (!isEnabled()) return;
                        param.setResult(false);
                    }
                });
                method = "g";
            }
            for (Method m : DexKit.doFindClass(DexKit.C_TogetherControlHelper).getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (method.equals(m.getName()) && m.getReturnType() == void.class && argt.length == 0) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
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
        return new Step[]{new DexDeobfStep(DexKit.C_TogetherControlHelper)};
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(kr_remove_play_together, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_remove_play_together);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
