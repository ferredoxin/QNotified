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

package me.singleneuron.qn_kernel.ui

import android.annotation.SuppressLint
import android.os.Bundle
import me.singleneuron.qn_kernel.tlb.UiTable
import me.singleneuron.qn_kernel.ui.base.UiDescription
import me.singleneuron.qn_kernel.ui.base.UiGroup
import me.singleneuron.qn_kernel.ui.base.UiPreference
import me.singleneuron.qn_kernel.ui.base.UiScreen
import me.singleneuron.qn_kernel.ui.gen.AnnotatedUiItemList
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

        initUiTable()
        changeFragment(UiTable)

        setContentBackgroundDrawable(ResUtils.skin_background)
        return true

    }

    fun setTitle(title:String) {
        this.title = title
    }

    fun changeFragment(uiScreen: UiScreen) {
        val rootFragment = SettingsFragment().setUiScreen(UiTable)
        fragmentManager.beginTransaction().replace(R.id.new_setting_container,rootFragment).addToBackStack(null).commit()
        setTitle(uiScreen.name)
    }

    private fun initUiTable() {
        for (uiItem in AnnotatedUiItemList.getAnnotatedUiItemClassList()) {
            addUiItemToUiGroup(uiItem.preference, UiTable, uiItem.preferenceLocate, 0)
        }
    }

    private fun addUiItemToUiGroup(uiDescription: UiDescription, uiGroup: UiGroup, array: Array<String>?, point:Int){
        if (array==null || array.isEmpty()) {
            if (uiDescription is UiPreference) {
                val contains = uiGroup.contains[uiDescription.title]
                if (contains is UiGroup) {
                    contains.contains[uiDescription.title] = uiDescription
                }
            }
        } else {
            val contains = uiGroup.contains
            if (contains.containsKey(array[point])) {
                if (point == array.size-1) {
                    val ui = contains[array[point]]
                    if (ui!=null&&ui is UiGroup) {
                        if (uiDescription is UiPreference) {
                            ui.contains[uiDescription.title] = uiDescription
                        } else if (uiDescription is UiGroup) {
                            ui.contains[uiDescription.name] = uiDescription
                        }
                    }
                } else {
                    val ui = contains[array[point]]
                    if (ui is UiGroup) {
                        addUiItemToUiGroup(uiDescription, ui, array, point+1)
                    }
                }
            }
        }
    }

}
