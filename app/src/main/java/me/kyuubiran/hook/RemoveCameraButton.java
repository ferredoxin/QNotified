package me.kyuubiran.hook;

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

//屏蔽聊天界面相机图标
public class RemoveCameraButton extends BaseDelayableHook {
    public static final String kr_disable_camera_button = "kr_disable_camera_button";
    private static final RemoveCameraButton self = new RemoveCameraButton();
    private boolean isInit = false;
    
    
    public static RemoveCameraButton get() {
        return self;
    }
    
    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }
    
    @Override
    public boolean isInited() {
        if (isInit) {
            return true;
        }
        try {
            for (Method m : Initiator._ConversationTitleBtnCtrl().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if ("a".equals(m.getName()) && m.getReturnType() == void.class && argt.length == 0) {
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
            isInit = true;
            return true;
        } catch (Throwable t) {
            Utils.log(t);
            return false;
        }
    }
    
    @Override
    public boolean init() {
        return false;
    }
    
    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }
    
    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_disable_camera_button);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(kr_disable_camera_button, enabled);
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
