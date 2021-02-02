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

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator._BaseChatPie
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//聊天界面顶栏群名字/好友昵称自动打码
object AutoMosaicName : CommonDelayableHook("kr_automatic_mosaic_name") {

    override fun initOnce(): Boolean {
        return try {
            for (m: Method in _BaseChatPie().declaredMethods) {
                val argt = m.parameterTypes
                if (argt.size == 1 && argt[0] == Boolean::class.java && m.name == ConfigTable.getConfig(AutoMosaicName::class.java.simpleName)) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            param.args[0] = true
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
