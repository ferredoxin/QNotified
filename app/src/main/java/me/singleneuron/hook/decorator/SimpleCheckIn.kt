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
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.decorator.BaseItemBuilderFactoryHookDecorator
import me.singleneuron.qn_kernel.ui.base.UiSwitchPreference
import nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null
import nil.nadph.qnotified.util.ReflexUtil.invoke_virtual

@UiItem
object SimpleCheckIn : BaseItemBuilderFactoryHookDecorator("qn_sign_in_as_text") {

    override fun doDecorate(
        result: Int,
        chatMessage: Any,
        param: XC_MethodHook.MethodHookParam
    ): Boolean {
        if (result == 71 || result == 84) {
            param.result = -1
            return true
        } else if (result == 47) {
            val json = invoke_virtual(
                iget_object_or_null(
                    param.args[param.args.size - 1],
                    "ark_app_message"
                ), "toAppXml", *arrayOfNulls(0)
            ) as String
            if (json.contains("com.tencent.qq.checkin")) {
                param.result = -1
                return true
            }
        }
        return false
    }

    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "签到文本化"
    }

    override val preferenceLocate: Array<String> = arrayOf("净化功能")

}
