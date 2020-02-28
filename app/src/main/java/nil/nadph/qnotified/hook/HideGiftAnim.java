package nil.nadph.qnotified.hook;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator._TroopGiftAnimationController;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class HideGiftAnim extends BaseDelayableHook {
    public static final String qn_hide_gift_animation = "qn_hide_gift_animation";
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
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
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
    public boolean isValid() {
        Application app = getApplication();
        return app == null || !isTim(app);
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
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_hide_gift_animation, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_hide_gift_animation);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
