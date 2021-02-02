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

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.data.hostInformationProvider
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object HideProfileBubble : BaseDelayableHighPerformanceConditionalHookAdapter("hideProfileBubble") {

    override val recordTime: Boolean = false

    override fun doInit(): Boolean {
        return try {
            val clz = Initiator.load("com.tencent.mobileqq.activity.QQSettingMe")
            for (m: Method in clz.declaredMethods) {
                val argt = m.parameterTypes
                if (m.name == ConfigTable.getConfig(HideProfileBubble::class.simpleName) && !Modifier.isStatic(m.modifiers) && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            param?.result = null
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

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { hostInformationProvider.versionCode >= QQVersion.QQ_8_3_6 }

}
