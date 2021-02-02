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
package me.singleneuron.qn_kernel.dispacher

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHookAdapter
import me.singleneuron.hook.decorator.SimpleCheckIn
import me.singleneuron.hook.decorator.SimpleReceiptMessage
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import java.lang.reflect.Method

object ItemBuilderFactoryHook : BaseDelayableHookAdapter(cfgName = "itemBuilderFactoryHook",cond = arrayOf(DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC))) {

    val decorators = arrayOf(
            SimpleCheckIn,
            SimpleReceiptMessage
    )

    override fun doInit(): Boolean {
            var getMsgType: Method? = null
            for (m in DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC).methods) {
                if (m.returnType == Int::class.javaPrimitiveType) {
                    val argt = m.parameterTypes
                    if (argt.isNotEmpty() && argt[argt.size - 1] == Initiator.load("com.tencent.mobileqq.data.ChatMessage")) {
                        getMsgType = m
                        break
                    }
                }
            }
            XposedBridge.hookMethod(getMsgType, object : XC_MethodHook(39) {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val result = param.result as Int
                    val chatMessage = param.args[param.args.size - 1]
                    for (decorator in decorators) {
                        if (decorator.decorate(result,chatMessage,param)) {
                            return
                        }
                    }
                }
            })
            return true

    }

    override fun setEnabled(enabled: Boolean) {}
    override fun isEnabled(): Boolean {
        return true
    }

}
