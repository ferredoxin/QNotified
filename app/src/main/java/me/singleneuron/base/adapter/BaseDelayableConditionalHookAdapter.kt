package me.singleneuron.base.adapter

import me.singleneuron.base.Conditional
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import nil.nadph.qnotified.SyncUtils

abstract class BaseDelayableConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableHookAdapter(string, proc), Conditional {

    //如有更改重启后生效
    protected abstract val conditionCache : PageFaultHighPerformanceFunctionCache<Boolean>

    override val condition : Boolean
    get() {
        return conditionCache.getValue()
    }

    override fun checkEnabled(): Boolean {
        return condition&&super.checkEnabled()
    }

    internal open fun getClass(): String {
        throw RuntimeException("$cfgName :Unsupported QQ Version")
    }

    internal open fun getID(): Int {
        throw RuntimeException("$cfgName :Unsupported QQ Version")
    }

}