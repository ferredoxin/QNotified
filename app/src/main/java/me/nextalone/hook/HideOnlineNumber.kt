package me.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object HideOnlineNumber : CommonDelayableHook("na_hide_online_number") {
    override fun initOnce(): Boolean {
        return try {
//            val classSimpleName = this::class.java.simpleName
            var className = "com.tencent.mobileqq.activity.aio.core.TroopChatPie"
            if (Utils.getHostVersionCode() <= QQVersion.QQ_8_4_8) {
                className = "com.tencent.mobileqq.activity.aio.rebuild.TroopChatPie"
            }
            for (m: Method in getMethods(className)) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 2 && argt[0] == String::class.java && argt[1] == Boolean::class.java) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            param.args[0] = ""
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
