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
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import me.ketal.util.TIMVersion
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils
import java.text.SimpleDateFormat
import java.util.*

object ChatItemShowQQUin : CommonDelayableHook("ketal_ChatItem_ShowQQUin") {
    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_0_0, TIMVersion.TIM_1_0_0)

    override fun initOnce() = try {
        "Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;->a(IILcom/tencent/mobileqq/data/ChatMessage;Landroid/view/View;Landroid/view/ViewGroup;Lcom/tencent/mobileqq/activity/aio/OnLongClickAndTouchListener;)Landroid/view/View;"
            .method.hookAfter(this) {
                val msg = it.args[2]
                val listener = View.OnClickListener {
                    CustomDialog.createFailsafe(it.context)
                        .setTitle(Utils.`getShort$Name`(msg))
                        .setMessage(msg.toString())
                        .setPositiveButton("确认", null)
                        .show()
                }
                val time = ReflexUtil.iget_object_or_null(msg, "time", Long::class.java)
                val qq = ReflexUtil.iget_object_or_null(msg, "senderuin", String::class.java)
                val text = "QQ:$qq  Time:" + SimpleDateFormat("MM-dd HH:mm:ss", Locale.getDefault()).format(Date(time * 1000))
                "Lcom/tencent/mobileqq/activity/aio/BaseChatItemLayout;->setTailMessage(ZLjava/lang/CharSequence;Landroid/view/View\$OnClickListener;)V"
                    .method
                    .invoke(it.result, true, text, listener)
            }
        true
    } catch (e: Exception) {
        Utils.log(e)
        false
    }
}
