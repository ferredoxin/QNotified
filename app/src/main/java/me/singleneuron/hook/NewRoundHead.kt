package me.singleneuron.hook

import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object NewRoundHead : BaseDelayableHighPerformanceConditionalHookAdapter("newroundhead") {

    override val recordTime: Boolean = false

    override fun doInit(): Boolean {
        //特征字符串："FaceManager"
        val faceManagerClass = Class.forName(getClass())
        //参数和值都是byte类型
        //这个方法在QQ主界面初始化时会调用200+次，因此需要极高的性能
        XposedHelpers.findAndHookMethod(faceManagerClass, "a", Byte::class.javaPrimitiveType, object : XposedMethodHookAdapter(){
            override fun beforeMethod(param: MethodHookParam?) {
                //Utils.logd("NewRoundHead Started");
                param!!.result = param.args[0] as Byte
            }
        })
        return true
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache {Utils.getHostVersionCode()>=QQVersion.QQ_8_3_6}

    override fun getClass():String {
        return when(Utils.getHostVersionCode()) {
            QQVersion.QQ_8_3_6 -> "beft"
            QQVersion.QQ_8_3_9 -> "bfsw"
            QQVersion.QQ_8_4_1 -> "aocs"
            QQVersion.QQ_8_4_5 -> "aope"
            QQVersion.QQ_8_4_8 -> "anho"
            else -> super.getClass()
        }
    }

}
