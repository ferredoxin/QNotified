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

import android.content.DialogInterface
import android.view.ViewGroup
import android.widget.LinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.R
import nil.nadph.qnotified.ui.CommonContextWrapper

fun uiClickToActivityItem(init: UiClickToActivityPreferenceFactory.() -> Unit): Pair<String, UiPreference> {
    val uiClickToActivityItemFactory = UiClickToActivityPreferenceFactory()
    uiClickToActivityItemFactory.init()
    return Pair(uiClickToActivityItemFactory.title, uiClickToActivityItemFactory)
}

fun uiCategory(init: UiCategory.() -> Unit): Pair<String, UiCategory> {
    val uiCategory = UiCategoryFactory()
    uiCategory.init()
    return Pair(uiCategory.name, uiCategory)
}

fun uiDialogPreference(init: MaterialAlertDialogPreferenceFactory.() -> Unit): MaterialAlertDialogPreferenceFactory {
    val builder = MaterialAlertDialogPreferenceFactory(CommonContextWrapper.createMaterialDesignContext(hostInfo.application))
    builder.init()
    builder.onClickListener = {
        val builder2 = MaterialAlertDialogPreferenceFactory(CommonContextWrapper.createMaterialDesignContext(it))
        builder2.setTitle(builder.title)
        builder2.init()
        builder2.create().show()
        true
    }
    return builder
}

fun uiEditTextPreference(init: EditPreferenceFactory.() -> Unit): UiEditTextPreference {
    val builder = EditPreferenceFactory(hostInfo.application)
    builder.init()
    builder.onClickListener = {
        val builder2 = MaterialAlertDialogBuilder(CommonContextWrapper.createMaterialDesignContext(it), R.style.MaterialDialog)
        val inputLayout = TextInputLayout(CommonContextWrapper.createMaterialDesignContext(it))
        inputLayout.layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        builder.inputLayoutSetter.invoke(inputLayout)
        val textInputEditText = EditPreferenceFactory(it)
        inputLayout.addView(textInputEditText, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        textInputEditText.init()
        builder2.setTitle(builder.title)
        builder2.setView(inputLayout)
        builder2.setNegativeButton("取消", null)
        builder2.setPositiveButton("确定") { dialog: DialogInterface, _: Int ->
            builder.value.value = textInputEditText.text.toString()
            dialog.dismiss()
        }
        builder2.create().show()
        true
    }
    return builder
}

fun uiScreen(init: UiScreenFactory.() -> Unit): Pair<String, UiScreen> {
    val uiScreenFactory = UiScreenFactory()
    uiScreenFactory.init()
    return Pair(uiScreenFactory.name, uiScreenFactory)
}

