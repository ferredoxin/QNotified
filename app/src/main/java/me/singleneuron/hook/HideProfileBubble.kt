package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object HideProfileBubble : BaseDelayableHighPerformanceConditionalHookAdapter("hideProfileBubble") {

    override val recordTime: Boolean = false

    override fun doInit(): Boolean {
        return try {
            val clz = Initiator.load("com.tencent.mobileqq.activity.QQSettingMe")
            for (m: Method in clz.declaredMethods) {
                val argt = m.parameterTypes
                if (m.name == getMethod() && !Modifier.isStatic(m.modifiers) && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            param?.result = null
                        }
                    })
                }
            }
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { Utils.getHostVersionCode() >= QQVersion.QQ_8_3_6 }

    fun getMethod(): String {
        return when (Utils.getHostVersionCode()) {
            QQVersion.QQ_8_4_5 -> "V"
            QQVersion.QQ_8_4_8 -> "U"
            QQVersion.QQ_8_4_10 -> "Y"
            QQVersion.QQ_8_4_17 -> "Y"
            QQVersion.QQ_8_4_18 -> "Y"
            QQVersion.QQ_8_5_0 -> "Z"
            else -> throw RuntimeException("hideProfileBubble :Unsupported QQ Version")
        }
    }
}
