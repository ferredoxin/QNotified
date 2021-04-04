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
package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.ui.base.UiDescription
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

@UiItem
@FunctionEntry
object ForceSystemCamera : CommonDelayAbleHookBridge("forceSystemCamera") {

    override fun getPreconditions(): Array<Step> {
        //特征字符串："CaptureUtil"
        return arrayOf(DexDeobfStep(DexKit.C_CaptureUtil))
    }

    override fun initOnce(): Boolean {
        return try {
            for (m in DexKit.doFindClass(DexKit.C_CaptureUtil)!!.declaredMethods) {
                val argt = m.parameterTypes
                if ("a" == m.name && m.returnType == Boolean::class.javaPrimitiveType && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return
                            }
                            if (!isEnabled) {
                                return
                            }
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

    override val preference: UiDescription = uiSwitchPreference {
        title = "强制使用系统相机"
        summary = "支持8.3.6及更高"
    }

    override val preferenceLocate: Array<String> = arrayOf("增强功能")

}
