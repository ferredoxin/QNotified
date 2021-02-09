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
package me.nextalone.hook.testhook

import de.robv.android.xposed.XC_MethodHook
import me.kyuubiran.util.getMethods
import me.nextalone.util.Utils.hook
import me.nextalone.util.logd
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object TestCommonDelayable : CommonDelayableHook("na_test_base_delayable_kt") {

    override fun initOnce(): Boolean {
        return try {
            val hookSimpleName = this::class.java.simpleName
            val className = ""
            for (m: Method in getMethods(className)) {
                val argt = m.parameterTypes
                if (m.name == "methodName" && argt.size == 1) {
                    m.hook(object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            logd("B", hookSimpleName)
                        }

                        override fun afterHookedMethod(param: MethodHookParam) {
                            logd("A", hookSimpleName)
                        }
                    })
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
