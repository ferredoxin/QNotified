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
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.ui.base.UiDescription
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.DexMethodDescriptor
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

//自己的消息居左显示
@FunctionEntry
@UiItem
object ShowSelfMsgByLeft : CommonDelayAbleHookBridge("kr_show_self_msg_by_left") {

    override fun initOnce(): Boolean {
        return try {
            val m =
                DexMethodDescriptor("Lcom/tencent/mobileqq/activity/aio/BaseChatItemLayout;->setHearIconPosition(I)V")
                    .getMethodInstance(Initiator.getHostClassLoader())
            XposedBridge.hookMethod(m, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    param?.result = null
                }
            })
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override val preference: UiDescription = uiSwitchPreference {
        title = "自己的消息和头像居左显示"
    }
    override val preferenceLocate: Array<String> = arrayOf("其他功能", "娱乐功能")

}
