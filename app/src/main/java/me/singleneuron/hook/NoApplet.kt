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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.data.hostInformationProvider
import me.singleneuron.util.NoAppletUtil
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object NoApplet : BaseDelayableConditionalHookAdapter("noapplet") {

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { hostInformationProvider.versionCode>=QQVersion.QQ_8_0_0}

    override fun doInit(): Boolean {
        try {

            //val jumpActivityClass = Class.forName("com.tencent.mobileqq.activity.JumpActivity")
            Utils.logd("NoApplet inited")
            XposedBridge.hookAllMethods(Activity::class.java, "getIntent", object : XposedMethodHookAdapter() {
                override fun afterMethod(param: MethodHookParam?) {
                    if (param!!.thisObject::class.java.simpleName != "JumpActivity") return
                    //Utils.logd("NoApplet started: "+param.thisObject::class.java.simpleName)
                    val originIntent = param.result as Intent
                    /*Utils.logd("NoApplet getIntent: $originIntent")
                    Utils.logd("NoApplet getExtra: ${originIntent.extras}")*/
                    val originUri = originIntent.data
                    val schemeUri = originUri.toString()
                    if (!schemeUri.contains("mini_program")) return
                    Utils.logd("transfer applet intent: $schemeUri")
                    val processScheme = NoAppletUtil.removeMiniProgramNode(schemeUri)
                    val newScheme = NoAppletUtil.replace(processScheme, "req_type", "MQ==")
                    val newUri = Uri.parse(newScheme)
                    originIntent.data = newUri
                    originIntent.component = null
                    param.result = originIntent
                }
            })
        } catch (e: Exception) {
            Utils.log(e)
            return false
        }
        return true
    }

}
