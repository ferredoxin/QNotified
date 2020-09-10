package me.singleneuron.hook

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.util.Utils

object ChangeDrawerWidth : BaseDelayableConditionalHookAdapter("changeDrawerWidth") {

    override fun doInit(): Boolean {
        XposedHelpers.findAndHookMethod(Resources::class.java, "getDimensionPixelSize", Int::class.javaPrimitiveType, object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args[0] == getResourceID()) {
                    param.result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width.toFloat(), (param.thisObject as Resources).displayMetrics).toInt()
                }
            }
        })
        return true
    }

    //去com.tencent.mobileqq.activity.recent.DrawerFrame类里面找一个奇怪的只有一行以一个ID从Resources获取DimensionPixelSize的方法（大概率在最末尾），然后把ID填过来
    internal fun getResourceID(): Int {
        return when (Utils.getHostVersionCode()) {
            QQVersion.QQ_8_4_1 -> 0x7f090834
            QQVersion.QQ_8_4_5 -> 0x7f090841
            QQVersion.QQ_8_4_8 -> 0x7f090882
            else -> return super.getID()
        }
    }

    override fun setEnabled(enabled: Boolean) {}

    override fun isEnabled(): Boolean {
        return width!=0
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { Utils.getHostVersionCode() >= QQVersion.QQ_8_4_1 }

    private const val ChangeDrawerWidth_width = "ChangeDrawerWidth_width"

    var width: Int
        get() {
            return ConfigManager.getDefaultConfig().getIntOrDefault(ChangeDrawerWidth_width, 0)
        }
        set(value) {
            ConfigManager.getDefaultConfig().apply { putInt(ChangeDrawerWidth_width, value); save() }
        }

    fun getMaxWidth(context: Context) :Float {
        val dm = DisplayMetrics()
        val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(dm)
        return (dm.widthPixels/dm.density)
    }
}
