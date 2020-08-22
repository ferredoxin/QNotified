package me.singleneuron.base

import nil.nadph.qnotified.SyncUtils

abstract class BaseDelayableConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableHookAdapter(string, proc), ConditionalHook {

    override fun checkEnabled(): Boolean {
        return condition()&&super.checkEnabled()
    }

    protected open fun getClass():String {
        throw RuntimeException("$cfgName :Unsupported QQ Version")
    }

}