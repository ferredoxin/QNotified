package nil.nadph.qnotified.script.params;

import bsh.EvalError;
import nil.nadph.qnotified.script.QNScript;
import nil.nadph.qnotified.script.QNScriptManager;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.log;

public class QNScriptEventBus {

    public static void onGroupMessage(GroupMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onGroupMessage(param);
        }
    }

    public static void onFriendMessage(FriendMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onFriendMessage(param);
        }
    }

    public static void onFriendRequest(FriendRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onFriendRequest(param);
        }
    }

    public static void onFriendAdded(FriendAddedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onFriendAdded(param);
        }
    }

    public static void onGroupRequest(GroupRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onGroupRequest(param);
        }
    }

    public static void onGroupJoined(GroupJoinedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onGroupJoined(param);
        }
    }

    public static void onLoad() {
        for (QNScript qs : QNScriptManager.getScripts()) {
            qs.onLoad();
        }
    }
}
