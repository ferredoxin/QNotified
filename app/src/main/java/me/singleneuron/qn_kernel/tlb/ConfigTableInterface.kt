package me.singleneuron.qn_kernel.tlb

interface ConfigTableInterface {
    val configs: Map<String?, Map<Long, Any>>
    val rangingConfigs: Map<String?, Map<Long, Any>>
}
