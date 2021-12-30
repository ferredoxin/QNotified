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

import android.content.Intent
import cc.ioctl.activity.JefsRulesActivity
import me.singleneuron.qn_kernel.ui.gen.getAnnotatedUiItemClassList
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Toasts
import org.ferredoxin.ferredoxinui.common.base.*

val Config: UiScreen = uiScreen {
    name = "参数设定"
    contains = linkedMapOf(
        uiCategory {
            noTitle = true
            name = "参数设定"
            contains = linkedMapOf(
                uiClickableItem {
                    title = "跳转控制"
                    summary = "跳转自身及第三方Activity控制"
                    onClickListener = {
                        if (Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass") != null) {
                            it.startActivity(Intent(it, JefsRulesActivity::class.java))
                        } else {
                            Toasts.error(it, "当前版本客户端版本不支持")
                        }
                        true
                    }
                }
            )
        }
    )
    loadUiInList(contains, getAnnotatedUiItemClassList())
    contains
}.second
