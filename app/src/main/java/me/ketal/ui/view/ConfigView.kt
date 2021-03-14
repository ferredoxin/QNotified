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

package me.ketal.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.minusAssign
import androidx.core.view.plusAssign
import nil.nadph.qnotified.util.Utils.dip2px

class ConfigView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    val enable = CheckBox(context)
    var view: View? = null
        set(value) {
            if (value != null && value.parent != null) {
                // Make sure the view is detached from any former parents.
                value.parent as ViewGroup -= value
            }
            if (value != null) {
                this += value
            }
            field = value
        }
    var isVisible: Boolean?
        get() = view?.isVisible
        set(value) {
            if (value != null) {
                view?.isVisible = value
            }
        }
    var isChecked: Boolean
        get() = enable.isChecked
        set(value) {
            enable.isChecked = value
        }

    init {
        layoutParams = LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        orientation = VERTICAL
        this += LinearLayout(context).apply {
            addView(enable)
            setPadding(dip2px(context, 21f), 0, dip2px(context, 21f), 0)
        }
        enable.setOnCheckedChangeListener { _, isChecked -> isVisible = isChecked }
    }

    fun setText(text: String) {
        enable.text = text
    }
}
