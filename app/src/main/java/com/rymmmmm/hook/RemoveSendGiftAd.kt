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
package com.rymmmmm.hook

import xyz.nextalone.util.hookBefore
import xyz.nextalone.util.isStatic
import xyz.nextalone.util.set
import xyz.nextalone.util.tryOrFalse
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator

//去除群聊送礼物广告
@FunctionEntry
object RemoveSendGiftAd : CommonDelayableHook("rq_remove_send_gift_ad",
    SyncUtils.PROC_ANY) {
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
