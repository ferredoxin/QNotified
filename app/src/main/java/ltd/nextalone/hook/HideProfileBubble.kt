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

import ltd.nextalone.util.replace
import ltd.nextalone.util.tryOrFalse
import nil.nadph.qnotified.util.PlayQQVersion.PlayQQ_8_2_9
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.data.requireMinPlayQQVersion
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import nil.nadph.qnotified.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit

@FunctionEntry
object HideProfileBubble : BaseDelayableHighPerformanceConditionalHookAdapter("hideProfileBubble") {

    override val recordTime: Boolean = false

    override fun getPreconditions(): Array<Step> {
        return arrayOf(DexDeobfStep(DexKit.N_QQSettingMe_updateProfileBubble))
    }

    override fun doInit() = tryOrFalse {
        DexKit.doFindMethod(DexKit.N_QQSettingMe_updateProfileBubble)?.replace(this, null)
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> =
        PageFaultHighPerformanceFunctionCache { requireMinQQVersion(QQVersion.QQ_8_3_6) or requireMinPlayQQVersion(PlayQQ_8_2_9) }

}
