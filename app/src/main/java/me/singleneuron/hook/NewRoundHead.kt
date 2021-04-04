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
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.ui.base.UiDescription
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

@FunctionEntry
@UiItem
object NewRoundHead : CommonDelayAbleHookBridge("newroundhead") {

    override fun getPreconditions(): Array<Step> {
        //特征字符串："FaceManager"/"AvatarUtil"
        return if (requireMinQQVersion(QQVersion.QQ_8_5_0)) {
            arrayOf(DexDeobfStep(DexKit.C_AvatarUtil))
        } else {
            arrayOf(DexDeobfStep(DexKit.C_FaceManager))
        }
    }

    override fun initOnce(): Boolean {
        return try {
            var method = "a"
            if (hostInfo.versionCode == QQVersion.QQ_8_5_0) {
                method = "adjustFaceShape"
            }
            //参数和值都是byte类型
            //这个方法在QQ主界面初始化时会调用200+次，因此需要极高的性能
            if (requireMinQQVersion(QQVersion.QQ_8_5_0)) {
                for (m in DexKit.doFindClass(DexKit.C_AvatarUtil)!!.declaredMethods) {
                    val argt = m.parameterTypes
                    if (argt.isNotEmpty() && method == m.name && argt[0] == Byte::class.javaPrimitiveType && m.returnType == Byte::class.javaPrimitiveType) {
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
                for (m in DexKit.doFindClass(DexKit.C_FaceManager)!!.declaredMethods) {
                    val argt = m.parameterTypes
                    if (argt.isNotEmpty() && method == m.name && argt[0] == Byte::class.javaPrimitiveType && m.returnType == Byte::class.javaPrimitiveType) {
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

    override val preference: UiDescription = uiSwitchPreference {
        title = "新版简洁模式圆头像"
        summary = "From 花Q，支持8.3.6及更高，重启后生效"
    }

    override val preferenceLocate: Array<String> = arrayOf("辅助功能")

}
