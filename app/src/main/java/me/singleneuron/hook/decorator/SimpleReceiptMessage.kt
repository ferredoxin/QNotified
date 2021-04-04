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
import nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null

@UiItem
object SimpleReceiptMessage : BaseItemBuilderFactoryHookDecorator("simpleReceiptMessage") {

    override fun doDecorate(
        result: Int,
        chatMessage: Any,
        param: XC_MethodHook.MethodHookParam
    ): Boolean {
        if (result == 5) {
            val id = iget_object_or_null(
                iget_object_or_null(
                    param.args[param.args.size - 1],
                    "structingMsg"
                ), "mMsgServiceID"
            ) as Int
            if (id == 107) {
                XposedHelpers.setObjectField(chatMessage, "msg", "[回执消息]")
                param.result = -1
                return true
            }
        }
        return false
    }

    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "回执消息文本化"
    }

    override val preferenceLocate: Array<String> = arrayOf("净化功能")

}
