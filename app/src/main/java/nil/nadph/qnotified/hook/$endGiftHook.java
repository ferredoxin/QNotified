package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.app.Application;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class $endGiftHook extends BaseDelayableHook {
    private static final $endGiftHook self = new $endGiftHook();
    private boolean inited = false;

    private $endGiftHook() {
    }

    public static $endGiftHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method m = DexKit.doFindClass(DexKit.C_TROOP_GIFT_UTIL).getDeclaredMethod("a", Activity.class, String.class, String.class, load("com/tencent/mobileqq/app/QQAppInterface"));
            XposedBridge.hookMethod(m, new XC_MethodHook(47) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        if (!cfg.getBooleanOrFalse(qn_disable_$end_gift)) return;
                    } catch (Exception ignored) {
                    }
                    param.setResult(null);
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_TROOP_GIFT_UTIL};
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_disable_$end_gift);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}

