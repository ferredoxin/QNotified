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

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.singleneuron.qn_kernel.data.hostInformationProvider
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object HideTotalNumber : CommonDelayableHook("na_hide_total_number") {
    
    override fun initOnce(): Boolean {
        return try {
            var className = "com.tencent.mobileqq.activity.aio.core.TroopChatPie"
            if (hostInformationProvider.versionCode <= QQVersion.QQ_8_4_8) {
                className = "com.tencent.mobileqq.activity.aio.rebuild.TroopChatPie"
            }
            for (m: Method in getMethods(className)) {
                val argt = m.parameterTypes
                if (m.name == ConfigTable.getConfig(HideTotalNumber::class.simpleName) && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            param.result = false
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
