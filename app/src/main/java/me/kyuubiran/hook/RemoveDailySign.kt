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
package me.kyuubiran.hook

import android.widget.LinearLayout
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getObjectOrNull
import me.kyuubiran.util.loadClass
import me.kyuubiran.util.setViewZeroSize
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

//移除侧滑栏左上角打卡
object RemoveDailySign : CommonDelayableHook("kr_remove_daily_sign") {

    override fun initOnce(): Boolean {
        return try {
            XposedBridge.hookAllConstructors(loadClass("com.tencent.mobileqq.activity.QQSettingMe"), object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    val dailySignLayout = getObjectOrNull(param?.thisObject, "a", LinearLayout::class.java) as LinearLayout
                    dailySignLayout.setViewZeroSize()
                }
            })
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
