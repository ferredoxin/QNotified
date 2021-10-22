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
package xyz.nextalone.hook

import android.app.Activity
import android.view.View
import xyz.nextalone.base.MultiItemDelayableHook
import xyz.nextalone.util.*
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.ui.base.净化功能
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
@UiItem
object SimplifyQQSettings : MultiItemDelayableHook("na_simplify_qq_settings_multi") {
    override val preferenceLocate: Array<String> = 净化功能
    override val preferenceTitle = "精简设置菜单"
    override val allItems = "手机号码|达人|安全|模式选择|通知|记录|隐私|通用|辅助|免流量|关于"
    override val defaultItems = ""

    override fun initOnce() = tryOrFalse {
        "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->a(IIII)V".method.hookAfter(this) {
            val activity = it.thisObject as Activity
            val viewId: Int = it.args[0].toString().toInt()
            val strId: Int = it.args[1].toString().toInt()
            val view = activity.findViewById<View>(viewId)
            val str = activity.getString(strId)
            if (activeItems.any { string ->
                    string.isNotEmpty() && string in str
                }) {
                view.hide()
            }
        }
        if (activeItems.contains("免流量")) {
            try {
                "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->a()V".method.replace(
                    this,
                    null
                )
            } catch (t: Throwable) {
                "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->b()V".method.replace(
                    this,
                    null
                )
            }
        }
    }
}
