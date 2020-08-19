package me.singleneuron.base

import de.robv.android.xposed.XposedBridge
import nil.nadph.qnotified.util.LicenseStatus

abstract class BaseDelayableConditionalHookAdapter(string:String) : BaseDelayableHookAdapter(string), ConditionalHook {

    override fun checkEnabled(): Boolean {
        return condition()&&super.checkEnabled()
    }
    
}