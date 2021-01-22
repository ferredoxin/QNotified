package me.ketal.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Utils.*;

public class LeftSwipeReplyHook extends CommonDelayableHook {
    public static final LeftSwipeReplyHook INSTANCE = new LeftSwipeReplyHook();

    protected LeftSwipeReplyHook() {
        super("ketal_left_swipe_reply", SyncUtils.PROC_MAIN, false, new DexDeobfStep(DexKit.C_LeftSwipeReply_Helper));
    }

    @Override
    public boolean isValid() {
        return super.isValid();
    }

    @Override
    protected boolean initOnce() {
        try {
            String methodName = "a";
            if(isTim())
                methodName = "L";
            XposedHelpers.findAndHookMethod(DexKit.doFindClass(DexKit.C_LeftSwipeReply_Helper), methodName, float.class, float.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isEnabled())
                        param.setResult(null);
                }
            });
            return true;
        } catch (Exception e) {
            log(e);
        }
        return false;
    }
}
