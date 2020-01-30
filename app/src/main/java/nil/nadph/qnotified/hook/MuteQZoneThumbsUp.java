package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.qn_mute_thumb_up;

public class MuteQZoneThumbsUp extends BaseDelayableHook {

    private static final MuteQZoneThumbsUp self = new MuteQZoneThumbsUp();
    private boolean inited = false;

    private MuteQZoneThumbsUp() {
    }

    public static MuteQZoneThumbsUp get() {
        return self;
    }

    private int MSG_INFO_OFFSET = -1;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = DexKit.doFindClass(DexKit.C_QZONE_MSG_NOTIFY);
            Method showQZoneMsgNotification = null;
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class)) {
                    if (showQZoneMsgNotification == null ||
                            m.getParameterTypes().length > showQZoneMsgNotification.getParameterTypes().length) {
                        showQZoneMsgNotification = m;
                    }
                }
            }
            XposedBridge.hookMethod(showQZoneMsgNotification, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    if (MSG_INFO_OFFSET < 0) {
                        Class<?>[] argt = ((Method) param.method).getParameterTypes();
                        int hit = 0;
                        for (int i = 0; i < argt.length; i++) {
                            if (argt[i].equals(String.class)) {
                                if (hit == 1) {
                                    MSG_INFO_OFFSET = i;
                                    break;
                                } else {
                                    hit++;
                                }
                            }
                        }
                    }
                    String desc = (String) param.args[MSG_INFO_OFFSET];
                    if (desc != null && desc.endsWith("赞了你的说说")) {
                        param.setResult(null);
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
        return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_QZONE_MSG_NOTIFY};
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_mute_thumb_up);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
