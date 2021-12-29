/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package com.rymmmmm.hook

import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.tlb.花Q
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Initiator
import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.isStatic
import xyz.nextalone.util.set
import xyz.nextalone.util.tryOrFalse

//去除群聊送礼物广告
@FunctionEntry
@UiItem
object RemoveSendGiftAd : CommonDelayAbleHookBridge(SyncUtils.PROC_ANY) {
    override val preference = uiSwitchPreference {
        title = "免广告送免费礼物[仅限群聊送礼物]"
        summary = "若失效请使用屏蔽小程序广告"
    }
    override val preferenceLocate = 花Q
    public override fun initOnce() = tryOrFalse {
        val troopGiftPanel = Initiator
            .load("com.tencent.biz.troopgift.TroopGiftPanel")
        for (m in troopGiftPanel.declaredMethods) {
            val argt = m.parameterTypes
            if (m.name == "onClick" && argt.size == 1 && !m.isStatic) {
                m.hookBefore(this) {
                    it.thisObject.set("f", java.lang.Boolean.TYPE, true)
                }
            }
        }
    }


}
