package me.singleneuron.qn_kernel.tlb

import me.ketal.util.TIMVersion.*
import nil.nadph.qnotified.bridge.QQMessageFacade
import nil.nadph.qnotified.hook.MultiActionHook
import nil.nadph.qnotified.hook.ReplyNoAtHook
import nil.nadph.qnotified.util.Utils

object TIMConfigTable {

    private val configs: Map<String?, Map<Long, Any>> = mapOf(

    )

    private val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(

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

    )

    private val cacheMap: Map<String?, Any?> by lazy {
        val map: HashMap<String?, Any?> = HashMap()
        val versionCode = Utils.getHostVersionCode()
        for (pair in rangingConfigs) {
            for (i in versionCode downTo TIM_1_0_0) {
                if (pair.value.containsKey(i)) {
                    map[pair.key] = pair.value[i]
                    break
                }
            }
        }
        for (pair in configs) {
            if (pair.value.containsKey(versionCode)) {
                map[pair.key] = pair.value[versionCode]
            }
        }
        map
    }

    fun <T> getConfig(className: String?): T {
        val config = cacheMap[className]
        return config as T
                ?: throw RuntimeException("$className :Unsupported TIM Version")
    }

}
