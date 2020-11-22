package me.kyuubiran.hook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook

import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.*
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils

//屏蔽戳一戳灰字提示
object RemovePokeGrayTips : BaseDelayableHook() {
    private const val kr_remove_poke_tips: String = "kr_remove_poke_tips"
    val keys = listOf("拍了拍", "戳了戳", "亲了亲", "抱了抱", "揉了揉", "喷了喷", "踢了踢", "舔了舔", "捏了捏", "摸了摸")
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInit) return true
        return try {
            val Msg = loadClass("com.tencent.imcore.message.QQMessageFacade\$Message")
            val MsgRecord = loadClass("com.tencent.mobileqq.data.MessageRecord")
            for (m in getMethods("com.tencent.imcore.message.QQMessageFacade")) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] == Msg::class.java) {
                    logd(LOG_TYPE_FIND_METHOD, "m -> $m")
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val msg = getObjectOrNull(param.args[0], "msg", String::class.java) as String
                            logd("msg -> $msg")
                        }
                    })
                }
            }
            isInit = true
            true
        } catch (t: Throwable) {
            logdt(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_remove_poke_tips)
        } catch (e: java.lang.Exception) {
            Utils.log(e)
            false
        }
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_ANY
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[kr_remove_poke_tips] = enabled
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