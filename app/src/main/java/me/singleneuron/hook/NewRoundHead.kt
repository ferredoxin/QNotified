/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

object NewRoundHead : BaseDelayableHighPerformanceConditionalHookAdapter("newroundhead") {

    override val recordTime: Boolean = false

    override fun doInit(): Boolean {
        return try {
            //参数和值都是byte类型
            //这个方法在QQ主界面初始化时会调用200+次，因此需要极高的性能
            if (Utils.getHostVersionCode() >= QQVersion.QQ_8_5_0) {
                for (m in DexKit.doFindClass(DexKit.C_AvatarUtil).declaredMethods) {
                    val argt = m.parameterTypes
                    if ("adjustFaceShape" == m.name && argt[0] == Byte::class.javaPrimitiveType && m.returnType == Byte::class.javaPrimitiveType) {
                        XposedBridge.hookMethod(m, object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return
                                }
                                if (!isEnabled) {
                                    return
                                }
                                param.result = param.args[0]
                            }
                        })
                    }
                }
            } else {
                for (m in DexKit.doFindClass(DexKit.C_FaceManager).declaredMethods) {
                    val argt = m.parameterTypes
                    if (argt.isNotEmpty() && "a" == m.name && argt[0] == Byte::class.javaPrimitiveType && m.returnType == Byte::class.javaPrimitiveType) {
                        XposedBridge.hookMethod(m, object : XC_MethodHook() {
                            @Throws(Throwable::class)
                            override fun beforeHookedMethod(param: MethodHookParam) {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return
                                }
                                if (!isEnabled) {
                                    return
                                }
                                param.result = param.args[0]
                            }
                        })
                    }
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun getPreconditions(): Array<Step> {
        return if (Utils.getHostVersionCode() >= QQVersion.QQ_8_5_0) {
            arrayOf(DexDeobfStep(DexKit.C_AvatarUtil))
        } else {
            arrayOf(DexDeobfStep(DexKit.C_FaceManager))
        }
    }
}
