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

package me.singleneuron.hook.decorator

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.decorator.BaseItemBuilderFactoryHookDecorator
import me.singleneuron.qn_kernel.ui.base.UiSwitchPreference
import nil.nadph.qnotified.BuildConfig
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils

@UiItem
object CardMsgToText : BaseItemBuilderFactoryHookDecorator(CardMsgToText::class.java.simpleName) {
    override fun doDecorate(
        result: Int,
        chatMessage: Any,
        param: XC_MethodHook.MethodHookParam
    ): Boolean {
        return try {
            var text: String
            if (Initiator.load("com.tencent.mobileqq.data.MessageForStructing")
                    .isAssignableFrom(chatMessage.javaClass)
            ) {
                text = ReflexUtil.invoke_virtual(
                    ReflexUtil.iget_object_or_null(
                        chatMessage,
                        "structingMsg"
                    ), "getXml", *arrayOfNulls(0)
                ) as String
                dumpCardMsg(chatMessage)
            } else if (Initiator.load("com.tencent.mobileqq.data.MessageForArkApp")
                    .isAssignableFrom(chatMessage.javaClass)
            ) {
                text = ReflexUtil.invoke_virtual(
                    ReflexUtil.iget_object_or_null(
                        chatMessage,
                        "ark_app_message"
                    ), "toAppXml", *arrayOfNulls(0)
                ) as String
                dumpCardMsg(chatMessage)
            } else return false

            text = "[卡片消息] ${chatMessage::class.java.simpleName}\n\n$text"
            XposedHelpers.setObjectField(chatMessage, "msg", text)
            param.result = -1
            true
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }

    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "卡片消息文本化"
    }

    override val preferenceLocate: Array<String> = arrayOf("净化功能")

}

private fun dumpCardMsg(chatMessage: Any) {
    if (!BuildConfig.DEBUG) return
    try {
        Utils.logd("Start dump card message...")
        Utils.logd("chatMessage class: " + chatMessage::class.java.name)

        if (Initiator.load("com.tencent.mobileqq.data.MessageForStructing")
                .isAssignableFrom(chatMessage.javaClass)
        ) {
            val structingMsg = ReflexUtil.iget_object_or_null(chatMessage, "structingMsg")
            Utils.logd("structingMsg class: " + structingMsg::class.java.name)
        }

        if (Initiator.load("com.tencent.mobileqq.data.MessageForArkApp")
                .isAssignableFrom(chatMessage.javaClass)
        ) {
            val arkAppMessage = ReflexUtil.iget_object_or_null(chatMessage, "ark_app_message")
            Utils.logd("ark_app_message class: " + arkAppMessage::class.java.name)
        }

        Utils.logd("...Dump end")
    } catch (e: Exception) {
        Utils.log(e)
    }
}
