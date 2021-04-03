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

package cn.lliiooll.hook

import android.view.View
import cn.lliiooll.msg.MessageReceiver
import cn.lliiooll.util.MsgRecordUtil
import de.robv.android.xposed.XposedHelpers
import ltd.nextalone.base.MultiItemDelayableHook
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion

object AntiMessage : MultiItemDelayableHook("qn_anti_message_items"), MessageReceiver {
    override var allItems = ""
    override val defaultItems = ""
    override var items: MutableList<String> = MsgRecordUtil.MSG.keys.sorted().toMutableList()


    override fun onReceive(data: MsgRecordData?): Boolean {
        if (data?.selfUin.equals(data?.senderUin)) return false
        val items: List<Int> = MsgRecordUtil.parse(activeItems)
        if (items.contains(data?.msgType)) {
            XposedHelpers.setBooleanField(data?.msgRecord, "isread", true)
            return true
        } else if (items.contains(0) and (data?.msg?.contains("@全体成员") == true)) {
            XposedHelpers.setBooleanField(data?.msgRecord, "isread", true)
            return true
        }
        return false
    }

    override fun listener(): View.OnClickListener {
        items.forEachIndexed { i: Int, str: String ->
            items[i] = MsgRecordUtil.getDesc(str)
        }
        items = items.sortedWith(SortChinese()).toTypedArray().toMutableList()
        return super.listener()
    }

    override fun getBoolAry(): BooleanArray {
        val ret = BooleanArray(items.size)
        for ((i, item) in items.withIndex()) {
            ret[i] = activeItems.contains(item) or activeItems.contains(MsgRecordUtil.getKey(item))
        }
        return ret
    }

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)
}

class SortChinese : Comparator<String> {
    override fun compare(o1: String, o2: String): Int {
        if ((MsgRecordUtil.getKey(o1) != o1) and (MsgRecordUtil.getKey(o2) == o2)) {
            return -1
        } else if ((MsgRecordUtil.getKey(o1) == o1) and (MsgRecordUtil.getKey(o2) != o2)) {
            return 1
        }
        return 0
    }
}
