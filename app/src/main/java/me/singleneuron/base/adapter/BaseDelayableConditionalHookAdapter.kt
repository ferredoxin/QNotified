package me.singleneuron.base.adapter

import me.singleneuron.base.Conditional
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.SyncUtils

abstract class BaseDelayableConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableHookAdapter(string, proc), Conditional {

    //如有更改重启后生效
    protected open val conditionCache : PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache {
        try {
            ConfigTable.getConfig<Any?>(this::class.simpleName) != null
        } catch (e:Exception) {
            false
        }
    }

    override val condition : Boolean
    get() {
        return conditionCache.getValue()
    }

    override fun checkEnabled(): Boolean {
        return condition&&super.checkEnabled()
    }

}