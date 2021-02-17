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
package ltd.nextalone.hook

import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.replaceTrue
import me.kyuubiran.util.getMethods
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object EnableQLog : CommonDelayableHook("na_enable_qlog") {

    override fun initOnce(): Boolean {
        return try {
            for (m: Method in getMethods("com.tencent.qphone.base.util.QLog")) {
                val argt = m.parameterTypes
                if (m.name == "isColorLevel" && argt.isEmpty()) {
                    m.replaceTrue(this)
                }
            }
            for (m: Method in getMethods("com.tencent.qphone.base.util.QLog")) {
                val argt = m.parameterTypes
                if (m.name == "getTag" && argt.size == 1 && argt[0] == String::class.java) {
                    m.hookAfter(this) {
                        val tag = it.args[0]
                        it.result = "NAdump:$tag"
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
