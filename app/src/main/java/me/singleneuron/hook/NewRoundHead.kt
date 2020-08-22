package me.singleneuron.hook

import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object NewRoundHead : BaseDelayableConditionalHookAdapter("newroundhead") {
    override fun doInit(): Boolean {
        //特征字符串："FaceManager"
        val faceManagerClass = Class.forName(getClass())
        //参数和值都是byte类型
        XposedHelpers.findAndHookMethod(faceManagerClass, "a", Byte::class.javaPrimitiveType, object : XposedMethodHookAdapter(){
            override fun beforeMethod(param: MethodHookParam?) {
                //Utils.logd("NewRoundHead Started");
                param!!.result = param.args[0] as Byte
            }
        })
        return true
    }

    override val condition: () -> Boolean
        get() = {Utils.getHostVersionCode()==QQVersion.QQ_8_3_6 || Utils.getHostVersionCode()==QQVersion.QQ_8_3_9 || Utils.getHostVersionCode()==QQVersion.QQ_8_4_1 || Utils.getHostVersionCode()==QQVersion.QQ_8_4_5}

    override fun getClass():String {
        return when(Utils.getHostVersionCode()) {
            QQVersion.QQ_8_3_6 -> "beft"
            QQVersion.QQ_8_3_9 -> "bfsw"
            QQVersion.QQ_8_4_1 -> "aocs"
            QQVersion.QQ_8_4_5 -> "aope"
            else -> super.getClass()
        }
    }

}