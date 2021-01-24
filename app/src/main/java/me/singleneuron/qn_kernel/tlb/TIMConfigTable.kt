package me.singleneuron.qn_kernel.tlb

import me.ketal.hook.LeftSwipeReplyHook
import me.ketal.util.TIMVersion.*
import nil.nadph.qnotified.bridge.QQMessageFacade
import nil.nadph.qnotified.hook.MultiActionHook
import nil.nadph.qnotified.hook.ReplyNoAtHook

object TIMConfigTable {

    val configs: Map<String?, Map<Long, Any>> = mapOf(

    )

    val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(

            MultiActionHook::class.java.simpleName to mapOf(
                    TIM_1_0_0 to "a",
                    TIM_3_0_0 to "kqr",
                    TIM_3_0_0_1 to "kqy",
                    TIM_3_1_1 to "hd",
            ),

            //key:public \S* \(boolean
            QQMessageFacade::class.java.simpleName to mapOf(
                    TIM_1_0_0 to "b",
                    TIM_3_0_0 to "wa",
                    TIM_3_1_1 to "PK",
                    TIM_3_3_0 to "PO",
            ),

            ReplyNoAtHook::class.java.simpleName to mapOf(
                    TIM_3_1_1 to "wg",
                    TIM_3_3_0 to "wk",
            ),

            LeftSwipeReplyHook::class.java.simpleName to mapOf(
                    TIM_3_1_1 to "Cg",
                    TIM_3_3_0 to "Cn"
            ),
    )

}
