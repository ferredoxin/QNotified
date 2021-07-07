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

import ltd.nextalone.util.clazz
import ltd.nextalone.util.get
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import ltd.nextalone.util.set
import ltd.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.QQVersion

@FunctionEntry
object Emoji2Sticker : CommonDelayableHook("Ketal_Emoji2Sticker", SyncUtils.PROC_MAIN, true) {
    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_7_5)

    override fun initOnce() = tryOrFalse {
        "Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack;->parseMsgForAniSticker(Ljava/lang/String;Lcom/tencent/mobileqq/activity/aio/BaseSessionInfo;)Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack\$AniStickerTextParseResult;"
            .method.hookAfter(this) {
                if (!superIsEnable()) {
                    it.result.set("singleAniSticker", false)
                }
            }
    }

    fun superIsEnable() = super.isEnabled()

    override fun isEnabled() = true

    fun parseMsgForAniSticker(str: String, session: Any) : Any? {
        return try {
            "Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack;->parseMsgForAniSticker(Ljava/lang/String;Lcom/tencent/mobileqq/activity/aio/BaseSessionInfo;)Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack\$AniStickerTextParseResult;"
                .method(null, str, session)
        } catch (e: Exception) {
            null
        }
    }

    fun sendParseAticker(result: Any, session: Any) {
        val clazz = "com/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack".clazz
            ?: return
        for (m in clazz.declaredMethods) {
            when(m.name) {
                "sendAniStickerMsg" -> {
                    m(null, result, session)
                }
                "sendAniSticker"  -> {
                    val id = result.get("emoLocalId")
                    m(null, id, session)
                }
            }
        }
    }
}
