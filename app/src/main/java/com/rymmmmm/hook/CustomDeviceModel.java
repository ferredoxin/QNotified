package com.rymmmmm.hook;

import java.lang.reflect.*;

import de.robv.android.xposed.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.dialog.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.util.*;

//自定义机型
public class CustomDeviceModel extends BaseDelayableHook {
    private static final CustomDeviceModel self = new CustomDeviceModel();
    private boolean isInit = false;
    
    public static CustomDeviceModel get() {
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
            Class<?> Clz = Initiator.load("android.os.Build");
            Field manufacturer = XposedHelpers.findField(Clz, "MANUFACTURER");
            Field model = XposedHelpers.findField(Clz, "MODEL");
            manufacturer.setAccessible(true);
            model.setAccessible(true);
            manufacturer.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceManufacturer());
            model.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceModel());
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
        return RikkaCustomDeviceModelDialog.IsEnabled();
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
