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
package me.nextalone.util

import nil.nadph.qnotified.util.Utils
import java.util.*

const val LOG_TYPE_FIND_CLASS = "c"
const val LOG_TYPE_FIND_METHOD = "m"
const val LOG_TYPE_START_HOOK = "s"
const val LOG_TYPE_BEFORE_HOOK = "b"
const val LOG_TYPE_AFTER_HOOK = "a"
fun logd(msg: String) {
    Utils.logd("NA: $msg")
}

fun logd(logType: String, msg: String = "") {
    when (logType.toLowerCase(Locale.ROOT)) {
        LOG_TYPE_FIND_CLASS -> logd("Class-$msg")
        LOG_TYPE_FIND_METHOD -> logd("Method-$msg")
        LOG_TYPE_START_HOOK -> logd("Start-$msg")
        LOG_TYPE_BEFORE_HOOK -> logd("Before-$msg")
        LOG_TYPE_AFTER_HOOK -> logd("After-$msg")
    }
}

fun logdt(t: Throwable) {
    logd("Throwable: \n$t")
}
