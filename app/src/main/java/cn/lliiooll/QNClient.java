/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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
package cn.lliiooll;

import java.lang.reflect.*;

import cn.lliiooll.hook.*;
import nil.nadph.qnotified.bridge.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

public class QNClient {
    
    /**
     * 私聊发送一条消息
     *
     * @param qq  目标qq
     * @param msg 消息
     */
    public static void sendFriendMsg(String qq, String msg) {
        send(qq, msg, 0);
    }
    
    /**
     * 群聊发送一条消息
     *
     * @param qq  目标qq
     * @param msg 消息
     */
    public static void sendGroupMsg(String qq, String msg) {
        send(qq, msg, 1);
    }
    
    /**
     * 发送一条文字消息
     *
     * @param uin     要发送的 群/好友
     * @param content 要发送的内容
     * @param type    类型，当发送给好友为0.否则为1
     */
    public static void send(String uin, String content, int type) {
        // to do
        ChatActivityFacade.sendMessage(
            Utils.getQQAppInterface(), getApplication(), SessionInfoImpl.createSessionInfo(uin, type), content
        );
    }
    
    /**
     * 发送一条toast
     *
     * @param msg 要显示的消息
     */
    public static void toast(String msg) {
        //Utils.showToast(getApplication(), TOAST_TYPE_INFO, msg, Toasts.LENGTH_LONG);
        //Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
    }
    
    public void hook(Member member, QNHook hook) {
        //TODO: hook
    }
}
