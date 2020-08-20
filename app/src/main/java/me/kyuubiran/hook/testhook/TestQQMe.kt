package me.kyuubiran.hook.testhook

import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.hook.SimplifyQQSettingMe
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

//测试用 精简侧滑栏
object TestQQMe : BaseDelayableHook() {
    private const val kr_test_qq_me: String = "kr_test_qq_me"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    fun hasKeyWords(keyword: String): Boolean {
        val keywords = listOf("开播啦", "小世界", "情侣", "相册", "日程", "视频",
                "小游戏", "文档", "打卡", "王卡")
        for (i in keywords.indices) {
            if (keyword.contains(keywords[i])) return true
        }
        return false
    }

    fun setZeroHeightWeight(v: View) {
        v.layoutParams.width = 0
        v.layoutParams.height = 0
    }

    override fun init(): Boolean {
        if (isInited) return true
        return try {
            val clz = Initiator.load("com.tencent.mobileqq.activity.QQSettingMe")
            XposedBridge.hookAllConstructors(clz, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    val layout1: LinearLayout = Utils.iget_object_or_null(param?.thisObject, "k", View::class.java) as LinearLayout
                    val layout2: LinearLayout = Utils.iget_object_or_null(param?.thisObject, "h", View::class.java) as LinearLayout
//                    val layout3: LinearLayout = Utils.iget_object_or_null(param?.thisObject, "a", View::class.java) as LinearLayout
//                    setZeroHeightWeight(layout3)
                    for (i in 1 until layout2.childCount) {
                         val child = layout2.getChildAt(i) as LinearLayout
                        val tv = child.getChildAt(1) as TextView
                        val text = tv.text
                        when {
                            text.contains("间") -> {
                                setZeroHeightWeight(child)
                            }
                            (text.contains("达") || text.contains("天")) -> {
                                setZeroHeightWeight(child)
                            }
                            i == 3 -> {
                                setZeroHeightWeight(child)
                            }
                        }
                    }
                    if (layout1.toString().contains("midcontent_list")) {
                        val count = layout1.childCount
                        for (i in 1 until count) {
                            val child = layout1.getChildAt(i) as LinearLayout
                            val tv = child.getChildAt(1) as TextView
                            val text = tv.text
                            if (hasKeyWords(text.toString())) {
                                setZeroHeightWeight(child)
                            }
                        }
                    }
                }
            })
            for (m: Method in clz.declaredMethods) {
                val argt = m.parameterTypes
                if (m.name == "V" && !Modifier.isStatic(m.modifiers) && argt.isEmpty()) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            param?.result = null
                        }
                    })
                }
            }
            SimplifyQQSettingMe.isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_test_qq_me)
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
            mgr.allConfig[kr_test_qq_me] = enabled
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

//when {
//    text.contains("开播啦") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("小世界") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("会员") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("钱包") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("装扮") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("情侣") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("相册") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("收藏") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("文件") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("日程") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("视频") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("小游戏") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("文档") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("打卡") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//    text.contains("王卡") -> {
//        child.layoutParams.height = 0
//        child.layoutParams.width = 0
//    }
//}