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

import cc.ioctl.script.params.FriendAddedParam;
import cc.ioctl.script.params.FriendMessageParam;
import cc.ioctl.script.params.FriendRequestParam;
import cc.ioctl.script.params.GroupJoinedParam;
import cc.ioctl.script.params.GroupMessageParam;
import cc.ioctl.script.params.GroupRequestParam;
import cc.ioctl.script.params.ParamFactory;

public class QNScriptEventBus {

    /**
     * 广播群消息事件
     *
     * @param param 使用{@link ParamFactory}构建的param对象
     */
    public static void broadcastGroupMessage(GroupMessageParam param) {
        for (QNScript qs : QNScriptManager.getScripts()) {
            if (!qs.isEnable()) {
                continue;
            }
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
            if (!qs.isEnable()) {
                continue;
            }
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
            if (!qs.isEnable()) {
                continue;
            }
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
            if (!qs.isEnable()) {
                continue;
            }
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
            if (!qs.isEnable()) {
                continue;
            }
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
            if (!qs.isEnable()) {
                continue;
            }
            qs.onGroupJoined(param);
        }
    }
}
