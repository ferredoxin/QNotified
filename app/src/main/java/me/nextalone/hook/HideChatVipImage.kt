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
package me.nextalone.hook

import android.widget.ImageView
import android.widget.RelativeLayout
import me.nextalone.util.clazz
import me.nextalone.util.hookAfterAllConstructors
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

object HideChatVipImage : CommonDelayableHook("na_hide_chat_vip_image_kt") {

    override fun initOnce(): Boolean {
        return try {
            "com.tencent.mobileqq.widget.navbar.NavBarAIO".clazz.hookAfterAllConstructors {
                val ctx = it.thisObject as RelativeLayout
                val titleImageId = ctx.resources.getIdentifier("jp0", "id", Utils.PACKAGE_NAME_QQ)
                ctx.findViewById<ImageView>(titleImageId).alpha = 0F
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
