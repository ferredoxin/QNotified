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

import android.view.View
import android.widget.CheckBox
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import nil.nadph.qnotified.util.Utils.PACKAGE_NAME_QQ
import java.lang.reflect.Method

object ForcedSendOriginalPhoto : CommonDelayableHook("na_test_forced_original") {

    override fun initOnce(): Boolean {
        return try {
            for (m: Method in getMethods("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] == Boolean::class.java) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam?) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val ctx = param!!.thisObject as View
                            val id = ctx.resources.getIdentifier("h1y", "id", PACKAGE_NAME_QQ)
                            val sendOriginPhotoCheckbox: CheckBox = ctx.findViewById(id)
                            sendOriginPhotoCheckbox.isChecked = true
                        }
                    })
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
