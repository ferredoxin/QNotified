package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.loadClass
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

object ForceSystemCamera : BaseDelayableConditionalHookAdapter("forceSystemCamera") {
    override fun doInit(): Boolean {
        if (Utils.getHostVersionCode() == QQVersion.QQ_8_5_0) {
            XposedHelpers.findAndHookMethod(loadClass(getClass()), "a", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    Utils.logd("ForceSystemCamera babq.a():" + (param!!.result as Boolean))
                    param.result = false
                }
            })
        } else {
            //特征字符串："CaptureUtil"
            val captureUtilClass = Class.forName(getClass())
            //特征字符串："GT-I9500"
            XposedHelpers.findAndHookMethod(captureUtilClass, "a", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    Utils.logd("ForceSystemCamera babq.a():" + (param!!.result as Boolean))
                    param.result = false
                }
            })
        }
        return true
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache {Utils.getHostVersionCode()>=QQVersion.QQ_8_3_6}

    override fun getClass(): String {
        return when (Utils.getHostVersionCode()) {
            QQVersion.QQ_8_3_6 -> "aypd"
            QQVersion.QQ_8_3_9 -> "babg"
            QQVersion.QQ_8_4_1 -> "bann"
            QQVersion.QQ_8_4_5 -> "bbgg"
            QQVersion.QQ_8_4_8 -> "babd"
            QQVersion.QQ_8_4_10 -> "bbhm"
            QQVersion.QQ_8_4_17 -> "bcmd"
            QQVersion.QQ_8_4_18 -> "bcmd"
            QQVersion.QQ_8_5_0 -> "com/tencent/mobileqq/richmedia/capture/util/CaptureUtil"
            else -> super.getClass()
        }
    }
}
