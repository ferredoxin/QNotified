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
package cc.ioctl.script.params;

public class ParamFactory {

    /**
     * 构建一个好友消息参数对象
     *
     * @return {@link FriendMessageParam}
     */
    public static FriendMessageParam friendMessage() {
        return new FriendMessageParam();
    }

    /**
     * 构建一个好友请求参数对象
     *
     * @return {@link FriendRequestParam}
     */
    public static FriendRequestParam friendRequest() {
        return new FriendRequestParam();
    }

    /**
     * 构建一个好友添加完毕参数对象
     *
     * @return {@link FriendAddedParam}
     */
    public static FriendAddedParam friendAdded() {
        return new FriendAddedParam();
    }

    /**
     * 构建一个群消息参数对象
     *
     * @return {@link GroupMessageParam}
     */
    public static GroupMessageParam groupMessage() {
        return new GroupMessageParam();
    }

    /**
     * 构建一个入群请求参数对象
     *
     * @return {@link GroupRequestParam}
     */
    public static GroupRequestParam groupRequest() {
        return new GroupRequestParam();
    }

    /**
     * 构建一个成员入群参数对象
     *
     * @return {@link GroupJoinedParam}
     */
    public static GroupJoinedParam groupJoined() {
        return new GroupJoinedParam();
    }

}
