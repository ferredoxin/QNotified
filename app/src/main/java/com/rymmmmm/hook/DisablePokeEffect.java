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

//屏蔽戳一戳动画
public class DisablePokeEffect extends BaseDelayableHook {
    public static final String rq_disable_poke_effect = "rq_disable_poke_effect";
    private static final DisablePokeEffect self = new DisablePokeEffect();
    private boolean isInit = false;
    
    public static DisablePokeEffect get() {
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
            for (Method m : Initiator._GivingHeartItemBuilder().getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("a") && argt.length == 3 && !Modifier.isStatic(m.getModifiers())) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            // param.setResult(null);// 此处不应为null
                            if (param.getResult().getClass().isPrimitive()) {// 判断是boolean (基本类型)
                                param.setResult(false);
                            }
                        }
                    });
                }
//                "fangdazhao" need to fix.
//                if (m.getName().equals("b") && m.getParameterTypes().length == 2 && !Modifier.isStatic(m.getModifiers())) {
//                    XposedBridge.hookMethod(m, new XC_MethodHook() {
//                        @Override
//                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                            if (LicenseStatus.sDisableCommonHooks) return;
//                            if (!isEnabled()) return;
//                            param.setResult(null);
//                        }
//                    });
//                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_disable_poke_effect);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_disable_poke_effect, enabled);
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
