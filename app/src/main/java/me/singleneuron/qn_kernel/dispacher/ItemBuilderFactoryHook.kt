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
package me.singleneuron.qn_kernel.dispacher

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHookAdapter
import me.singleneuron.hook.decorator.CardMsgToText
import me.singleneuron.hook.decorator.MiniAppToStruckMsg
import me.singleneuron.hook.decorator.SimpleCheckIn
import me.singleneuron.hook.decorator.SimpleReceiptMessage
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import java.lang.reflect.Method

@FunctionEntry
object ItemBuilderFactoryHook : BaseDelayableHookAdapter(
    cfgName = "itemBuilderFactoryHook",
    cond = arrayOf(DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC))
) {

    //Register your decorator here
    val decorators = arrayOf(
        CardMsgToText,
        MiniAppToStruckMsg,
        SimpleCheckIn,
        SimpleReceiptMessage,
    )

    override fun doInit(): Boolean {
        var getMsgType: Method? = null
        for (m in DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC)!!.methods) {
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
                    if (decorator.decorate(result, chatMessage, param)) {
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
