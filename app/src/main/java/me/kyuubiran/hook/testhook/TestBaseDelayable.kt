package me.kyuubiran.hook.testhook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//kotlin BaseDelayable模板
object TestBaseDelayable : BaseDelayableHook() {
    private const val kr_test_base_delayable_kt: String = "kr_test_base_delayable_kt"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInit) return true
        return try {
            for (m: Method in getMethods("className")) {
                val argt = m.parameterTypes
                if (m.name == "methodName" && argt.size == 1) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            Utils.logd("这是一个BaseDelayable模板")
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
            ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_test_base_delayable_kt)
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
            mgr.allConfig[kr_test_base_delayable_kt] = enabled
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