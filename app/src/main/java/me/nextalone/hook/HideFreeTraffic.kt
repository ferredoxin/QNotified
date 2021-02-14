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
package me.nextalone.hook

import me.nextalone.util.hookNull
import me.nextalone.util.method
import me.nextalone.util.replace
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

object HideFreeTraffic : CommonDelayableHook("na_hide_free_traffic_kt") {
    override fun initOnce(): Boolean {
        return try {
            "Lcom/tencent/mobileqq/activity/QQSettingSettingActivity;->a()V".method.replace(hookNull)
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
