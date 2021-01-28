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
package me.singleneuron.base.adapter

import me.singleneuron.base.Conditional
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.SyncUtils

abstract class BaseDelayableConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableHookAdapter(string, proc), Conditional {

    //如有更改重启后生效
    protected open val conditionCache : PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache {
        try {
            ConfigTable.getConfig<Any?>(this::class.simpleName) != null
        } catch (e:Exception) {
            false
        }
    }

    override val condition : Boolean
    get() {
        return conditionCache.getValue()
    }

    override fun checkEnabled(): Boolean {
        return condition&&super.checkEnabled()
    }

}
