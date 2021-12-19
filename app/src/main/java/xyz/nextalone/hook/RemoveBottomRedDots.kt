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
import me.singleneuron.qn_kernel.tlb.净化_主页
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import xyz.nextalone.util.clazz
import xyz.nextalone.util.method
import xyz.nextalone.util.replace
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
@UiItem
object RemoveBottomRedDots : CommonDelayAbleHookBridge() {

    override val preference = uiSwitchPreference {
        title = "隐藏底栏小红点"
    }

    override val preferenceLocate = 净化_主页

    override fun initOnce(): Boolean = tryOrFalse {
        "com.tencent.mobileqq.activity.home.impl.TabFrameControllerImpl".clazz?.method("updateRedTouch")
            ?.replace(this, null)
        "com.tencent.mobileqq.activity.framebusiness.controllerinject.FrameControllerInjectImpl".clazz?.method(
            "a",
            6,
            Void.TYPE
        )?.replace(this, null)
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_5_5)
}
