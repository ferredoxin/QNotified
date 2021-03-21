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
package ltd.nextalone.hook

import ltd.nextalone.base.MultiItemDelayableHook
import ltd.nextalone.util.*
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
object SimplifyRecentDialog : MultiItemDelayableHook("na_simplify_recent_dialog_multi") {
    override val allItems = "创建群聊|加好友/群|匹配聊天|一起派对|扫一扫|面对面快传|收付款"
    override val defaultItems = ""
    override fun initOnce() = tryOrFalse {
        val methodName: String
        val titleName: String
        if (requireMinQQVersion(QQVersion.QQ_8_6_0)) {
            methodName = "conversationPlusBuild"
            titleName = "title"
        } else {
            methodName = "b"
            titleName = "a"
        }
        "com/tencent/widget/PopupMenuDialog".clazz?.method(methodName,
            4,
            "com.tencent.widget.PopupMenuDialog".clazz)?.hookBefore(this) {
            val list = (it.args[1] as List<*>).toMutableList()
            val iterator = list.iterator()
            while (iterator.hasNext()) {
                val string = iterator.next().get(titleName, String::class.java)
                if (activeItems.contains(string)) {
                    iterator.remove()
                }
            }
            it.args[1] = list.toList()
        }
    }

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_3_9)
}
