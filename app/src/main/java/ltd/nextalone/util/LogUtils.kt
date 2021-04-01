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
package ltd.nextalone.util

import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

internal fun logd(vararg msg: Any?) {
    Utils.logd("NA: ${msg.joinToString(", ")}")
}

internal fun logThrowable(msg: Throwable) {
    logd("Throwable: ${msg.stackTraceToString()}")
}

internal fun <T : BaseDelayableHook> T.logDetail(info: String?, vararg msg: Any?) {
    logd("${this.javaClass.simpleName}: $info, ${msg.joinToString(", ")}")
}

internal fun <T : BaseDelayableHook> T.logClass(msg: String? = "") {
    logd("$this: Class, $msg")
}

internal fun <T : BaseDelayableHook> T.logMethod(method: Method?) {
    logDetail("$this: Method", "name", method?.name, "return", method?.returnType, "param", *method?.parameterTypes
        ?: arrayOf("null"))
}

internal fun <T : BaseDelayableHook> T.logStart() {
    logd("$this: Start")
}

internal fun <T : BaseDelayableHook> T.logBefore(msg: String? = "") {
    logd("$this: Before, $msg")
}

internal fun <T : BaseDelayableHook> T.logAfter(msg: String? = "") {
    logd("$this: After, $msg")
}
