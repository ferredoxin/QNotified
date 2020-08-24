package me.singleneuron.hook

import android.content.res.Resources
import android.util.TypedValue
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.BaseDelayableConditionalHookAdapter
import me.singleneuron.data.PageFaultHighPerformanceFunctionCache
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object ChangeDrawerWidth : BaseDelayableConditionalHookAdapter("changeDrawerWidth") {

    override fun doInit(): Boolean {
        XposedHelpers.findAndHookMethod(Resources::class.java,"getDimensionPixelSize",Int::class.javaPrimitiveType,object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args[0] == getResourceID()) {
                    param.result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,75f,(param.thisObject as Resources).displayMetrics).toInt()
                }
            }
        })
        return true
    }

    //去com.tencent.mobileqq.activity.recent.DrawerFrame类里面找一个奇怪的只有一行以一个ID从Resources获取DimensionPixelSize的方法（大概率在最末尾），然后把ID填过来
    private fun getResourceID(): Int {
        return when(Utils.getHostVersionCode()) {
            QQVersion.QQ_8_4_1 -> 0x7f090834
            QQVersion.QQ_8_4_5 -> 0x7f090841
            else -> return super.getID()
        }
    }

    override val conditionCache: PageFaultHighPerformanceFunctionCache<Boolean> = PageFaultHighPerformanceFunctionCache { Utils.getHostVersionCode()==QQVersion.QQ_8_4_1 || Utils.getHostVersionCode()==QQVersion.QQ_8_4_5}

}
