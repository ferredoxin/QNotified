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

import android.widget.LinearLayout
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getObjectOrNull
import me.kyuubiran.util.loadClass
import me.kyuubiran.util.setViewZeroSize
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

//移除侧滑栏左上角打卡
@FunctionEntry
object RemoveDailySign : CommonDelayableHook("kr_remove_daily_sign") {

    override fun initOnce(): Boolean {
        return try {
            XposedBridge.hookAllConstructors(
                loadClass("com.tencent.mobileqq.activity.QQSettingMe"),
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        if (LicenseStatus.sDisableCommonHooks) return
                        if (!isEnabled) return
                        try {
                            val dailySignName = if (hostInfo.packageName == Utils.PACKAGE_NAME_QQ
                                && hostInfo.versionCode == QQVersion.QQ_8_6_0) "b" else "a"
                            val dailySignLayout = getObjectOrNull(
                                param.thisObject,
                                dailySignName,
                                LinearLayout::class.java
                            ) as LinearLayout
                            dailySignLayout.setViewZeroSize()
                        } catch (t: Throwable) {
                            Utils.log(t)
                        }
                    }
                })
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }
}
