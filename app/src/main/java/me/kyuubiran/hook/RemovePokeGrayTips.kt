package me.kyuubiran.hook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.utils.loadClass
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

//屏蔽戳一戳灰字提示
object RemovePokeGrayTips : BaseDelayableHook() {
    private const val kr_test_remove_tips: String = "kr_test_remove_tips"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInited) return true
        return try {
            val HighlightItem = loadClass("com.tencent.mobileqq.data.MessageForGrayTips\$HightlightItem")
            XposedBridge.hookAllConstructors(HighlightItem, object : XC_MethodHook(66) {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    val str = param?.args?.get(7) as String
                    if (str.contains("gxh.vip.qq.com")) param.thisObject = null
                }
            })
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_test_remove_tips)
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
            mgr.allConfig[kr_test_remove_tips] = enabled
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