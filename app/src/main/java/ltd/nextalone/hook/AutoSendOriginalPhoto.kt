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

import android.view.View
import android.widget.CheckBox
import ltd.nextalone.util.hookAfter
import me.kyuubiran.util.getMethods
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils
import nil.nadph.qnotified.util.Utils.PACKAGE_NAME_QQ
import java.lang.reflect.Method

object AutoSendOriginalPhoto : CommonDelayableHook("na_test_forced_original") {

    override fun initOnce(): Boolean {
        return try {
            for (m: Method in getMethods("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] == Boolean::class.java) {
                    m.hookAfter(this) {
                        val ctx = it.thisObject as View
                        val id = ctx.resources.getIdentifier("h1y", "id", PACKAGE_NAME_QQ)
                        val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(id)
                        sendOriginPhotoCheckbox.isChecked = true
                    }
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
