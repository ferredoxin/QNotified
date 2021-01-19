package me.singleneuron.qn_kernel.tlb

object ConfigTable {
    var isTim: Boolean = false

    fun <T> getConfig(className: String?) : T {
        if(isTim) {
            return TIMConfigTable.getConfig(className) as T
        }
        return QQConfigTable.getConfig(className) as T
    }

}