package com.rymmmmm.hook;

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

//屏蔽群聊炫彩昵称
public class DisableColorNickName extends BaseDelayableHook {
    public static final String rq_disable_color_nick_name = "rq_disable_color_nick_name";
    private static final DisableColorNickName self = new DisableColorNickName();
    private boolean isInit = false;
    
    public static DisableColorNickName get() {
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
        if (isInited()) {
            return true;
        }
        try {
            for (Method m : Initiator._ColorNickManager().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("a") && Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class && argt.length == 3) {
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
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_disable_color_nick_name);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_disable_color_nick_name, enabled);
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
