package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class SimpleCheckInHook extends BaseDelayableHook {
    private static final SimpleCheckInHook self = new SimpleCheckInHook();
    private boolean inited = false;

    SimpleCheckInHook() {
    }

    public static SimpleCheckInHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC);
            XposedHelpers.findAndHookMethod(clz, "a", load("com.tencent.mobileqq.data.ChatMessage"), new XC_MethodHook(39) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        if (!cfg.getBooleanOrFalse(qn_sign_in_as_text)) return;
                    } catch (Exception ignored) {
                    }
                    int result = (int) param.getResult();
                    if (result == 71 || result == 84) {
                        param.setResult(-1);
                    } else if (result == 47) {
                        String json = (String) invoke_virtual(iget_object_or_null(param.args[0], "ark_app_message"), "toAppXml", new Object[0]);
                        if (json.contains("com.tencent.qq.checkin")) {
                            param.setResult(-1);
                        }
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
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ITEM_BUILDER_FAC};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_sign_in_as_text);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
