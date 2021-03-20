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

import ltd.nextalone.util.*
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

@FunctionEntry
object CollapseTroopMessage : CommonDelayableHook("na_collapse_troop_message_kt") {
    override fun initOnce() = tryOrFalse {
        "com.tencent.mobileqq.activity.aio.core.TroopChatPie".clazz?.method(
            "a",
            List::class.java,
            List::class.java
        )?.hookAfter(this) {
            var picMd5: CharSequence
            var text: CharSequence
            val list = (it.result as List<*>).toMutableList()
            val iterator = list.iterator()
            val textList = arrayListOf("", "").toMutableList()
            val picList = arrayListOf("", "").toMutableList()
            while (iterator.hasNext()) {
                val obj = iterator.next()!!
                if (obj.javaClass.name == "com.tencent.mobileqq.data.MessageForText" && obj.get("sb") != null) {
                    text = obj.get("sb") as CharSequence
                    if (textList.last() == text.toString() && textList[textList.size - 2] == text.toString()) {
                        if (obj.get("senderuin") as String != Utils.getLongAccountUin()
                                .toString()
                        ) iterator.remove()
                    }
                    textList.add(text.toString())
                }
                if (obj.javaClass.name == "com.tencent.mobileqq.data.MessageForPic" && obj.get("md5") != null) {
                    picMd5 = obj.get("md5") as CharSequence
                    if (picList.last() == picMd5.toString()) {
                        if (obj.get("senderuin") as String != Utils.getLongAccountUin()
                                .toString()
                        ) iterator.remove()
                    }
                    picList.add(picMd5.toString())
                }
            }
            it.result = list
        }
    }
}
