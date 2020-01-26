package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;

import static nil.nadph.qnotified.util.Initiator._TroopGiftAnimationController;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.qn_hide_gift_animation;

public class HideGiftAnim extends BaseDelayableHook {
    private static final HideGiftAnim self = new HideGiftAnim();
    private boolean inited = false;

    HideGiftAnim() {
    }

    public static HideGiftAnim get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = _TroopGiftAnimationController();
            XposedHelpers.findAndHookMethod(clz, "a", load("com/tencent/mobileqq/data/MessageForDeliverGiftTips"), new XC_MethodHook(39) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        ConfigManager cfg = ConfigManager.getDefault();
                        if (!cfg.getBooleanOrFalse(qn_hide_gift_animation)) return;
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
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
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
            return ConfigManager.getDefault().getBooleanOrFalse(qn_hide_gift_animation);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
