package nil.nadph.qnotified.hook.rikka;

import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.log;

//回赞界面一键20赞
public class OneTapTwentyLikes extends BaseDelayableHook {
    public static final String rq_one_tap_twenty_likes = "rq_one_tap_twenty_likes";
    private static final OneTapTwentyLikes self = new OneTapTwentyLikes();
    private boolean isInit = false;

    public static OneTapTwentyLikes get() {
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
        if (isInit) return true;
        try {
            for (Method m : Initiator.load("com.tencent.mobileqq.activity.VisitorsActivity").getDeclaredMethods()) {
                if (m.getName().equals("onClick")) {
                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            View view = (View) param.args[0];
                            Object tag = view.getTag();
                            Object likeClickListener = Utils.iget_object_or_null(param.thisObject, "a", Initiator._VoteHelper());
                            Method onClick = likeClickListener.getClass().getDeclaredMethod("a", tag.getClass(), ImageView.class);
                            for (int i = 0; i < 20; i++) {
                                onClick.invoke(likeClickListener, tag, (ImageView) view);
                            }
                        }
                    });
                }
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_one_tap_twenty_likes, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_one_tap_twenty_likes);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
