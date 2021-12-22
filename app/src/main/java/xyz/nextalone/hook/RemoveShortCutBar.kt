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
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.tlb.净化_聊天
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import xyz.nextalone.util.method
import xyz.nextalone.util.replace
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
@UiItem
object RemoveShortCutBar : CommonDelayAbleHookBridge() {

    override val preference = uiSwitchPreference {
        title = "隐藏文本框上方快捷方式"
    }

    override val preferenceLocate = 净化_聊天

    override fun initOnce() = tryOrFalse {
        val methodName = if (requireMinQQVersion(QQVersion.QQ_8_6_0))
            "Lcom/tencent/mobileqq/activity/aio/helper/TroopAppShortcutBarHelper;->g()V"
        else "Lcom.tencent.mobileqq.activity.aio.helper.ShortcutBarAIOHelper;->h()V"
        methodName.method.replace(
            this,
            null
        )
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_5_5)
}
