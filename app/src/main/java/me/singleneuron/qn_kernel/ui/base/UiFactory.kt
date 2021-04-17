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
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.tlb.UiMap
import nil.nadph.qnotified.R
import nil.nadph.qnotified.ui.CommonContextWrapper

class UiCategoryFactory: UiCategory{
    override lateinit var name: String
    override var contains: UiMap = linkedMapOf()
}

class UiClickToActivityPreferenceFactory : UiPreference {

    override lateinit var title: String
    override var summary: String? = null
    override var onClickListener: (Context) -> Boolean = { true }
    lateinit var activity: Class<out Activity>

    fun create() {
        onClickListener = {
            hostInfo.application.startActivity(Intent(hostInfo.application, activity))
            true
        }
    }
}

class MaterialAlertDialogPreferenceFactory(context: Context) : MaterialAlertDialogBuilder(context, R.style.MaterialDialog), UiChangeablePreference<String> {
    override lateinit var title: String
    override var summary: String? = null
    override var onClickListener: (Context) -> Boolean = { true }
    override val value: MutableLiveData<String?> = MutableLiveData()
}

class EditPreferenceFactory(context: Context) : UiEditTextPreference, TextInputEditText(CommonContextWrapper.createMaterialDesignContext(context), null, R.style.MaterialDialog) {
    override val value: MutableLiveData<String?> = MutableLiveData()
    override lateinit var title: String
    override var summary: String? = null
    override lateinit var onClickListener: (Context) -> Boolean
    var inputLayoutSetter: TextInputLayout.() -> Unit = {}
}

class UiScreenFactory : UiScreen {
    override var summary: String? = null
    override lateinit var name: String
    override var contains: UiMap = linkedMapOf()
}
