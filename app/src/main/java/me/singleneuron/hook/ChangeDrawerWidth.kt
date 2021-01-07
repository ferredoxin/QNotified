package me.singleneuron.hook

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.tlb.ConfigTable
import nil.nadph.qnotified.config.ConfigManager

object ChangeDrawerWidth : BaseDelayableConditionalHookAdapter("changeDrawerWidth") {

    override fun doInit(): Boolean {
        XposedHelpers.findAndHookMethod(Resources::class.java, "getDimensionPixelSize", Int::class.javaPrimitiveType, object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args[0] == ConfigTable.getConfig<Int>(ChangeDrawerWidth::class.simpleName)) {
                    param.result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width.toFloat(), (param.thisObject as Resources).displayMetrics).toInt()
                }
            }
        })
        return true
    }

    override fun setEnabled(enabled: Boolean) {}

    override fun isEnabled(): Boolean {
        return width!=0
    }

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
