package me.singleneuron.hook

import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.loadClass
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object NewRoundHead : BaseDelayableHighPerformanceConditionalHookAdapter("newroundhead") {

    override val recordTime: Boolean = false

    override fun doInit(): Boolean {

        val className = ConfigTable.getConfig<String>(NewRoundHead::class.simpleName)
        val faceManagerClass = Class.forName(className)
        //参数和值都是byte类型
        //这个方法在QQ主界面初始化时会调用200+次，因此需要极高的性能
        var method = "a"
        if (Utils.getHostVersionCode() >= QQVersion.QQ_8_5_0) {
            method = "adjustFaceShape"
            XposedHelpers.findAndHookMethod(loadClass(className), method, Byte::class.javaPrimitiveType, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    //Utils.logd("NewRoundHead Started");
                    param!!.result = param.args[0] as Byte
                }
            })
            return true
        } else {
            XposedHelpers.findAndHookMethod(faceManagerClass, method, Byte::class.javaPrimitiveType, object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    //Utils.logd("NewRoundHead Started");
                    param!!.result = param.args[0] as Byte
                }
            })
            return true
        }
    }

}
