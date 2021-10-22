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
package me.kyuubiran.hook

import xyz.nextalone.util.method
import xyz.nextalone.util.replace
import xyz.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.isPlayQQ
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.qn_kernel.ui.base.净化功能
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.QQVersion
import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference

//屏蔽群聊界面一起嗨
@FunctionEntry
@UiItem
object RemovePlayTogether : CommonDelayAbleHookBridge() {
    override val preferenceLocate = 净化功能
    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "移除群聊界面一起嗨"
    }

    const val ClockInEntryHelper = "RemovePlayTogether.ClockInEntryHelper"
    const val TogetherControlHelper = "RemovePlayTogether.TogetherControlHelper"
    public override fun initOnce(): Boolean = tryOrFalse {
        if (requireMinQQVersion(QQVersion.QQ_8_4_8)) {
            //QQ 8.4.8 除了一起嗨按钮，同一个位置还有一个群打卡按钮。默认显示群打卡，如果已经打卡就显示一起嗨，两个按钮点击之后都会打开同一个界面，但是要同时hook两个
            Initiator._ClockInEntryHelper()?.method(ConfigTable.getConfig(ClockInEntryHelper), 0, Boolean::class.java)?.replace(this, result = false)
        }
        Initiator._TogetherControlHelper()?.method(ConfigTable.getConfig(TogetherControlHelper), 0, Void.TYPE)?.replace(this, result = null)
    }

    override fun isValid(): Boolean = !isPlayQQ()
}
