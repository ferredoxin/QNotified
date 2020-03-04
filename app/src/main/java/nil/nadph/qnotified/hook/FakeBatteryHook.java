package nil.nadph.qnotified.hook;

import android.content.Context;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static nil.nadph.qnotified.util.Utils.*;

public class FakeBatteryHook extends BaseDelayableHook implements InvocationHandler {
    public static final String qn_fake_bat_enable = "qn_fake_bat_enable";
    private static final FakeBatteryHook self = new FakeBatteryHook();
    private boolean inited = false;

    private Object origRegistrar = null;
    private Object origStatus = null;

    FakeBatteryHook() {
    }

    public static FakeBatteryHook get() {
        return self;
    }

    @Override
    public boolean init() {
        //log("---> FakeBatteryHook called init!");
        if (inited) return true;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                BatteryManager batmgr = (BatteryManager) getApplication().getSystemService(Context.BATTERY_SERVICE);
                if (batmgr == null) {
                    log("Wtf, init FakeBatteryHook but BatteryManager is null!");
                    return false;
                }
                if (Build.VERSION.SDK_INT < 23) {
                    //make a call to init mBatteryStats, so we don't care about the result
                    batmgr.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                }
                Field fBatteryPropertiesRegistrar = BatteryManager.class.getDeclaredField("mBatteryPropertiesRegistrar");
                fBatteryPropertiesRegistrar.setAccessible(true);
                origRegistrar = fBatteryPropertiesRegistrar.get(batmgr);
                Class<?> cIBatteryPropertiesRegistrar = fBatteryPropertiesRegistrar.getType();
                if (origRegistrar == null) {
                    log("Error! mBatteryPropertiesRegistrar(original) got null");
                    return false;
                }
                Class<?> cIBatteryStatus = null;
                Field fBatteryStatus = null;
                try {
                    fBatteryStatus = BatteryManager.class.getDeclaredField("mBatteryStats");
                    fBatteryStatus.setAccessible(true);
                    origStatus = fBatteryStatus.get(batmgr);
                    cIBatteryStatus = fBatteryStatus.getType();
                    if (origStatus == null) {
                        log("FakeBatteryHook/W Field mBatteryStats found, but instance got null");
                    }
                } catch (NoSuchFieldException e) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        log("FakeBatteryHook/W Field mBatteryStats not found, but SDK_INT is " + Build.VERSION.SDK_INT);
                    }
                }
                Object proxy;
                if (origStatus != null && cIBatteryStatus != null) {
                    proxy = Proxy.newProxyInstance(Initiator.getClassLoader(), new Class[]{cIBatteryPropertiesRegistrar, cIBatteryStatus}, this);
                    fBatteryPropertiesRegistrar.set(batmgr, proxy);
                    fBatteryStatus.set(batmgr, proxy);
                } else {
                    proxy = Proxy.newProxyInstance(Initiator.getClassLoader(), new Class[]{cIBatteryPropertiesRegistrar}, this);
                    fBatteryPropertiesRegistrar.set(batmgr, proxy);
                }
                inited = true;
                return true;
            } else {
                //Device too old, not supported
                return false;
            }
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if(method.getName().equals(" "))
        } catch (Exception e) {
            log(e);
        }
        String className = method.getDeclaringClass().getName();
        if (className.endsWith("IBatteryPropertiesRegistrar")) {
            return method.invoke(origRegistrar, args);
        } else if (className.endsWith("IBatteryStats")) {
            return method.invoke(origStatus, args);
        } else {
            //WTF QAQ
            log("Panic, unexpected method " + method);
            throw new NoSuchMethodError(method.toString());
        }
    }

    public void setFakeBatteryStatus(int val) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putInt(ConfigItems.qn_fake_bat_expr, val);
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
    }

    public int getFakeBatteryStatus() {
        int val = ConfigManager.getDefaultConfig().getIntOrDefault(ConfigItems.qn_fake_bat_expr, -1);
        if (val < 0) {
            //log("getFakeBatteryStatus: qn_fake_bat_expr = " + val);
            return 0;//safe value
        }
        return val;
    }

    public boolean isFakeBatteryCharging() {
        return (getFakeBatteryStatus() & 128) > 0;
    }

    public int getFakeBatteryCapacity() {
        return getFakeBatteryStatus() & 127;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
    }

    @Override
    public int[] getPreconditions() {
        return new int[0];
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_fake_bat_enable, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_fake_bat_enable);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
