package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;

import java.io.IOException;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class FakeBatteryHook extends BaseDelayableHook {
    private static final FakeBatteryHook self = new FakeBatteryHook();
    private boolean inited = false;

    FakeBatteryHook() {
    }

    public static FakeBatteryHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = load("com/tencent/mobileqq/msf/sdk/MsfSdkUtils");
            findAndHookMethod(clz, "getSendBatteryStatus", new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    //log("--->getSendBatteryStatus isEnabled=" + isEnabled() + ", getFakeBatteryStatus=" + getFakeBatteryStatus());
                    if (!isEnabled()) return;
                    param.setResult(getFakeBatteryStatus());
                }
            });
            findAndHookMethod(clz, "getBatteryStatus", new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    param.setResult(getFakeBatteryCapacity());
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public void setFakeBatteryStatus(int val) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putInt(qn_fake_bat_expr, val);
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
    }

    public int getFakeBatteryStatus() {
        int val = ConfigManager.getDefaultConfig().getIntOrDefault(qn_fake_bat_expr, -1);
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
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_fake_bat_enable);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
