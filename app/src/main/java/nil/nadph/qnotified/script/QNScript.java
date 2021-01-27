package nil.nadph.qnotified.script;

import java.io.*;

import bsh.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.script.params.*;

import static nil.nadph.qnotified.util.Utils.*;

public class QNScript {
    private final Interpreter instance;
    private final String code;
    private final QNScriptInfo info;
    private boolean enable;
    private final boolean init = false;
    
    public QNScript(Interpreter lp, String code) {
        instance = lp;
        this.code = code;
        info = QNScriptInfo.getInfo(code);
    }
    
    public static QNScript create(Interpreter lp, String code) {
        return new QNScript(lp, code);
    }
    
    public void onLoad() {
        try {
            if (!init) {
                instance.eval(code);
            }
            instance.eval("onLoad()");
            QNScriptManager.addEnable();
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onGroupMessage(GroupMessageParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("groupMessageParam", param);
            instance.eval("onGroupMessage(groupMessageParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onFriendMessage(FriendMessageParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("friendMessageParam", param);
            instance.eval("onFriendMessage(friendMessageParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onFriendRequest(FriendRequestParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("friendRequestParam", param);
            instance.eval("onFriendRequest(friendRequestParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onFriendAdded(FriendAddedParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("friendAddedParam", param);
            instance.eval("onFriendAdded(friendAddedParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onGroupRequest(GroupRequestParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("groupRequestParam", param);
            instance.eval("onGroupRequest(groupRequestParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public void onGroupJoined(GroupJoinedParam param) {
        if (!init) {
            return;
        }
        try {
            instance.set("groupJoinedParam", param);
            instance.eval("onGroupJoined(groupJoinedParam)");
        } catch (EvalError evalError) {
            log(evalError);
        }
    }
    
    public String getName() {
        return info.name;
    }
    
    public String getLabel() {
        return info.label;
    }
    
    public String getVersion() {
        return info.version;
    }
    
    public String getAuthor() {
        return info.author;
    }
    
    public String getDecs() {
        return info.decs;
    }
    
    public String getCode() {
        return code;
    }
    
    public boolean isEnable() {
        enable = ConfigManager.getDefaultConfig().getBooleanOrFalse(ConfigItems.qn_script_enable_ + getLabel());
        return enable;
    }
    
    public boolean setEnable(boolean enable) {
        // 写入配置文件
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        cfg.putBoolean(ConfigItems.qn_script_enable_ + getLabel(), enable);
        try {
            cfg.save();
        } catch (IOException e) {
            log(e);
        }
        return this.enable = enable;
    }
    
    public CharSequence getEnable() {
        return isEnable() ? "[启用]" : "[禁用]";
    }
}
