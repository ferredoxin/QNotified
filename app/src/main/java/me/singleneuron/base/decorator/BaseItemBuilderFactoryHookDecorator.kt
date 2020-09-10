package me.singleneuron.base.decorator

import de.robv.android.xposed.XC_MethodHook
import nil.nadph.qnotified.util.Utils

abstract class BaseItemBuilderFactoryHookDecorator(cfg: String): BaseDecorator(cfg) {

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