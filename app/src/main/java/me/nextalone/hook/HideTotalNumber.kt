package me.nextalone.hook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.getMethods
import me.singleneuron.qn_kernel.tlb.ConfigTable
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

object HideTotalNumber : BaseDelayableHook() {
    private const val na_hide_total_number: String = "na_hide_total_number"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInit) return true
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
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(na_hide_total_number)
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
            mgr.allConfig[na_hide_total_number] = enabled
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
