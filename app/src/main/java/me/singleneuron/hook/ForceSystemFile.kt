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
package me.singleneuron.hook

import android.content.Intent
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseFileAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.data.hostInformationProvider
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit

object ForceSystemFile : BaseDelayableConditionalHookAdapter("forceSystemFile") {

    override fun doInit(): Boolean {
        if (hostInformationProvider.versionCode >= QQVersion.QQ_8_4_8) {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.pluspanel.appinfo.FileAppInfo")
            //特征字符串:"SmartDeviceProxyMgr create"
            val sessionInfoClass = Class.forName("com.tencent.mobileqq.activity.aio.SessionInfo")
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(plusPanelClass, "a", Initiator._BaseChatPie(), sessionInfoClass, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    val context = hostInformationProvider.applicationContext
                    context.startActivity(Intent(context, ChooseFileAgentActivity::class.java))
                    param!!.result = null
                }
            })
        } else {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
            val smartDeviceProxyMgrClass = DexKit.doFindClass(DexKit.C_SmartDeviceProxyMgr)
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(plusPanelClass, "a", smartDeviceProxyMgrClass, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    val context = hostInformationProvider.applicationContext
                    context.startActivity(Intent(context, ChooseFileAgentActivity::class.java))
                    param!!.result = null
                }
            })
        }
        return true
    }

    override fun getPreconditions(): Array<Step> {
        return arrayOf(DexDeobfStep(DexKit.C_SmartDeviceProxyMgr))
    }

    override val condition: Boolean
        get() = true
}
