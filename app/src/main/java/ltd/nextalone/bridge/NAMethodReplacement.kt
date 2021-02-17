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

package ltd.nextalone.bridge

import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

abstract class NAMethodReplacement(baseHook: BaseDelayableHook) : NAMethodHook(baseHook) {
    override val hook = baseHook

    @Throws(Throwable::class)
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            if (!hook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            param.result = replaceMethod(param)
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    @Throws(Throwable::class)
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    @Throws(Throwable::class)
    override fun beforeMethod(param: MethodHookParam?) {
    }

    @Throws(Throwable::class)
    override fun afterMethod(param: MethodHookParam?) {
    }

    @Throws(Throwable::class)
    abstract fun replaceMethod(param: MethodHookParam?): Any?
}
