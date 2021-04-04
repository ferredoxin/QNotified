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
import me.singleneuron.data.MiniAppArkData
import me.singleneuron.data.StructMsgData
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.decorator.BaseItemBuilderFactoryHookDecorator
import me.singleneuron.qn_kernel.ui.base.UiSwitchPreference
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils
import org.json.JSONObject

@UiItem
object MiniAppToStruckMsg : BaseItemBuilderFactoryHookDecorator(MiniAppToStruckMsg::class.java.simpleName) {

    override fun doDecorate(
        result: Int,
        chatMessage: Any,
        param: XC_MethodHook.MethodHookParam
    ): Boolean {
        try {
            if (hostInfo.versionCode < QQVersion.QQ_8_0_0) return false
            return if (Initiator.load("com.tencent.mobileqq.data.MessageForArkApp")
                    .isAssignableFrom(chatMessage.javaClass)
            ) {
                val arkAppMsg = ReflexUtil.iget_object_or_null(chatMessage, "ark_app_message")
                val json =
                    ReflexUtil.invoke_virtual(arkAppMsg, "toAppXml", *arrayOfNulls(0)) as String
                val jsonObject = JSONObject(json)
                if (jsonObject.optString("app").contains("com.tencent.miniapp", true)) {
                    val miniAppArkData = MiniAppArkData.fromJson(json)
                    val structMsgJson = StructMsgData.fromMiniApp(miniAppArkData).toString()
                    //Utils.logd(structMsgJson)
                    XposedHelpers.callMethod(arkAppMsg, "fromAppXml", structMsgJson)
                    true
                } else false
            } else false
        } catch (e: Exception) {
            Utils.log(e)
            return false
        }
    }

    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "小程序转链接分享（接收）"
    }

    override val preferenceLocate: Array<String> = arrayOf("辅助功能")

}
