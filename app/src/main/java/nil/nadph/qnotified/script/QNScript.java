package nil.nadph.qnotified.script;

import android.view.View;
import bsh.EvalError;
import bsh.Interpreter;
import nil.nadph.qnotified.dialog.ScriptSettingDialog;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.log;

public class QNScript {
    private final Interpreter instance;
    private final String code;
    private boolean enable;

    public QNScript(Interpreter lp, String code) {
        this.instance = lp;
        this.code = code;
    }

    public void onLoad() {
        try {
            instance.eval("onLoade()");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onEnable() {
        try {
            instance.eval("onEnable()");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }

    public void onDisable() {
        try {
            instance.eval("onDisable()");
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

    public String getCode() {
        return code;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public boolean setEnable(boolean enable) {
        return this.enable = enable;
    }

    public static QNScript create(Interpreter lp,String code) {
        return new QNScript(lp,code);
    }

    /**
     * 处理点击事件
     *
     * @param view
     * @param qs
     */
    public static void onClick(View view, QNScript qs) {
        ScriptSettingDialog.createAndShowDialog(view.getContext(), qs);
    }

    public CharSequence getEnable() {
        return isEnable() ? "[启用]" : "[禁用]";
    }
}
