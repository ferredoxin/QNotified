package com.rymmmmm.hook;

import android.annotation.*;

import java.lang.reflect.*;
import java.text.*;
import java.util.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.dialog.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

//自定义聊天页面时间格式
public class CustomMsgTimeFormat extends BaseDelayableHook {
    private static final CustomMsgTimeFormat self = new CustomMsgTimeFormat();
    private boolean isInit = false;
    
    public static CustomMsgTimeFormat get() {
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
            for (Method m : DexKit.doFindClass(DexKit.C_TimeFormatterUtils).getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (m.getName().equals("a") && argt.length == 3 && Modifier.isStatic(m.getModifiers())) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @SuppressLint("SimpleDateFormat")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return;
                            }
                            if (!isEnabled()) {
                                return;
                            }
                            String fmt = RikkaCustomMsgTimeFormatDialog.getCurrentMsgTimeFormat();
                            if (fmt != null) {
                                param.setResult(new SimpleDateFormat(fmt).format(new Date((long) param.args[2])));
                            }
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
        return new Step[]{new DexDeobfStep(DexKit.C_TimeFormatterUtils)};
    }
    
    @Override
    public boolean isEnabled() {
        return RikkaCustomMsgTimeFormatDialog.IsEnabled();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
