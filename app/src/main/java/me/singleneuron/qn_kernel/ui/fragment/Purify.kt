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

package me.singleneuron.qn_kernel.ui.fragment

import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import org.ferredoxin.ferredoxinui.common.base.ViewMap
import org.ferredoxin.ferredoxinui.common.base.loadUiInList
import org.ferredoxin.ferredoxinui.common.base.uiCategory
import org.ferredoxin.ferredoxinui.common.base.uiScreen

val Purify: ViewMap = listOf(
    uiScreen {
        name = "主页"
        contains = linkedMapOf(
            uiCategory {
                name = "主页"
            },
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "侧滑"
        contains = linkedMapOf(
            uiCategory {
                name = "侧滑"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "聊天"
        contains = linkedMapOf(
            uiCategory {
                name = "聊天"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "群聊"
        contains = linkedMapOf(
            uiCategory {
                name = "群聊"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
    uiScreen {
        name = "扩展"
        contains = linkedMapOf(
            uiCategory {
                name = "扩展"
            }
        )
        loadUiInList(contains, getAnnotatedUiItemClassList())
        contains
    },
)

