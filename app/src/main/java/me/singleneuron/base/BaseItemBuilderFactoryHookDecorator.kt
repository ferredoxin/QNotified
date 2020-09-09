package me.singleneuron.base

import de.robv.android.xposed.XC_MethodHook
import nil.nadph.qnotified.util.Utils

abstract class BaseItemBuilderFactoryHookDecorator(val cfg: String): BaseDelayableHookAdapter(cfg) {

    override fun init(): Boolean {
        return true
    }
    override fun doInit(): Boolean {
        return true
    }

    fun decorate(result:Int,chatMessage:Any,param:XC_MethodHook.MethodHookParam): Boolean {
        if (!checkEnabled()) return false
        return try {
            doDecorate(result,chatMessage,param)
        } catch (e:Exception) {
            Utils.log(e)
            false
        }
    }

    protected abstract fun doDecorate(result:Int,chatMessage:Any,param:XC_MethodHook.MethodHookParam): Boolean

}