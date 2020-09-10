package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHookAdapter
import me.singleneuron.hook.decorator.SimpleCheckIn
import me.singleneuron.hook.decorator.SimpleReceiptMessage
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import java.lang.reflect.Method

object ItemBuilderFactoryHook : BaseDelayableHookAdapter(cfgName = "itemBuilderFactoryHook",cond = arrayOf(DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC))) {

    val decorators = arrayOf(
            SimpleCheckIn,
            SimpleReceiptMessage
    )

    override fun doInit(): Boolean {
            var getMsgType: Method? = null
            for (m in DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC).methods) {
                if (m.returnType == Int::class.javaPrimitiveType) {
                    val argt = m.parameterTypes
                    if (argt.isNotEmpty() && argt[argt.size - 1] == Initiator.load("com.tencent.mobileqq.data.ChatMessage")) {
                        getMsgType = m
                        break
                    }
                }
            }
            XposedBridge.hookMethod(getMsgType, object : XC_MethodHook(39) {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val result = param.result as Int
                    val chatMessage = param.args[param.args.size - 1]
                    for (decorator in decorators) {
                        if (decorator.decorate(result,chatMessage,param)) {
                            return
                        }
                    }
                }
            })
            return true

    }

    override fun setEnabled(enabled: Boolean) {}
    override fun isEnabled(): Boolean {
        return true
    }

}