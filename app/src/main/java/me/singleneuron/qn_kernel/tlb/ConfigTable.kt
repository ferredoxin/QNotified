package me.singleneuron.qn_kernel.tlb

import nil.nadph.qnotified.util.Utils

object ConfigTable {

    private val presentConfigMap: Map<String?, Map<Long, Any>> by lazy {
        return@lazy if (Utils.isTim()) {
            TIMConfigTable.configs
        } else {
            QQConfigTable.configs
        }
    }
    private val presentRangeConfigMap: Map<String?, Map<Long, Any>> by lazy {
        return@lazy if (Utils.isTim()) {
            TIMConfigTable.rangingConfigs
        } else {
            QQConfigTable.rangingConfigs
        }
    }

    public val cacheMap: Map<String?, Any?> by lazy {
        val map: HashMap<String?, Any?> = HashMap()
        val versionCode = Utils.getHostVersionCode()
        for (pair in presentRangeConfigMap) {
            for (i in versionCode downTo 1) {
                if (pair.value.containsKey(i)) {
                    map[pair.key] = pair.value[i]
                    break
                }
            }
        }
        for (pair in presentConfigMap) {
            if (pair.value.containsKey(versionCode)) {
                map[pair.key] = pair.value[versionCode]
            }
        }
        map
    }

    fun <T> getConfig(className: String?): T {
        val config = cacheMap[className]
        return config as T
                ?: throw RuntimeException("$className :Unsupported Version: "+Utils.getHostVersionCode())
    }

}
