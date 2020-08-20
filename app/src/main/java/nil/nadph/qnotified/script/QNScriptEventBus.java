package nil.nadph.qnotified.script;

import bsh.EvalError;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.log;

public class QNScriptEventBus {

    /**
     * 广播群消息事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastGroupMessage(GroupMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupMessage(param);
        }
    }

    /**
     * 广播好友消息事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastFriendMessage(FriendMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendMessage(param);
        }
    }

    /**
     * 广播好友请求事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastFriendRequest(FriendRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendRequest(param);
        }
    }

    /**
     * 广播好友添加完毕事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastFriendAdded(FriendAddedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onFriendAdded(param);
        }
    }

    /**
     * 广播入群请求事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastGroupRequest(GroupRequestParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupRequest(param);
        }
    }

    /**
     * 广播成员入群事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastGroupJoined(GroupJoinedParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) continue;
            qs.onGroupJoined(param);
        }
    }
}
