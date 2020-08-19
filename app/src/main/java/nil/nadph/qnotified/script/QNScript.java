package nil.nadph.qnotified.script;

import bsh.EvalError;
import bsh.Interpreter;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.log;

public class QNScript {
    private final Interpreter instance;
    private boolean enable;

    public QNScript(Interpreter lp) {
        this.instance = lp;
    }

    public void enable() {
        try {
            instance.eval("enable()");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void disable() {
        try {
            instance.eval("disable()");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onGroupMessage(GroupMessageParam param) {
        try {
            instance.set("groupMessageParam", param);
            instance.eval("onGroupMessage(groupMessageParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onFriendMessage(FriendMessageParam param) {
        try {
            instance.set("friendMessageParam", param);
            instance.eval("onFriendMessage(friendMessageParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onFriendRequest(FriendRequestParam param) {
        try {
            instance.set("friendRequestParam", param);
            instance.eval("onFriendRequest(friendRequestParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onFriendAdded(FriendAddedParam param) {
        try {
            instance.set("friendAddedParam", param);
            instance.eval("onFriendAdded(friendAddedParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onGroupRequest(GroupRequestParam param) {
        try {
            instance.set("groupRequestParam", param);
            instance.eval("onGroupRequest(groupRequestParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onGroupJoined(GroupJoinedParam param) {
        try {
            instance.set("groupJoinedParam", param);
            instance.eval("onGroupJoined(groupJoinedParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public String getName() {
        try {
            return (String) instance.get("name");
        } catch (EvalError evalError) {
            log(evalError);
        }
        return "";
    }

    public String getLabel() {
        try {
            return (String) instance.get("label");
        } catch (EvalError evalError) {
            log(evalError);
        }
        return "";
    }

    public String getVersion() {
        try {
            return (String) instance.get("version");
        } catch (EvalError evalError) {
            log(evalError);
        }
        return "";
    }

    public String getAuthor() {
        try {
            return (String) instance.get("author");
        } catch (EvalError evalError) {
            log(evalError);
        }
        return "";
    }

    public String getDecs() {
        try {
            return (String) instance.get("decs");
        } catch (EvalError evalError) {
            log(evalError);
        }
        return "";
    }

    public boolean isEnable() {
        return this.enable;
    }

    public boolean setEnable(boolean enable) {
        return this.enable = enable;
    }

    public static QNScript create(Interpreter lp) {
        return new QNScript(lp);
    }
}
