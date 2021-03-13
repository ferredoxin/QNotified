/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.script;

import static nil.nadph.qnotified.util.Utils.log;

import bsh.EvalError;
import bsh.Interpreter;
import cc.ioctl.script.params.FriendAddedParam;
import cc.ioctl.script.params.FriendMessageParam;
import cc.ioctl.script.params.FriendRequestParam;
import cc.ioctl.script.params.GroupJoinedParam;
import cc.ioctl.script.params.GroupMessageParam;
import cc.ioctl.script.params.GroupRequestParam;
import java.io.IOException;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;

public class QNScript {

    private final Interpreter instance;
    private final String code;
    private final QNScriptInfo info;
    private boolean enable;
    private boolean init = false;

    public QNScript(Interpreter lp, String code) {
        this.instance = lp;
        this.code = code;
        this.info = QNScriptInfo.getInfo(code);
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
        this.enable = ConfigManager.getDefaultConfig()
            .getBooleanOrFalse(ConfigItems.qn_script_enable_ + getLabel());
        return this.enable;
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
