/*
 * QNotified - An Xposed module for QQ/TIM
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

package cn.lliiooll.msg;

import me.singleneuron.data.MsgRecordData;

public interface MessageReceiver {

    /**
     * 当拦截到消息时会调用此方法
     *
     * @param data 拦截到的数据
     * @return 如果返回为真将不会向下传递拦截到的消息
     */
    boolean onReceive(MsgRecordData data);
}
