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
import android.text.TextUtils
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import me.ketal.ui.view.BViewGroup
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.R
import nil.nadph.qnotified.ui.ResUtils

class ListItemButton(context: Context) : BViewGroup(context) {

    var title: String?
        get() {
            return titleTextView.text.toString()
        }
        set(value) {
            titleTextView.text = value
        }

    var summary: String?
        get() {
            return descTextView.text.toString()
        }
        set(value) {
            descTextView.text = value
        }

    var value: String?
        get() {
            return valueTextView.text.toString()
        }
        set(value) {
            valueTextView.text = value
        }

    @ColorInt
    var themeColor = ContextCompat.getColor(hostInfo.application, R.color.colorPrimary)

    @ColorInt
    var firstColor: Int = ResUtils.skin_black.defaultColor

    @ColorInt
    var secondColor: Int = ResUtils.skin_black.defaultColor

    @ColorInt
    var thirdColor: Int = ResUtils.skin_gray3.defaultColor

    init {
        ResUtils.initTheme(context)
        background = ResUtils.getListItemBackground()
        kotlin.runCatching {
            val typedArray = context.theme.obtainStyledAttributes(intArrayOf(
                android.R.attr.textColor,
                android.R.attr.textColorSecondary,
                android.R.attr.textColorTertiary,
                android.R.attr.colorPrimary
            ))
            firstColor = typedArray.getColor(0, firstColor)
            secondColor = typedArray.getColor(1, secondColor)
            thirdColor = typedArray.getColor(2, thirdColor)
            themeColor = typedArray.getColor(3, themeColor)
            typedArray.recycle()
            if (ResUtils.isInNightMode()) {
                firstColor = 0x00FFFFFF xor firstColor
                secondColor = 0x00FFFFFF xor secondColor
                thirdColor = 0x00FFFFFF xor thirdColor
            }
        }
    }

    private val titleTextView = TextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setTextColor(firstColor)
        textSize = 18.dp2sp
        addView(this)
    }

    private var hasDesc = false

    private val descTextView by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setTextColor(secondColor)
            textSize = 13.dp2sp
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            hasDesc = true
            addView(this)
        }
    }

    private val valueTextView = TextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setTextColor(thirdColor)
        textSize = 15.dp2sp
        addView(this)
    }

    private val arrow = ImageView(context).apply {
        layoutParams = LayoutParams(9.dp, 15.dp)
        setImageDrawable(ResUtils.skin_icon_arrow_right_normal)
        //ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(firstColor))
        addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        arrow.autoMeasure()
        valueTextView.autoMeasure()
        val descWidth = measuredWidth - arrow.measuredWidth - (14 + 14 + 6).dp
        val titleWidth = descWidth - valueTextView.measuredWidth
        titleTextView.measure(titleWidth.toExactlyMeasureSpec(), defaultHeightMeasureSpec(this))
        if (hasDesc) descTextView.measure(descWidth.toExactlyMeasureSpec(), defaultHeightMeasureSpec(this))
        setMeasuredDimension(measuredWidth, 48.dp)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val margin = 14.dp
        arrow.layout(margin, arrow.toVerticalCenter(this), true)
        valueTextView.layout(margin + arrow.measuredWidth + 3.dp, valueTextView.toVerticalCenter(this), true)
        if (hasDesc) {
            titleTextView.layout(margin, margin / 2)
            descTextView.layout(margin, titleTextView.bottom)
        } else {
            titleTextView.layout(margin, titleTextView.toVerticalCenter(this))
        }
    }
}
