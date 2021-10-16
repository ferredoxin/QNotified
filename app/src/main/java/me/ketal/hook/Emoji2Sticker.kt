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

import android.content.Context
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import ltd.nextalone.util.*
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.decorator.BaseInputButtonDecorator
import mqq.app.AppRuntime
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import nil.nadph.qnotified.util.ReflexUtil

@FunctionEntry
object Emoji2Sticker : BaseInputButtonDecorator("Ketal_Emoji2Sticker") {

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_7_5)

    override fun decorate(text: String, session: Parcelable, input: EditText, sendBtn: View, ctx1: Context, qqApp: AppRuntime): Boolean {
        if (!isEnabled) {
            return false
        }
        return tryOrFalse {
            val stickerParse: Any? = parseMsgForAniSticker(text, session)
            val singleAniSticker = ReflexUtil.iget_object_or_null(
                stickerParse, "singleAniSticker") as Boolean
            val sessionAvailable = ReflexUtil.iget_object_or_null(
                stickerParse, "sessionAvailable") as Boolean
            val configAniSticker = ReflexUtil.iget_object_or_null(
                stickerParse, "configAniSticker") as Boolean
            if (singleAniSticker && sessionAvailable
                && configAniSticker) {
                sendParseAticker(stickerParse, session)
                input.text.clear()
            }
        }
    }

    override fun initOnce() = tryOrFalse {
        "Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack;->parseMsgForAniSticker(Ljava/lang/String;Lcom/tencent/mobileqq/activity/aio/BaseSessionInfo;)Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack\$AniStickerTextParseResult;"
            .method.hookAfter(this) {
                if (!isEnabled) {
                    it.result.set("singleAniSticker", false)
                }
            }
    }

    override fun isEnabled(): Boolean {
        return isValid && super.isEnabled()
    }

    fun parseMsgForAniSticker(str: String, session: Any) : Any? {
        return try {
            "Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack;->parseMsgForAniSticker(Ljava/lang/String;Lcom/tencent/mobileqq/activity/aio/BaseSessionInfo;)Lcom/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack\$AniStickerTextParseResult;"
                .method(null, str, session)
        } catch (e: Exception) {
            null
        }
    }

    fun sendParseAticker(result: Any?, session: Any) {
        val clazz = "com/tencent/mobileqq/emoticonview/AniStickerSendMessageCallBack".clazz
            ?: return
        for (m in clazz.declaredMethods) {
            when (m.name) {
                "sendAniStickerMsg" -> {
                    m(null, result, session)
                }
                "sendAniSticker" -> {
                    val id = result.get("emoLocalId")
                    m(null, id, session)
                }
            }
        }
    }
}
