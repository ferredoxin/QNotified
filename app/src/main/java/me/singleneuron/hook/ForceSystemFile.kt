package me.singleneuron.hook

import android.content.Intent
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.activity.ChooseFileAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object ForceSystemFile : BaseDelayableConditionalHookAdapter("forceSystemAlbum") {

    override fun doInit(): Boolean {
        val plusPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.PlusPanel")
        //特征字符串:"SmartDeviceProxyMgr create"
        val smartDeviceProxyMgrClass = Class.forName(getClass())
        //特征字符串:"0X800407C"、"send_file"
        XposedHelpers.findAndHookMethod(plusPanelClass,"a",smartDeviceProxyMgrClass,object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val context = Utils.getApplication()
                context.startActivity(Intent(context,ChooseFileAgentActivity::class.java))
                param!!.result = null
            }
        })
        return true
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache {Utils.getHostVersionCode()>=QQVersion.QQ_8_3_6}

    override fun getClass(): String {
        return when(Utils.getHostVersionCode()) {
            QQVersion.QQ_8_3_6 -> "zyr"
            QQVersion.QQ_8_3_9 -> "aaxe"
            QQVersion.QQ_8_4_1 -> "abqn"
            QQVersion.QQ_8_4_5 -> "abur"
            else -> super.getClass()
        }
    }

}