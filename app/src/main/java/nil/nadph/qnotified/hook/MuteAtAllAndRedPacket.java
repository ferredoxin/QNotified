package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class MuteAtAllAndRedPacket extends BaseDelayableHook {
    private static final MuteAtAllAndRedPacket self = new MuteAtAllAndRedPacket();
    private boolean inited = false;

    private MuteAtAllAndRedPacket() {
    }

    public static MuteAtAllAndRedPacket get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class cl_MessageInfo = load("com/tencent/mobileqq/troop/data/MessageInfo");
            if (cl_MessageInfo == null) {
                Class c = _MessageRecord();
                cl_MessageInfo = c.getDeclaredField("mMessageInfo").getType();
            }
            /* @author qiwu */
            final int at_all_type = (Utils.getHostInfo(getApplication()).versionName.compareTo("7.8.0") >= 0) ? 13 : 12;
            XposedHelpers.findAndHookMethod(cl_MessageInfo, "a", _QQAppInterface(), boolean.class, String.class, new XC_MethodHook(60) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int ret = (int) param.getResult();
                    String troopuin = (String) param.args[2];
                    if (ret != at_all_type) return;
                    String muted = "," + ConfigManager.getDefaultConfig().getString(ConfigItems.qn_muted_at_all) + ",";
                    if (muted.contains("," + troopuin + ",")) {
                        param.setResult(0);
                    }
                }
            });
        } catch (Exception e) {
            log(e);
        }
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"), "doParse", new XC_MethodHook(200) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    boolean mute = false;
                    int istroop = (Integer) iget_object_or_null(param.thisObject, "istroop");
                    if (istroop != 1) return;
                    String troopuin = (String) iget_object_or_null(param.thisObject, "frienduin");
                    String muted = "," + ConfigManager.getDefaultConfig().getString(ConfigItems.qn_muted_red_packet) + ",";
                    if (muted.contains("," + troopuin + ",")) mute = true;
                    if (mute) XposedHelpers.setObjectField(param.thisObject, "isread", true);
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
    public void setEnabled(boolean enabled) {
        //do nothing
    }

    @Override
    public boolean checkPreconditions() {
        return true;
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
        return true;
    }
}
