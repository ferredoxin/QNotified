package me.singleneuron.base.bridge

import nil.nadph.qnotified.util.NonNull

abstract class CardMsgList {

    abstract val blackList: Map<String,Array<String>>

    companion object {

        @JvmStatic
        @NonNull
        fun getInstance(): CardMsgList {
            //Todo
            return CardMsgListExample()
        }

    }

    class CardMsgListExample: CardMsgList() {
        override val blackList: Map<String, Array<String>> = mapOf(
                "禁止引流" to arrayOf("jq.qq.com"),
                "禁止引流" to arrayOf("mqqapi","forward"),
                "禁止发送回执" to arrayOf("viewReceiptMessage")
        )
    }

}