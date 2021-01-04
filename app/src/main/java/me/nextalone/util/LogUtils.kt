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
