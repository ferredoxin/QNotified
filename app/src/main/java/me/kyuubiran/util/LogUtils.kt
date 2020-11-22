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