/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

package me.ketal.hook

import android.app.Activity
import android.os.Build
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.tlb.辅助功能
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import xyz.nextalone.util.replace
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
@UiItem
object FakeMultiWindowStatus : CommonDelayAbleHookBridge(
    SyncUtils.PROC_ANY
) {
    override val preference = uiSwitchPreference {
        title = "伪装处于非多窗口模式"
        summary = "用于分屏状态使用一些功能,例如扫码"
    }
    override val preferenceLocate = 辅助功能

    override fun isValid() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    override fun initOnce() = tryOrFalse {
        Activity::class.java.getDeclaredMethod("isInMultiWindowMode")
            .replace(this, false)
    }

}
