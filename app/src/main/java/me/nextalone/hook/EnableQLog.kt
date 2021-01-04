package me.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object EnableQLog : CommonDelayableHook("na_enable_qlog") {

    override fun initOnce(): Boolean {
        return try {
            for (m: Method in getMethods("com.tencent.qphone.base.util.QLog")) {
                val argt = m.parameterTypes
                if (m.name == "isColorLevel" && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            param.result = true
                        }
                    })
                }
            }
            for (m: Method in getMethods("com.tencent.qphone.base.util.QLog")) {
                val argt = m.parameterTypes
                if (m.name == "getTag" && argt.size == 1 && argt[0] == String::class.java) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val tag = param.args[0]
                            param.result = "NAdump $tag"
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
}
