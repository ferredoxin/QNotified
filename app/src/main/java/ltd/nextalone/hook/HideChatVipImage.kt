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
import android.widget.RelativeLayout
import ltd.nextalone.util.clazz
import ltd.nextalone.util.findHostViewById
import ltd.nextalone.util.hookAfterAllConstructors
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

object HideChatVipImage : CommonDelayableHook("na_hide_chat_vip_image_kt") {

    override fun initOnce(): Boolean {
        return try {
            "com.tencent.mobileqq.widget.navbar.NavBarAIO".clazz.hookAfterAllConstructors {
                val ctx = it.thisObject as RelativeLayout
                ctx.findHostViewById<ImageView>("jp0")!!.alpha = 0F
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
