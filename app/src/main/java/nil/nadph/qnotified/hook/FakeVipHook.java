package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;

public class FakeVipHook extends BaseDelayableHook {

    private FakeVipHook() {
    }

    private static final FakeVipHook self = new FakeVipHook();

    public static FakeVipHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = DexKit.doFindClass(DexKit.C_VIP_UTILS);
            XposedHelpers.findAndHookMethod(clz, "a", load("mqq/app/AppRuntime"), String.class, new XC_MethodHook(-52) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int ret;
                    //self
                    if (param.args[1] == null) {
                        ret = (int) param.getResult();
                        param.setResult(4 | ret);//svip
                    }
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
    public boolean isInited() {
        return inited;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_VIP_UTILS};
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
