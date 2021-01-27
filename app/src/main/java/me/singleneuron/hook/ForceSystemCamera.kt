package me.singleneuron.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

object ForceSystemCamera : BaseDelayableConditionalHookAdapter("forceSystemCamera") {
    override fun doInit(): Boolean {
        return try {
            for (m in DexKit.doFindClass(DexKit.C_CaptureUtil).declaredMethods) {
                val argt = m.parameterTypes
                if ("a" == m.name && m.returnType == Boolean::class.javaPrimitiveType && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        @Throws(Throwable::class)
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) {
                                return
                            }
                            if (!isEnabled) {
                                return
                            }
                            param.result = false
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

    override fun getPreconditions(): Array<Step> {
        return arrayOf(DexDeobfStep(DexKit.C_CaptureUtil))
    }
}
