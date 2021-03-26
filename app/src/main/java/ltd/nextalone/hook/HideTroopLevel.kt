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

import android.view.ViewGroup
import androidx.core.view.isVisible
import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.data.TroopInfo
import me.ketal.dispacher.OnBubbleBuilder
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.data.MsgRecordData
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator._TroopMemberLevelView

object HideTroopLevel : CommonDelayableHook("na_hide_troop_level_kt"), OnBubbleBuilder {
    private val levelClass = _TroopMemberLevelView()

    override fun isValid() = levelClass != null

    override fun initOnce() = isValid

    override fun onGetView(
        rootView: ViewGroup,
        msg: MsgRecordData,
        param: XC_MethodHook.MethodHookParam
    ) {
        if (!isEnabled || 1 != msg.isTroop) return
        if (levelClass == null) return
        val sendUin = msg.senderUin
        val troopInfo = TroopInfo(msg.friendUin)
        val ownerUin = troopInfo.troopOwnerUin
        val admin = troopInfo.troopAdmin
        val levelView = rootView.findViewByType(levelClass)
        val isAdmin = admin?.contains(sendUin) == true || ownerUin == sendUin
        levelView?.isVisible = isAdmin
    }
}
