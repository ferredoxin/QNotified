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
package me.kyuubiran.util

import nil.nadph.qnotified.util.Utils

const val LOG_TYPE_FIND_CLASS = 1
const val LOG_TYPE_FIND_METHOD = 2
const val LOG_TYPE_START_HOOK = 3
const val LOG_TYPE_FINISHED_HOOK = 4

fun logd(msg: String) {
    Utils.logd("好耶 $msg")
}

fun logd(logType: Int, msg: String = "") {
    when (logType) {
        LOG_TYPE_FIND_CLASS -> logd("找到类了 $msg")
        LOG_TYPE_FIND_METHOD -> logd("找到方法了 $msg")
        LOG_TYPE_START_HOOK -> logd("开始Hook了 $msg")
        LOG_TYPE_FINISHED_HOOK -> logd("搞完事情了 $msg")
    }
}

fun logdt(t: Throwable) {
    logd("搞出大事情了 \n$t")
}
