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

internal fun logd(msg: String) {
    Utils.logd("NA: $msg")
}

internal fun logThrowable(msg: Throwable) {
    logd("Throwable: $msg")
}

internal fun logDetail(info: String, msg: String = "") {
    logd("$info--$msg")
}

internal fun logClass(msg: String = "") {
    logd("Class--$msg")
}

internal fun logMethod(msg: String = "") {
    logd("Method--$msg")
}

internal fun logStart(msg: String = "") {
    logd("Start--$msg")
}

internal fun logBefore(msg: String = "") {
    logd("Before--$msg")
}

internal fun logAfter(msg: String = "") {
    logd("After--$msg")
}
