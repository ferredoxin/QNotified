package me.nextalone.hook.testhook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.nextalone.util.logd
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object TestCommonDelayable : CommonDelayableHook("na_test_base_delayable_kt") {

    override fun initOnce(): Boolean {
        return try {
            val hookSimpleName = this::class.java.simpleName
            logd("S", hookSimpleName)
            val className = ""
            for (m: Method in getMethods(className)) {
                logd("C", className)
                val argt = m.parameterTypes
                if (m.name == "methodName" && argt.size == 1) {
                    logd("M", m.name)
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            logd("B", hookSimpleName)
                        }

                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            logd("A", hookSimpleName)
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
