package me.singleneuron.hook

import com.microsoft.appcenter.analytics.channel.SessionTracker
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.util.Utils
import java.text.SimpleDateFormat
import java.util.*

fun initAppCenterHook() {
    XposedHelpers.findAndHookMethod(SessionTracker::class.java, "hasSessionTimedOut", object : XC_MethodReplacement() {
        @Throws(Throwable::class)
        override fun replaceHookedMethod(methodHookParam: MethodHookParam): Any {
            try {
                val configManager = ConfigManager.getDefaultConfig()
                val LAST_TRACE_DATA_CONFIG = "lastTraceDate"
                val format = "yyyy-MM-dd"
                val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
                val nowTime = simpleDateFormat.format(Date(System.currentTimeMillis()))
                val oldTime = configManager.getString(LAST_TRACE_DATA_CONFIG)
                if (oldTime != null && oldTime == nowTime) {
                    Utils.logd("Hooked hasSessionTimedOut: oldTime=$oldTime nowTime=$nowTime, ignore")
                    return false
                }
                configManager.putString(LAST_TRACE_DATA_CONFIG, nowTime)
                configManager.save()
                Utils.logd("Hooked hasSessionTimedOut: oldTime=$oldTime nowTime=$nowTime, continue")
            } catch (e: Exception) {
                Utils.log(e)
            }
            return true
        }
    })
    XposedHelpers.findAndHookMethod(SessionTracker::class.java, "sendStartSessionIfNeeded", object : XC_MethodHook() {
        @Throws(Throwable::class)
        override fun beforeHookedMethod(param: MethodHookParam) {
            try {
                val configManager = ConfigManager.getDefaultConfig()
                val LAST_TRACE_DATA_CONFIG2 = "lastTraceDate2"
                val format = "yyyy-MM-dd"
                val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
                val nowTime = simpleDateFormat.format(Date(System.currentTimeMillis()))
                val oldTime = configManager.getString(LAST_TRACE_DATA_CONFIG2)
                if (oldTime != null && oldTime == nowTime) {
                    Utils.logd("Hooked sendStartSessionIfNeeded: oldTime=$oldTime nowTime=$nowTime, ignore")
                    param.result = null
                    return
                }
                configManager.putString(LAST_TRACE_DATA_CONFIG2, nowTime)
                configManager.save()
                Utils.logd("Hooked sendStartSessionIfNeeded: oldTime=$oldTime nowTime=$nowTime, continue")
            } catch (e: Exception) {
                Utils.log(e)
            }
        }
    })
}