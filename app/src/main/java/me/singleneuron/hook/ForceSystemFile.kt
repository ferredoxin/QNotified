package me.singleneuron.hook

import android.content.Intent
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseFileAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object ForceSystemFile : BaseDelayableConditionalHookAdapter("forceSystemAlbum") {

    override fun doInit(): Boolean {
        if (Utils.getHostVersionCode() >= QQVersion.QQ_8_4_8) {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.pluspanel.appinfo.FileAppInfo")
            //特征字符串:"SmartDeviceProxyMgr create"
            val smartDeviceProxyMgrClass = Class.forName("com.tencent.mobileqq.activity.aio.core.BaseChatPie")
            val smartDeviceProxyMgrClass2 = Class.forName("com.tencent.mobileqq.activity.aio.SessionInfo")
            //特征字符串:"0X800407C"、"send_file"
            XposedHelpers.findAndHookMethod(plusPanelClass, "a", smartDeviceProxyMgrClass, smartDeviceProxyMgrClass2, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    val context = Utils.getApplication()
                    context.startActivity(Intent(context, ChooseFileAgentActivity::class.java))
                    param!!.result = null
                }
            })
        } else {
            val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
            val smartDeviceProxyMgrClass = Class.forName(ConfigTable.getConfig(ForceSystemFile::class.simpleName))
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

}