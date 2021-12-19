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

import android.view.ViewGroup
import androidx.core.view.isVisible
import de.robv.android.xposed.XC_MethodHook
import me.ketal.dispacher.OnBubbleBuilder
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.tlb.净化_群聊
import nil.nadph.qnotified.util.Initiator._TroopMemberLevelView
import xyz.nextalone.data.TroopInfo

@UiItem
object HideTroopLevel : CommonDelayAbleHookBridge(), OnBubbleBuilder {

    override val preference = uiSwitchPreference {
        title = "隐藏群聊群成员头衔"
    }

    override val preferenceLocate = 净化_群聊

    private val levelClass
        get() = _TroopMemberLevelView()

    override fun isValid() = levelClass != null

    override fun initOnce() = isValid

    override fun onGetView(
        rootView: ViewGroup,
        chatMessage: MsgRecordData,
        param: XC_MethodHook.MethodHookParam
    ) {
        if (!isEnabled || 1 != chatMessage.isTroop) return
        if (levelClass == null) return
        val sendUin = chatMessage.senderUin
        val troopInfo = TroopInfo(chatMessage.friendUin)
        val ownerUin = troopInfo.troopOwnerUin
        val admin = troopInfo.troopAdmin
        val levelView = rootView.findViewByType(levelClass)
        val isAdmin = admin?.contains(sendUin) == true || ownerUin == sendUin
        levelView?.isVisible = isAdmin
    }
}
