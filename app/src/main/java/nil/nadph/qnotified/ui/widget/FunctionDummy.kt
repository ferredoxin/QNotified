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

package nil.nadph.qnotified.ui.widget

import android.content.Context
import android.text.TextUtils
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import me.ketal.ui.view.BViewGroup
import nil.nadph.qnotified.ui.ResUtils

class FunctionDummy(context: Context) : BViewGroup(context)  {

    init {
        background = ResUtils.getListItemBackground()
    }
    val title = TextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setTextColor(ResUtils.skin_black)
        textSize = 18.dp2sp.toFloat()
        addView(this)
    }
    private var hasDesc = false
    val desc by lazy {
        TextView(context).apply {
            layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
            setTextColor(ResUtils.skin_gray3)
            textSize = 13.dp2sp.toFloat()
            isSingleLine = true
            ellipsize = TextUtils.TruncateAt.END
            hasDesc = true
            addView(this)
        }
    }
    val value = TextView(context).apply {
        layoutParams = LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        setTextColor(ResUtils.skin_gray3)
        textSize = 15.dp2sp.toFloat()
        addView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        value.autoMeasure()
        val descWidth = measuredWidth - 28.dp
        val titleWidth = descWidth - value.measuredWidth
        title.measure(titleWidth.toExactlyMeasureSpec(), defaultHeightMeasureSpec(this))
        if (hasDesc) desc.measure(descWidth.toExactlyMeasureSpec(), defaultHeightMeasureSpec(this))
        setMeasuredDimension(measuredWidth, 48.dp)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val margin = 14.dp
        value.layout(margin, value.toVerticalCenter(this), true)
        if (hasDesc) {
            title.layout(margin, margin / 2)
            desc.layout(margin, title.bottom)
        } else {
            title.layout(margin, title.toVerticalCenter(this))
        }
    }
}
