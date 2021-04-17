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

import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.lifecycle.ProcessLifecycleOwner
import cn.lliiooll.msg.MessageReceiver
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.getExFriendCfg
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.ui.base.UiDescription
import me.singleneuron.qn_kernel.ui.base.UiItem
import me.singleneuron.qn_kernel.ui.base.uiEditTextPreference
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils

@me.singleneuron.qn_kernel.annotation.UiItem
object RegexAntiMeg : MessageReceiver, View.OnClickListener, UiItem {

    private var regexCache: Regex? = null
    private var regexStringCache: String = ""

    override fun onReceive(data: MsgRecordData?): Boolean {
        try {
            if (data == null) return false
            val regexString =
                getExFriendCfg().getStringOrDefault(RegexAntiMeg::class.simpleName, "")
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

    override fun onClick(v: View?) {
        val dialog = CustomDialog.createFailsafe(v!!.context)
        val context = dialog.context
        val _5 = Utils.dip2px(context, 5f)
        val editText = EditText(context)
        editText.setPadding(_5, _5, _5, _5 * 2)
        val params = ViewBuilder.newLinearLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            _5 * 2
        )
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(editText, params)
        dialog.setTitle("设置万象屏蔽卡片消息正则表达式（留空禁用）")
            .setView(linearLayout)
            .setPositiveButton("确定") { _, _ ->
                getExFriendCfg().putString(
                    RegexAntiMeg::class.java.simpleName,
                    editText.text.toString()
                )
            }
            .setNegativeButton("取消", null)
            .create()
            .show()
    }

    override val preference: UiDescription = uiEditTextPreference {
        title = "万象屏蔽卡片消息"
        summary = "使用强大的正则表达式自由屏蔽卡片消息"
        SyncUtils.post {
            value.observe(ProcessLifecycleOwner.get()) {
                getExFriendCfg().putString(RegexAntiMeg::class.java.simpleName, it)
            }
        }
        inputLayoutSetter = {
            helperText = "留空以禁用"
        }
    }

    override val preferenceLocate: Array<String> = arrayOf("辅助功能")

}
