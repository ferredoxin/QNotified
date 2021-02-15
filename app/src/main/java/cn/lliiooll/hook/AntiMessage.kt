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

package cn.lliiooll.hook

import cn.lliiooll.msg.MessageReceiver
import cn.lliiooll.util.MsgRecordUtil
import de.robv.android.xposed.XposedHelpers
import me.nextalone.hook.base.MultiItemDelayableHook
import me.singleneuron.data.MsgRecordData
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion

object AntiMessage : MultiItemDelayableHook("qn_anti_message_items", "屏蔽"), MessageReceiver {
    override val allItems = MsgRecordUtil.MSG.keys.toTypedArray().toList()
    override val defaultItems = ""

    override fun onReceive(data: MsgRecordData?): Boolean {
        if (data?.selfUin.equals(data?.senderUin)) return false
        val items: List<Int> = MsgRecordUtil.parse(activeItems)
        if (items.contains(data?.msgType)) {
            XposedHelpers.setBooleanField(data?.msgRecord, "isread", true)
            return true
        }
        return false
    }

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)
}
