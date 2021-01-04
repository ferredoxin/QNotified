package me.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object HideTotalNumber : CommonDelayableHook("na_hide_total_number") {
    
    override fun initOnce(): Boolean {
        return try {
//            val classSimpleName = this::class.java.simpleName
            var className = "com.tencent.mobileqq.activity.aio.core.TroopChatPie"
            if (Utils.getHostVersionCode() <= QQVersion.QQ_8_4_8) {
                className = "com.tencent.mobileqq.activity.aio.rebuild.TroopChatPie"
            }
            for (m: Method in getMethods(className)) {
                val argt = m.parameterTypes
                if (m.name == ConfigTable.getConfig(HideTotalNumber::class.simpleName) && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
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
}
