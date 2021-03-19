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

import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.forEach
import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.util.findHostView
import me.ketal.dispacher.OnBubbleBuilder
import me.singleneuron.qn_kernel.data.MsgRecordData
import me.singleneuron.qn_kernel.data.MsgRecordData.Companion.MSG_TYPE_REPLY_TEXT
import me.singleneuron.qn_kernel.data.MsgRecordData.Companion.MSG_TYPE_TEXT
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.MainHook
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils
import org.json.JSONArray
import org.json.JSONObject

object ShowMsgAt : CommonDelayableHook("Ketal_HideTroopLevel"), OnBubbleBuilder {

    override fun initOnce() = !hostInfo.isTim

    override fun onGetView(
        rootView: ViewGroup,
        chatMessage: MsgRecordData,
        param: XC_MethodHook.MethodHookParam
    ) {
        if (!isEnabled || 1 != chatMessage.isTroop) return
        val extStr = chatMessage.extStr ?: return
        val json = JSONObject(extStr)
        if (json.has("troop_at_info_list")) {
            val at = JSONArray(json["troop_at_info_list"] as String)
            when (chatMessage.msgType) {
                MSG_TYPE_TEXT, // TODO MSG_TYPE_MIX,
                MSG_TYPE_REPLY_TEXT -> {
                    when (val content = rootView.findHostView<View>("chat_item_content_layout")!!) {
                        is TextView -> {
                            copeAtInfo(content, at)
                        }
                        is ViewGroup -> {
                            content.forEach {
                                if (it is TextView)
                                    copeAtInfo(it, at)
                            }
                        }
                        else -> {
                            Utils.logd("暂不支持的控件类型--->$content")
                            return
                        }
                    }
                }
                else -> {
                    Utils.logd("暂不支持的消息类型--->${chatMessage.msgType}")
                    return
                }
            }
        }
    }

    private fun copeAtInfo(textView: TextView, at: JSONArray) {
        val spannableString = SpannableString(textView.text)
        for (i in 0 until at.length()) {
            val con = at[i] as JSONObject
            val uin = con["uin"].toString().toLong()
            val start = con["startPos"] as Int
            val length = con["textLen"] as Int
            spannableString.setSpan(OpenQQSpan(uin), start, start + length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        textView.text = spannableString
        textView.movementMethod = LinkMovementMethod.getInstance()
    }
}

class OpenQQSpan(val qq: Long) : ClickableSpan() {
    override fun onClick(v: View) {
        MainHook.openProfileCard(v.context, qq)
    }
}
