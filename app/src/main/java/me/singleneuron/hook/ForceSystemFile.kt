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

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseFileAgentActivity
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
import nil.nadph.qnotified.util.Initiator

@FunctionEntry
@UiItem
object ForceSystemFile : CommonDelayAbleHookBridge("forceSystemFile") {

    override fun getPreconditions(): Array<Step> {
        return arrayOf(DexDeobfStep(DexKit.C_SmartDeviceProxyMgr))
    }

    override fun initOnce(): Boolean {
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val context = hostInfo.application
                val intent = Intent(context, ChooseFileAgentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                param!!.result = null
            }
        }
        if (requireMinQQVersion(QQVersion.QQ_8_4_8)) {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.pluspanel.appinfo.FileAppInfo")
            //特征字符串:"SmartDeviceProxyMgr create"
            val sessionInfoClass = Class.forName("com.tencent.mobileqq.activity.aio.SessionInfo")
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(
                plusPanelClass,
                "a",
                Initiator._BaseChatPie(),
                sessionInfoClass,
                hook)
        } else {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
            val smartDeviceProxyMgrClass = DexKit.doFindClass(DexKit.C_SmartDeviceProxyMgr)
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(
                plusPanelClass,
                "a",
                smartDeviceProxyMgrClass,
                hook)
        }
        return true
    }

    override val preference: UiDescription = uiSwitchPreference {
        title = "强制使用系统文件"
        summary = "支持8.3.6及更高"
    }

    override val preferenceLocate: Array<String> = arrayOf("增强功能")

}
