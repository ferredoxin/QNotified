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
package xyz.nextalone.hook

import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.ui.base.净化功能
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import xyz.nextalone.base.MultiItemDelayableHook
import xyz.nextalone.util.clazz
import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.method
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
@UiItem
object SimplifyBottomTab : MultiItemDelayableHook("na_simplify_bottom_tab_kt") {
    override val preferenceLocate: Array<String> = 净化功能
    override val preferenceTitle = "精简底栏"

    private val clzNames = mapOf(
        //"消息" to "com.tencent.mobileqq.activity.home.Conversation", //保留一个
        "联系人" to "com.tencent.mobileqq.activity.contacts.base.Contacts",
        "快闪" to "com.tencent.mobileqq.activity.flashshow.FlashShowFrame",
        "动态" to "com.tencent.mobileqq.leba.Leba",
        "空间" to "com.tencent.mobileqq.activity.leba.QzoneFrame",
        //"看点" to "com.tencent.mobileqq.kandian.biz.tab.ReadinjoyTabFrame",
        "看点" to "com.tencent.mobileqq.kandian.biz.xtab.RIJXTabFrame",
        "小世界" to "com.tencent.mobileqq.activity.qcircle.QCircleFrame",
        "频道" to "com.tencent.mobileqq.guild.mainframe.GuildMainFrame"
    )
    override val allItems = setOf<String>()
    override val defaultItems = setOf<String>()
    override var items = clzNames.keys.toMutableList()

    override fun initOnce() = tryOrFalse {
        "com.tencent.mobileqq.activity.home.impl.TabFrameControllerImpl".clazz?.method("addFrame")
            ?.hookBefore(this) {
                val clzName = (it.args[it.args.size - 2] as Class<*>).name
                val index = clzNames.values.indexOf(clzName)
                if (index == -1) return@hookBefore
                if (items[index] in activeItems) {
                    it.result = null
                }
            }
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_5_0)
}
