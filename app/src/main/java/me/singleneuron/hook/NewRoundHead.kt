package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.base.BaseDelayableHookAdapter
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils
import java.lang.Exception

object NewRoundHead : BaseDelayableConditionalHookAdapter("newroundhead") {
    override fun doInit(): Boolean {
        val roundHeadClass = Class.forName("bfsw")
        XposedHelpers.findAndHookMethod(roundHeadClass, "a", Byte::class.javaPrimitiveType, object : XposedMethodHookAdapter(){
            override fun beforeMethod(param: MethodHookParam?) {
                //Utils.logd("NewRoundHead Started");
                param!!.result = param.args[0] as Byte
            }
        })
        return true
    }

    override val condition: () -> Boolean
        get() = {Utils.getHostVersionCode()==QQVersion.QQ_8_3_9}
}