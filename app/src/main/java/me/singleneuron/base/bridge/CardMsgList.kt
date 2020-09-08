package me.singleneuron.base.bridge

import com.google.gson.Gson
import nil.nadph.qnotified.util.NonNull

abstract class CardMsgList {

    companion object {

        @JvmStatic
        @NonNull
        fun getInstance(): ()->String {
            //Todo
            return ::getBlackListExample
        }

    }
}

fun getBlackListExample(): String {
    val map = mapOf(
            "禁止引流" to arrayOf("jq.qq.com"),
            "禁止引流" to arrayOf("mqqapi","forward"),
            "禁止发送回执消息" to arrayOf("viewReceiptMessage"),
            "禁止干扰性卡片" to arrayOf("com.tencent.mobileqq.reading")
    )
    return Gson().toJson(map)
}