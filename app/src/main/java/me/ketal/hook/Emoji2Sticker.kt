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

package me.ketal.hook

import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.tlb.娱乐功能
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import xyz.nextalone.util.*

@FunctionEntry
@UiItem
object Emoji2Sticker : CommonDelayAbleHookBridge() {
    override val preference = uiSwitchPreference {
        title = "关闭大号emoji"
        summary = "关闭此功能，输入单个emoji后发送大表情，仅支持部分表情"
    }
    override val preferenceLocate = 娱乐功能

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_7_5)

    override fun initOnce() = tryOrFalse {
        "com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack".clazz?.method("parseMsgForAniSticker")?.hookAfter(this) {
            it.result.set("singleAniSticker", false)
        }
    }
}
