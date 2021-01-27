package me.singleneuron.hook

import android.content.Intent
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseFileAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Utils

object ForceSystemFile : BaseDelayableConditionalHookAdapter("forceSystemFile") {

    override fun doInit(): Boolean {
        if (Utils.getHostVersionCode() >= QQVersion.QQ_8_4_8) {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.pluspanel.appinfo.FileAppInfo")
            //特征字符串:"SmartDeviceProxyMgr create"
            val sessionInfoClass = Class.forName("com.tencent.mobileqq.activity.aio.SessionInfo")
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(plusPanelClass, "a", Initiator._BaseChatPie(), sessionInfoClass, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    val context = Utils.getApplication()
                    context.startActivity(Intent(context, ChooseFileAgentActivity::class.java))
                    param!!.result = null
                }
            })
        } else {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
            val smartDeviceProxyMgrClass = DexKit.doFindClass(DexKit.C_SmartDeviceProxyMgr)
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(plusPanelClass, "a", smartDeviceProxyMgrClass, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    val context = Utils.getApplication()
                    context.startActivity(Intent(context, ChooseFileAgentActivity::class.java))
                    param!!.result = null
                }
            })
        }
        return true
    }

    override fun getPreconditions(): Array<Step> {
        return arrayOf(DexDeobfStep(DexKit.C_SmartDeviceProxyMgr))
    }
}
