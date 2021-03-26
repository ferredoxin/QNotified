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

import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.base.MultiItemDelayableHook
import ltd.nextalone.util.clazz
import ltd.nextalone.util.hookBefore
import ltd.nextalone.util.method
import ltd.nextalone.util.tryOrFalse
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
object SimplifyChatLongItem : MultiItemDelayableHook("na_simplify_chat_long_item_multi") {
    override val allItems =
        "复制|转发|收藏|回复|多选|撤回|删除|一起写|设为精华|待办|私聊|截图|存表情|相关表情|复制链接|存微云|发给电脑|静音播放|复制文字|转发文字|免提播放|2X|保存"
    override val defaultItems = ""

    override fun initOnce() = tryOrFalse {
        val callback: (XC_MethodHook.MethodHookParam) -> Unit = callback@{
            if (!isEnabled) return@callback
            val str = it.args[1] as String
            if (activeItems.contains(str))
                it.result = null
        }
        "com.tencent.mobileqq.utils.dialogutils.QQCustomMenuImageLayout".clazz?.declaredMethods.run {
            this?.forEach { method ->
                if (method.name == "setMenu") {
                    val customMenu = method.parameterTypes[0].name.replace(".", "/")
                    try {
                        "L$customMenu;->a(ILjava/lang/String;II)V"
                            .method
                            .hookBefore(this@SimplifyChatLongItem, callback)
                    } catch (t: Throwable) {
                        Unit
                    }
                    try {

                        "L$customMenu;->a(ILjava/lang/String;I)V"
                            .method
                            .hookBefore(this@SimplifyChatLongItem, callback)
                    } catch (t: Throwable) {
                        Unit
                    }
                    return@forEach
                }
            }
        }
    }
}
