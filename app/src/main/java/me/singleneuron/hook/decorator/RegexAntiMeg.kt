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

import cn.lliiooll.msg.MessageReceiver
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.getExFriendCfg
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.ui.base.辅助功能
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.ui.CommonContextWrapper
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxin_ui.base.UiItem
import org.ferredoxin.ferredoxin_ui.base.uiEditTextPreference

@me.singleneuron.qn_kernel.annotation.UiItem
object RegexAntiMeg : MessageReceiver, UiItem {

    private var regexCache: Regex? = null
    private var regexStringCache: String = ""

    override fun onReceive(data: MsgRecordData?): Boolean {
        try {
            if (data == null) return false
            val regexString =
                getExFriendCfg().getStringOrDefault(RegexAntiMeg::class.simpleName!!, "")
            if (regexString.isNullOrBlank()) return false
            return when {
                Initiator.load("com.tencent.mobileqq.data.MessageForStructing")
                    .isAssignableFrom(data.javaClass) -> {
                    val text = ReflexUtil.invoke_virtual(
                        ReflexUtil.iget_object_or_null(
                            data,
                            "structingMsg"
                        ), "getXml", *arrayOfNulls(0)
                    ) as String
                    processMsg(data, text, regexString)
                }
                Initiator.load("com.tencent.mobileqq.data.MessageForArkApp")
                    .isAssignableFrom(data.javaClass) -> {
                    val text = ReflexUtil.invoke_virtual(
                        ReflexUtil.iget_object_or_null(
                            data,
                            "ark_app_message"
                        ), "toAppXml", *arrayOfNulls(0)
                    ) as String
                    processMsg(data, text, regexString)
                }
                else -> false
            }
        } catch (e: Exception) {
            Utils.log(e)
            return false
        }
    }

    private fun processMsg(data: MsgRecordData, text: String, regexString: String): Boolean {
        if (regexStringCache != regexString) {
            regexCache = regexString.toRegex()
            regexStringCache = regexString
        }
        if (regexCache?.matches(text) == true) {
            XposedHelpers.setBooleanField(data.msgRecord, "isread", true)
            return true
        } else return false
    }

    override val preference = uiEditTextPreference {
        title = "万象屏蔽卡片消息"
        summary = "使用强大的正则表达式自由屏蔽卡片消息"
        SyncUtils.post {
            value.observeForever {
                getExFriendCfg().putString(RegexAntiMeg::class.java.simpleName, it)
            }
        }
        inputLayout = {
            helperText = "留空以禁用"
        }
        contextWrapper = CommonContextWrapper::createMaterialDesignContext
    }

    override val preferenceLocate: Array<String> = 辅助功能

}
