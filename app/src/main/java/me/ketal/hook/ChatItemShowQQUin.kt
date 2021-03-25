/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */

package me.ketal.hook

import android.view.View
import android.view.ViewGroup
import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.util.method
import me.ketal.dispacher.OnBubbleBuilder
import me.singleneuron.qn_kernel.data.MsgRecordData
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Utils
import java.text.SimpleDateFormat
import java.util.*

object ChatItemShowQQUin : CommonDelayableHook("ketal_ChatItem_ShowQQUin"), OnBubbleBuilder {

    override fun initOnce() = isValid

    override fun onGetView(rootView: ViewGroup, chatMessage: MsgRecordData, param: XC_MethodHook.MethodHookParam) {
        if (!isEnabled) return
        val listener = View.OnClickListener {
            CustomDialog.createFailsafe(it.context)
                .setTitle(Utils.`getShort$Name`(chatMessage.msgRecord))
                .setMessage(chatMessage.msgRecord.toString())
                .setPositiveButton("确认", null)
                .show()
        }
        val text = "QQ:${chatMessage.senderUin}  Time:" + SimpleDateFormat(
            "MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date(chatMessage.time!! * 1000))
        "Lcom/tencent/mobileqq/activity/aio/BaseChatItemLayout;->setTailMessage(ZLjava/lang/CharSequence;Landroid/view/View\$OnClickListener;)V"
            .method
            .invoke(rootView, true, text, listener)
    }
}
