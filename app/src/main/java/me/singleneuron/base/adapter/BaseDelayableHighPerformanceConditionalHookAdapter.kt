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
package me.singleneuron.base.adapter

import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import nil.nadph.qnotified.SyncUtils

/*
请在大量调用的情况下使用此类并标注为重启QQ后生效
使用此类后极大幅度提高了简洁模式圆头像的性能，使用此类前每次调用时间为1~6ms，使用后每次调用20μs内完成
测试环境：Mi9 PixelExperience EdXposed(Sandhook)
 */
abstract class BaseDelayableHighPerformanceConditionalHookAdapter @JvmOverloads constructor(
    string: String,
    proc: Int = SyncUtils.PROC_MAIN
) : BaseDelayableConditionalHookAdapter(string, proc) {

    //protected var highPerformanceEnabled by Delegates.notNull<Boolean>()
    protected var highPerformanceEnabledCache: PageFaultHighPerformanceFunctionCache<Boolean> =
        PageFaultHighPerformanceFunctionCache { super.checkEnabled() }

    override fun checkEnabled(): Boolean {
        return highPerformanceEnabledCache.getValue()
    }

}
