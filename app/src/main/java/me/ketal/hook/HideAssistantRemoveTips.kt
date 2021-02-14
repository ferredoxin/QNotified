/* QNotified - An Xposed module for QQ/TIM
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

import me.ketal.util.HookUtil.getMethod
import me.nextalone.util.hookBefore
import me.nextalone.util.hookNull
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

object HideAssistantRemoveTips: CommonDelayableHook("ketal_hide_assistant_removetips") {
    override fun initOnce(): Boolean {
        return try {
            "Lcom/tencent/mobileqq/activity/ChatActivityUtils;->a(Landroid/content/Context;Ljava/lang/String;Landroid/view/View\$OnClickListener;Landroid/view/View\$OnClickListener;)Landroid/view/View;"
                .getMethod()
                ?.hookBefore(this, hookNull)
            true
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }
}
