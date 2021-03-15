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
package me.kyuubiran.hook

import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.*
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus

//屏蔽戳一戳灰字提示
object RemovePokeGrayTips : CommonDelayableHook("kr_remove_poke_tips") {
    val keys = listOf("拍了拍", "戳了戳", "亲了亲", "抱了抱", "揉了揉", "喷了喷", "踢了踢", "舔了舔", "捏了捏", "摸了摸")

    override fun initOnce(): Boolean {
        return try {
            val Msg = loadClass("com.tencent.imcore.message.QQMessageFacade\$Message")
            val MsgRecord = loadClass("com.tencent.mobileqq.data.MessageRecord")
            for (m in getMethods("com.tencent.imcore.message.QQMessageFacade")) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] == Msg::class.java) {
                    logd(LOG_TYPE_FIND_METHOD, "m -> $m")
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val msg =
                                getObjectOrNull(param.args[0], "msg", String::class.java) as String
                            logd("msg -> $msg")
                        }
                    })
                }
            }
            true
        } catch (t: Throwable) {
            logdt(t)
            false
        }
    }
}
