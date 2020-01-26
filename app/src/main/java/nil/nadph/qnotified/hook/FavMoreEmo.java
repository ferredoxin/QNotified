package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Method;
import java.util.List;

import static nil.nadph.qnotified.util.Initiator._EmoAddedAuthCallback;
import static nil.nadph.qnotified.util.Initiator._FavEmoRoamingHandler;
import static nil.nadph.qnotified.util.Utils.*;

public class FavMoreEmo extends BaseDelayableHook {
    private static final FavMoreEmo self = new FavMoreEmo();
    private boolean inited = false;

    FavMoreEmo() {
    }

    public static FavMoreEmo get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            final Class mEmoAddedAuthCallback = _EmoAddedAuthCallback();
            final Class mFavEmoRoamingHandler = _FavEmoRoamingHandler();
            if (mEmoAddedAuthCallback == null) {
                if (mFavEmoRoamingHandler == null) {
                    setEmoNum();
                } else {
                    XposedHelpers.findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            setEmoNum();
                        }
                    });
                }
            } else {
                Class mUpCallBack$SendResult = null;
                for (Method m : mEmoAddedAuthCallback.getDeclaredMethods()) {
                    if (m.getName().equals("b") && m.getReturnType().equals(void.class) && m.getParameterTypes().length == 1) {
                        mUpCallBack$SendResult = m.getParameterTypes()[0];
                        break;
                    }
                }
                XposedHelpers.findAndHookMethod(mEmoAddedAuthCallback, "b", mUpCallBack$SendResult, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Object msg = param.args[0];
                        iput_object(msg, "a", int.class, 0);
                    }
                });
                XposedHelpers.findAndHookMethod(mFavEmoRoamingHandler, "a", List.class, List.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        setEmoNum();
                    }
                });
            }
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private void setEmoNum() {
        Class mFavEmoConstant = DexKit.doFindClass(DexKit.C_FAV_EMO_CONST);
        sput_object(mFavEmoConstant, "a", 800);
        sput_object(mFavEmoConstant, "b", 800);
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_FAV_EMO_CONST};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qqhelper_fav_more_emo);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
