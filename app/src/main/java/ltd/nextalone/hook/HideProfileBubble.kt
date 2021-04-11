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
package ltd.nextalone.hook

import android.view.View
import android.view.ViewGroup
import ltd.nextalone.util.*
import me.ketal.util.PlayQQVersion.PlayQQ_8_2_9
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.data.requireMinPlayQQVersion
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Initiator
import java.lang.reflect.Method

@FunctionEntry
object HideProfileBubble : BaseDelayableHighPerformanceConditionalHookAdapter("hideProfileBubble") {

    override val recordTime: Boolean = false

    override fun doInit() = tryOrFalse {
        if (requireMinQQVersion(QQVersion.QQ_8_6_5)) {
            "com.tencent.mobileqq.activity.QQSettingMe".clazz?.hookAfterAllConstructors {
                val viewGroup = it.thisObject.get("a", ViewGroup::class.java)
                viewGroup?.findHostView<View>("mvi")?.hide()
            }
        } else if (requireMinQQVersion(QQVersion.QQ_8_6_0)) {
            "com.tencent.mobileqq.activity.QQSettingMe".clazz?.hookAfterAllConstructors {
                val viewGroup = it.thisObject.get("a", ViewGroup::class.java)
                viewGroup?.findHostView<View>("cgf")?.hide()
            }
        } else {
            val clz = Initiator.load("com.tencent.mobileqq.activity.QQSettingMe")
            for (m: Method in clz.declaredMethods) {
                val argt = m.parameterTypes
                if (m.name == ConfigTable.getConfig(HideProfileBubble::class.simpleName) && !m.isStatic && argt.isEmpty()) {
                    m.replace(this, null)
                }
            }
        }
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> =
        PageFaultHighPerformanceFunctionCache { requireMinQQVersion(QQVersion.QQ_8_3_6) or requireMinPlayQQVersion(PlayQQ_8_2_9) }

}
