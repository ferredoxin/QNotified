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

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import ltd.nextalone.util.*
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import ltd.nextalone.util.tryOrFalse
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.data.MsgRecordData
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils

@FunctionEntry
object HideTroopLevel : CommonDelayableHook("na_hide_troop_level_kt") {

    override fun initOnce() = tryOrFalse {
        "Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;->a(IILcom/tencent/mobileqq/data/ChatMessage;Landroid/view/View;Landroid/view/ViewGroup;Lcom/tencent/mobileqq/activity/aio/OnLongClickAndTouchListener;)Landroid/view/View;"
            .method.hookAfter(this) {
                val rootView = it.result as ViewGroup
                val msg = MsgRecordData(it.args[2])
                if (1 != msg.isTroop) return@hookAfter
                val sendUin = msg.senderUin
                val troopUin = msg.friendUin
                val clzTroopInfo = Initiator.load("com.tencent.mobileqq.data.troop.TroopInfo")
                    ?: Initiator.load("com.tencent.mobileqq.data.TroopInfo")
                val troopInfo = ReflexUtil.invoke_virtual(Utils.getTroopManager(), "b", troopUin, String::class.java, clzTroopInfo)
                val ownerUin = ReflexUtil.iget_object_or_null(troopInfo, "troopowneruin", String::class.java)
                val admin = ReflexUtil.iget_object_or_null(troopInfo, "Administrator", String::class.java).split("|")
                val levelClass = "com.tencent.mobileqq.troop.troopMemberLevel.TroopMemberNewLevelView".clazz
                val levelView = rootView.findViewByType(levelClass)
                val isAdmin = sendUin in admin || ownerUin == sendUin
                levelView?.isVisible = isAdmin
            }
    }
}
