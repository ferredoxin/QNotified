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

@file:Suppress("DEPRECATION")

package me.singleneuron.qn_kernel.ui

import android.annotation.SuppressLint
import android.os.Bundle
import me.singleneuron.qn_kernel.tlb.UiTable
import me.singleneuron.qn_kernel.ui.base.UiScreen
import nil.nadph.qnotified.R
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat
import nil.nadph.qnotified.databinding.ActivityNewSettingsBinding
import nil.nadph.qnotified.ui.ResUtils

@SuppressLint("Registered")
class NewSettingsActivity : IphoneTitleBarActivityCompat() {

    private lateinit var binding: ActivityNewSettingsBinding

    override fun doOnCreate(bundle: Bundle?): Boolean {
        super.doOnCreate(bundle)

        binding = ActivityNewSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        changeFragment(UiTable)

        setContentBackgroundDrawable(ResUtils.skin_background)
        return true

    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun changeFragment(uiScreen: UiScreen) {
        val rootFragment = SettingsFragment().setUiScreen(uiScreen)
        fragmentManager.beginTransaction().replace(R.id.new_setting_container, rootFragment).addToBackStack(uiScreen.name).commit()
        setTitle(uiScreen.name)
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            super.onBackPressed()
        } else {
            val string = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 2).name
            setTitle(string)
            fragmentManager.popBackStack()
        }
    }

}
