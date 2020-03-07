package nil.nadph.qnotified.util;

import nil.nadph.qnotified.activity.*;

public class ActProxyMgr {
    public static final String STUB_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";
    public static final String ACTIVITY_PROXY_ACTION = "qn_act_proxy_action";
    public static final String ACTIVITY_PROXY_INTENT = "qn_act_proxy_intent";
    public static final int ACTION_EXFRIEND_LIST = 1;
    public static final int ACTION_ADV_SETTINGS = 2;
    public static final int ACTION_ABOUT = 3;
    public static final int ACTION_SHELL = 4;
    public static final int ACTION_MUTE_AT_ALL = 5;
    public static final int ACTION_MUTE_RED_PACKET = 6;
    public static final int ACTION_DONATE_ACTIVITY = 7;
    public static final int ACTION_TROUBLESHOOT_ACTIVITY = 8;
    public static final int ACTION_FRIENDLIST_EXPORT_ACTIVITY = 9;
    public static final int ACTION_FAKE_BAT_CONFIG_ACTIVITY = 10;

    @Deprecated
    public static Class<?> getActivityByAction(int action) {
        switch (action) {
            case ACTION_EXFRIEND_LIST:
                return ExfriendListActivity.class;
            case ACTION_ADV_SETTINGS:
                return SettingsActivity.class;
            case ACTION_MUTE_AT_ALL:
            case ACTION_MUTE_RED_PACKET:
                return TroopSelectActivity.class;
            case ACTION_ABOUT:
                return AboutActivity.class;
            case ACTION_DONATE_ACTIVITY:
                return DonateActivity.class;
            case ACTION_TROUBLESHOOT_ACTIVITY:
                return TroubleshootActivity.class;
            case ACTION_FRIENDLIST_EXPORT_ACTIVITY:
                return FriendlistExportActivity.class;
            case ACTION_FAKE_BAT_CONFIG_ACTIVITY:
                return FakeBatCfgActivity.class;
            default:
                return null;
        }
    }
}
