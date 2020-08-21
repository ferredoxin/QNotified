package me.singleneuron.base

import de.robv.android.xposed.XposedBridge
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.util.LicenseStatus

abstract class BaseDelayableConditionalHookAdapter @JvmOverloads constructor(string:String, proc:Int = SyncUtils.PROC_MAIN) : BaseDelayableHookAdapter(string, proc), ConditionalHook {

    override fun checkEnabled(): Boolean {
        return condition()&&super.checkEnabled()
    }
    
}