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
package ltd.nextalone.hook

import android.app.Activity
import android.view.View
import ltd.nextalone.base.MultiItemDelayableHook
import ltd.nextalone.util.*
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object SimplifyQQSettings : MultiItemDelayableHook("na_simplify_qq_settings", "保留") {
    override val allItems = "手机号码|达人|安全|通知|记录|隐私|通用|辅助|免流量|关于".split("|").toMutableList()
    override val defaultItems = "手机号码|达人|安全|通知|隐私|通用|辅助|关于"

    override fun initOnce() = try {
        "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->a(IIII)V".method.hookAfter(this) {
            val activity = it.thisObject as Activity
            val viewId: Int = it.args[0].toString().toInt()
            val strId: Int = it.args[1].toString().toInt()
            val view = activity.findViewById<View>(viewId)
            val str = activity.getString(strId)
            if (activeItems.all { string ->
                    string !in str
                }) {
                view.hide()
            }
        }
        if (!activeItems.contains("免流量"))
            "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->a()V".method.replace(hookNull)
        true
    } catch (t: Throwable) {
        Utils.log(t)
        false
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_0_0)
}
