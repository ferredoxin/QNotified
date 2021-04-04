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

package me.singleneuron.qn_kernel.ui.base

import android.app.Activity
import android.content.Intent
import me.singleneuron.qn_kernel.data.hostInfo

class UiCategoryFactory: UiCategory{
    override lateinit var name: String
    override var contains: MutableMap<String, UiDescription> = linkedMapOf()
}

class UiPreferenceFactory: UiPreference {

    override lateinit var title: String
    override var summary: String? = null
    override var onClickListener: () -> Boolean = {true}
    lateinit var activity: Class<out Activity>

    fun create() {
        onClickListener = {
            hostInfo.application.startActivity(Intent(hostInfo.application, activity))
            true
        }
    }
}
