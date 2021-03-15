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
package me.singleneuron.base.adapter

import android.os.Looper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

abstract class BaseDelayableHookAdapter @JvmOverloads protected constructor(
    protected val cfgName: String,
    val proc: Int = SyncUtils.PROC_MAIN,
    val cond: Array<Step> = arrayOf(),
    val defVal: Boolean = false
) : BaseDelayableHook() {

    private var inited = false
    protected open val recordTime = false
    override fun getEffectiveProc(): Int {
        return proc
    }

    override fun init(): Boolean {
        if (!checkEnabled()) return false
        if (inited) return true
        inited = try {
            doInit()
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
        return inited
    }

    @Throws(Throwable::class)
    protected abstract fun doInit(): Boolean

    override fun isInited(): Boolean {
        return inited
    }

    override fun getPreconditions(): Array<Step> {
        return cond
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[cfgName] = enabled
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(hostInfo.application, e.toString() + "")
            } else {
                SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
            }
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrDefault(cfgName, defVal)
        } catch (e: Exception) {
            Utils.log(e)
            false
        }
    }

    open fun checkEnabled(): Boolean {
        if (LicenseStatus.sDisableCommonHooks) return false
        return isEnabled
    }

    abstract inner class XposedMethodHookAdapter : XC_MethodHook() {
        @Throws(Throwable::class)
        override fun beforeHookedMethod(param: MethodHookParam) {
            var startTime: Long = 0
            if (recordTime) {
                startTime = System.nanoTime()
            }
            if (!checkEnabled()) return
            try {
                beforeMethod(param)
            } catch (e: Exception) {
                Utils.log(e)
            }
            if (recordTime) {
                Utils.logd(cfgName + " costs time: " + (System.nanoTime() - startTime) + " ns")
            }
        }

        @Throws(Throwable::class)
        override fun afterHookedMethod(param: MethodHookParam) {
            var startTime: Long = 0
            if (recordTime) {
                startTime = System.nanoTime()
            }
            if (!checkEnabled()) return
            try {
                afterMethod(param)
            } catch (e: Exception) {
                Utils.log(e)
            }
            if (recordTime) {
                Utils.logd(cfgName + " costs time: " + (System.nanoTime() - startTime) + " ns")
            }
        }

        @Throws(Throwable::class)
        protected open fun beforeMethod(param: MethodHookParam?) {
        }

        @Throws(Throwable::class)
        protected open fun afterMethod(param: MethodHookParam?) {
        }
    }

    abstract inner class XposedMethodReplacementAdapter : XC_MethodReplacement() {
        @Throws(Throwable::class)
        override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any? {
            var startTime: Long = 0
            if (recordTime) {
                startTime = System.currentTimeMillis()
            }
            val returnObject: Any? = if (!checkEnabled()) {
                XposedBridge.invokeOriginalMethod(
                    methodHookParam.method,
                    methodHookParam.thisObject,
                    methodHookParam.args
                )
            } else {
                replaceMethod(methodHookParam)
            }
            if (recordTime) {
                Utils.logd(cfgName + " costs time: " + (System.currentTimeMillis() - startTime) + " ms")
            }
            return returnObject
        }

        @Throws(Throwable::class)
        protected abstract fun replaceMethod(param: MethodHookParam?): Any?
    }

}
