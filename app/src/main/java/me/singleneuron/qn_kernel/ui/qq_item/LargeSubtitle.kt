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

package me.singleneuron.qn_kernel.ui.qq_item

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.R
import nil.nadph.qnotified.util.Utils.dip2px
import nil.nadph.qnotified.util.Utils.dip2sp

class LargeSubtitle @JvmOverloads constructor(val ctx: Context, attr: AttributeSet? = null, @StyleRes defStyleAttr: Int = 0) : LinearLayout(ctx, attr, defStyleAttr) {

    val textView = TextView(ctx)

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        @ColorInt var themeColor = ContextCompat.getColor(hostInfo.application, R.color.colorPrimary)
        kotlin.runCatching {
            val typedArray = context.theme.obtainStyledAttributes(intArrayOf(
                android.R.attr.colorPrimary
            ))
            themeColor = typedArray.getColor(0, themeColor)
            typedArray.recycle()
        }
        textView.apply {
            setTextIsSelectable(false)
            textSize = dip2sp(ctx, 13F)
            setTextColor(themeColor)
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
        layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        val m = dip2px(ctx, 14F)
        textView.setPadding(m, m, m / 5, m / 5)
        addView(textView)
    }

    var title: String?
        get() {
            return textView.text.toString()
        }
        set(value) {
            textView.text = value
        }

}
