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

package me.singleneuron.qn_kernel.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceFragmentCompat
import me.singleneuron.qn_kernel.tlb.UiTable
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.activity.MaterialSettingActivity
import org.ferredoxin.ferredoxinui.common.base.TitleAble
import org.ferredoxin.ferredoxinui.common.fragment.MaterialSettingFragment

class MaterialActivity<T> : MaterialSettingActivity<T>() where T : PreferenceFragmentCompat, T : TitleAble {

    override val fragment: T = MaterialSettingFragment().setUiScreen(UiTable.second) as T
    override val theme: MaterialTheme = MaterialTheme.Classic

    private val mLoader by lazy { this::class.java.classLoader }

    override fun getClassLoader() = mLoader!!

    override fun onCreate(savedInstanceState: Bundle?) {
        runCatching {
            AppCompatDelegate.setDefaultNightMode(
                if (ResUtils.isInNightMode()) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }.onFailure {
            Utils.log(it)
        }
        super.onCreate(savedInstanceState)
    }

}
