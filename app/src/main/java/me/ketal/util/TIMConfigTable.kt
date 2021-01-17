package me.ketal.util

import nil.nadph.qnotified.hook.MultiActionHook
import nil.nadph.qnotified.util.Utils
import me.ketal.util.TIMVersion.*;
import nil.nadph.qnotified.bridge.QQMessageFacade
import nil.nadph.qnotified.hook.ReplyNoAtHook

object TIMConfigTable {

    private val configs: Map<String?, Map<Long, Any>> = mapOf(

    )

    private val rangingConfigs: Map<String?, Map<Long, Any>> = mapOf(

            //com.tencent.mobileqq.activity.aio.helper.AIOMultiActionHelper|com.tencent.mobileqq.multimsg.MultiMsgManager
            MultiActionHook::class.java.simpleName to mapOf(
                    TIM_3_3_0 to "wxf|ajqo",
                    TIM_3_2_3 to "admr|atnd",
                    TIM_3_2_0 to "admr|atnb",
                    TIM_3_1_1 to "adms|atnc"
            ),

            //key:public \S* \(boolean
            QQMessageFacade::class.java.simpleName to mapOf(
                    TIM_3_3_0 to "PO",
                    TIM_3_1_1 to "PK",
                    TIM_3_0_0 to "wa",
                    TIM_1_0_0 to "b"
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
