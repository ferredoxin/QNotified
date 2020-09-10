package me.singleneuron.base.decorator

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import nil.nadph.qnotified.util.Utils

abstract class BaseStartActivityHookDecorator(cfg: String): BaseDecorator(cfg) {

    fun decorate(intent: Intent ,param: XC_MethodHook.MethodHookParam): Boolean {
        if (!checkEnabled()) return false
        return try {
            doDecorate(intent,param)
        } catch (e:Exception) {
            Utils.log(e)
            false
        }
    }

    protected abstract fun doDecorate(intent: Intent,param: XC_MethodHook.MethodHookParam): Boolean

}