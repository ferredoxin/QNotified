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

import me.nextalone.util.hookBefore
import me.nextalone.util.methods
import me.singleneuron.qn_kernel.data.hostInformationProvider
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object HideOnlineNumber : CommonDelayableHook("na_hide_online_number") {
    override fun initOnce(): Boolean {
        return try {
            var className = "com.tencent.mobileqq.activity.aio.core.TroopChatPie"
            if (hostInformationProvider.versionCode <= QQVersion.QQ_8_4_8) {
                className = "com.tencent.mobileqq.activity.aio.rebuild.TroopChatPie"
            }
            for (m: Method in className.methods) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 2 && argt[0] == String::class.java && argt[1] == Boolean::class.java) {
                    m.hookBefore(this) {
                        it.args[0] = ""
                    }
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
