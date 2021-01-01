package me.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHighPerformanceConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.qn_kernel.tlb.ConfigTable
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
                if (m.name == ConfigTable.getConfig(HideProfileBubble::class.simpleName) && !Modifier.isStatic(m.modifiers) && argt.isEmpty()) {
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

}
