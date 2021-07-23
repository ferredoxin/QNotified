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

package ltd.nextalone.hook

import android.widget.ImageView
import androidx.core.view.isVisible
import ltd.nextalone.util.clazz
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import ltd.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.ui.base.净化功能
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
@UiItem
object HideRedPoints : CommonDelayAbleHookBridge() {

    override val preference = uiSwitchPreference {
        title = "隐藏部分小红点"
    }

    override val preferenceLocate = 净化功能

    override fun initOnce(): Boolean = tryOrFalse {
        "com.tencent.mobileqq.tianshu.ui.RedTouch".clazz?.method("a", 1, ImageView::class.java) {
            it.parameterTypes[0] == Int::class.java
        }?.hookAfter(this) {
            (it.result as ImageView).isVisible = false
        }
    }
}
