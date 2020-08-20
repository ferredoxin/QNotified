package nil.nadph.qnotified.script;

import bsh.EvalError;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.log;

public class QNScriptEventBus {

    public static void onGroupMessage(GroupMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupMessage(param);
        }
    }

    public static void onFriendMessage(FriendMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendMessage(param);
        }
    }

    public static void onFriendRequest(FriendRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendRequest(param);
        }
    }

    public static void onFriendAdded(FriendAddedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendAdded(param);
        }
    }

    public static void onGroupRequest(GroupRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupRequest(param);
        }
    }

    public static void onGroupJoined(GroupJoinedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupJoined(param);
        }
    }
}
