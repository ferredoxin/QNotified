package me.ketal.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class LeftSwipeReplyHook extends CommonDelayableHook {
    public static final LeftSwipeReplyHook INSTANCE = new LeftSwipeReplyHook();

    protected LeftSwipeReplyHook() {
        super("ketal_left_swipe_reply", SyncUtils.PROC_MAIN, false);
    }
    @Override
    protected boolean initOnce() {
        try {
            String methodName = "a";
            String className = ConfigTable.INSTANCE.getConfig(LeftSwipeReplyHook.class.getSimpleName());
            if(isTim())
                methodName = "L";
            XposedHelpers.findAndHookMethod(load(className), methodName, float.class, float.class, new XC_MethodHook() {
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
