package me.kyuubiran.hook;

import android.content.*;
import android.os.*;
import android.widget.*;

import java.lang.reflect.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

//屏蔽截屏分享
public class DisableScreenshotHelper extends BaseDelayableHook {
    public static final String kr_disable_screenshot_helper = "kr_disable_screenshot_helper";
    private static final DisableScreenshotHelper self = new DisableScreenshotHelper();
    private boolean isInit = false;
    
    public static DisableScreenshotHelper get() {
        return self;
    }
    
    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_ANY;
    }
    
    @Override
    public boolean isInited() {
        return isInit;
    }
    
    @Override
    public boolean init() {
        if (isInit) {
            return true;
        }
        try {
            for (Method m : DexKit.doFindClass(DexKit.C_ScreenShotHelper).getDeclaredMethods()) {
                if (m.getName().equals("a") && Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 3 && argt[0] == Context.class && argt[1] == String.class && argt[2] == Handler.class) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return;
                                }
                                if (!isEnabled()) {
                                    return;
                                }
                                param.setResult(null);
                            }
                        });
                    }
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
        return new Step[]{new DexDeobfStep(DexKit.C_ScreenShotHelper)};
    }
    
    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_disable_screenshot_helper);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(kr_disable_screenshot_helper, enabled);
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
}
