/*
 * QNotified - An Xposed module for QQ/TIM
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

package cn.lliiooll.hook

import cn.lliiooll.msg.MessageManager
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.data.MsgRecordData
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Initiator._MessageRecord
import nil.nadph.qnotified.util.Utils


object MessageInterception : CommonDelayableHook("qn_message_interception") {

    override fun initOnce(): Boolean {
        return try {
            val clazz = Initiator._QQMessageFacade()
            for (m in clazz.declaredMethods) {
                val argt = m.parameterTypes
                if (argt.size == 1 && argt[0] == _MessageRecord()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            val msgRecord = param.args[0]
                            val msgRecordData = MsgRecordData(msgRecord)
                            MessageManager.call(msgRecordData)
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

    override fun isEnabled(): Boolean {
        return true
    }
}
