package me.singleneuron.hook

import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.BaseDelayableHookAdapter
import me.singleneuron.util.NoAppletUtil
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

object NoApplet : BaseDelayableHookAdapter("noapplet") {

    override fun doInit(): Boolean {
        super.init()
        try {
            val jumpActivityClass = Class.forName("com.tencent.mobileqq.activity.JumpActivity")
            XposedBridge.hookAllMethods(jumpActivityClass, "getIntent", object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    val originIntent = param!!.result as Intent
                    val originUri = originIntent.data
                    val schemeUri = originUri.toString()
                    if (!schemeUri.contains("mini_program")) return
                    val processScheme = NoAppletUtil.removeMiniProgramNode(schemeUri)
                    val newScheme = NoAppletUtil.replace(processScheme, "req_type", "MQ==")
                    val newUri = Uri.parse(newScheme)
                    originIntent.data = newUri
                    originIntent.component = null
                    param.result = originIntent
                }
            })
        } catch (e: Exception) {
            Utils.log(e)
            return false
        }
        return true
    }

}