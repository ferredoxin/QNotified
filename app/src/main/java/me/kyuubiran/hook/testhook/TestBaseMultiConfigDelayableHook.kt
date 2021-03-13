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
package me.kyuubiran.hook.testhook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.hook.BaseMultiConfigDelayableHook
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//kotlin BaseMultiConfigDelayableHook模板
object TestBaseMultiConfigDelayableHook : BaseMultiConfigDelayableHook() {
    var isInit = false

    private const val cfg1 = "config1"
    private const val cfg2 = "config2"
    private const val cfg3 = "config3"

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInited) return true
        return try {
            for (m: Method in Initiator.load("").declaredMethods) {
                if (m.name == "a") {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            Utils.logd("这是一个BaseMultiConfigDelayableHook模板")
                            if (getBooleanConfig(cfg1)) Utils.logd("cfg1已启用") else Utils.logd("cfg1已禁用")
                            if (getBooleanConfig(cfg2)) Utils.logd("cfg2已启用") else Utils.logd("cfg2已禁用")
                            if (getBooleanConfig(cfg3)) Utils.logd("cfg3已启用") else Utils.logd("cfg3已禁用")
                        }
                    })
                }
            }
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isInited(): Boolean {
        return isInit
    }

    override fun isEnabled(): Boolean {
        return getBooleanConfig(cfg1) || getBooleanConfig(cfg2) || getBooleanConfig(cfg3)
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_MAIN
    }

    override fun setEnabled(enabled: Boolean) {
        //not supported
    }
}
