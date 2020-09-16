/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.util;

import nil.nadph.qnotified.activity.*;

public class ActProxyMgr {
    public static final String STUB_DEFAULT_ACTIVITY = "com.tencent.mobileqq.activity.photo.CameraPreviewActivity";
    public static final String STUB_TRANSLUCENT_ACTIVITY = "cooperation.qlink.QlinkStandardDialogActivity";
    public static final String ACTIVITY_PROXY_ACTION = "qn_act_proxy_action";
    public static final String ACTIVITY_PROXY_INTENT = "qn_act_proxy_intent";
    public static final int ACTION_RESERVED = 0;
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
    public static final int ACTION_CHAT_TAIL_CONFIG_ACTIVITY = 11;
    public static final int ACTION_CHAT_TAIL_TROOPS_ACTIVITY = 12;
    public static final int ACTION_CHAT_TAIL_FRIENDS_ACTIVITY = 13;

    //@Deprecated
    public static Class<?> getActivityByAction(int action) {
        switch (action) {
            case ACTION_EXFRIEND_LIST:
                return ExfriendListActivity.class;
            case ACTION_ADV_SETTINGS:
                return SettingsActivity.class;
            case ACTION_MUTE_AT_ALL:
            case ACTION_MUTE_RED_PACKET:
            case ACTION_CHAT_TAIL_TROOPS_ACTIVITY:
                return TroopSelectActivity.class;
            case ACTION_CHAT_TAIL_FRIENDS_ACTIVITY:
                return FriendSelectActivity.class;
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
            case ACTION_CHAT_TAIL_CONFIG_ACTIVITY:
                return ChatTailActivity.class;
            default:
                return null;
        }
    }
}
