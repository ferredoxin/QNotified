package me.nextalone.hook.testhook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.nextalone.util.logd
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object TestBaseDelayable : BaseDelayableHook() {
    private const val na_test_base_delayable_kt: String = "na_test_base_delayable_kt"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInit) return true
        return try {
            val classSimpleName = this::class.java.simpleName
            logd("S", classSimpleName)
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
                            logd("B", classSimpleName)
                        }

                        override fun afterHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            logd("A", classSimpleName)
                        }
                    })
                }
            }
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(na_test_base_delayable_kt)
        } catch (e: java.lang.Exception) {
            Utils.log(e)
            false
        }
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_MAIN
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[na_test_base_delayable_kt] = enabled
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(Utils.getApplication(), Utils.TOAST_TYPE_ERROR, e.toString() + "", Toast.LENGTH_SHORT)
            } else {
                SyncUtils.post { Utils.showToast(Utils.getApplication(), Utils.TOAST_TYPE_ERROR, e.toString() + "", Toast.LENGTH_SHORT) }
            }
        }
    }

    override fun isInited(): Boolean {
        return isInit
    }
}
