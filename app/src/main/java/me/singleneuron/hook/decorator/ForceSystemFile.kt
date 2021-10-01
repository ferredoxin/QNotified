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

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import me.singleneuron.activity.ChooseAgentActivity
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.decorator.BaseStartActivityHookDecorator
import me.singleneuron.qn_kernel.ui.base.辅助功能

@UiItem
object ForceSystemFile : BaseStartActivityHookDecorator() {

    override fun doDecorate(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        if (intent.component?.className?.contains("filemanager.activity.FMActivity") == true) {
            val context = hostInfo.application
            val newIntent = Intent(context, ChooseAgentActivity::class.java)
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            newIntent.putExtras(intent)
            newIntent.type = "*/*"
            context.startActivity(newIntent)
            param.result = null
            return true
        }
        return false
    }

    override val preference = uiSwitchPreference {
        title = "强制使用系统文件"
        summary = "支持8.3.6及更高"
    }

    override val preferenceLocate: Array<String> = 辅助功能

}
