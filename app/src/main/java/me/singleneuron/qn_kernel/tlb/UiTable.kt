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

package me.singleneuron.qn_kernel.tlb

import me.singleneuron.qn_kernel.ui.base.UiDescription
import me.singleneuron.qn_kernel.ui.base.UiScreen
import me.singleneuron.util.uiCategory
import me.singleneuron.util.uiClickToActivityItem
import nil.nadph.qnotified.activity.BetaTestFuncActivity
import nil.nadph.qnotified.activity.OmegaTestFuncActivity

object UiTable: UiScreen {
    override var name: String = "QNotified"
    override var contains: MutableMap<String, UiDescription> = linkedMapOf(
        uiCategory {
            name = "希腊字母"
            contains = hashMapOf(
                uiClickToActivityItem {
                    title = "Beta测试"
                    summary = "仅用于测试稳定性"
                    activity = BetaTestFuncActivity::class.java
                },
                uiClickToActivityItem {
                    title = "Omega测试"
                    summary = "这是个不存在的功能"
                    activity = OmegaTestFuncActivity::class.java
                }
            )
        },
        uiCategory {
            name = "净化功能"
        },
        uiCategory {
            name = "增强功能"
        },
        uiCategory {
            name = "辅助功能"
        },
        uiCategory {
            name = "其他功能"
        },
        uiCategory {
            name = "实验性功能"
        }
    )
}
