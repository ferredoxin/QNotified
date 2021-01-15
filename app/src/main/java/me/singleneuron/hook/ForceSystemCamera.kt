package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.loadClass
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

object ForceSystemCamera : BaseDelayableConditionalHookAdapter("forceSystemCamera") {
    override fun doInit(): Boolean {
        val className = ConfigTable.getConfig<String>(ForceSystemCamera::class.simpleName)
        if (Utils.getHostVersionCode() >= QQVersion.QQ_8_5_0) {
            XposedHelpers.findAndHookMethod(loadClass(className), "a", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    param!!.result = false
                }
            })
        } else {
            val captureUtilClass = Class.forName(className)
            //特征字符串："GT-I9500"
            XposedHelpers.findAndHookMethod(captureUtilClass, "a", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    param!!.result = false
                }
            })
        }
        return true
    }

}
