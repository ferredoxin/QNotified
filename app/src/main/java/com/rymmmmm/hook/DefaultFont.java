package com.rymmmmm.hook;

import android.os.*;
import android.view.*;
import android.widget.*;

import java.lang.reflect.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

//强制使用默认字体
public class DefaultFont extends BaseDelayableHook {
    public static final String rq_default_font = "rq_default_font";
    private static final DefaultFont self = new DefaultFont();
    private boolean isInit = false;
    
    public static DefaultFont get() {
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
        if (isInit) {
            return true;
        }
        try {
            Class<?> C_ChatMessage = Initiator.load("com.tencent.mobileqq.data.ChatMessage");
            for (Method m : Initiator._TextItemBuilder().getDeclaredMethods()) {
                if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType() == void.class) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 2 && argt[0] != View.class && argt[1] == C_ChatMessage) {
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
        return new Step[0];
    }
    
    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_default_font);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_default_font, enabled);
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
