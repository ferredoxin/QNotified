/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.tencent.mobileqq.widget.BounceScrollView
import me.kyuubiran.hook.RemoveRedDot
import nil.nadph.qnotified.hook.BlockFluxThief
import nil.nadph.qnotified.hook.InterceptZipBomb
import nil.nadph.qnotified.hook.PttForwardHook
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.ui.ViewBuilder.*
import nil.nadph.qnotified.util.Utils

@SuppressLint("Registered")
class AuxFuncActivity : IphoneTitleBarActivityCompat() {
    override fun doOnCreate(bundle: Bundle?): Boolean {
        super.doOnCreate(bundle)
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        setContentView(BounceScrollView(this, null).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            addView(ll)
        })

        ll.addView(newListItemHookSwitchInit(this, "拦截异常zip", null, InterceptZipBomb.INSTANCE))
        ll.addView(newListItemHookSwitchInit(this, "拦截异常体积图片加载", null, BlockFluxThief.INSTANCE))

        setContentBackgroundDrawable(ResUtils.skin_background)
        title = "辅助功能"
        return true
    }
}
