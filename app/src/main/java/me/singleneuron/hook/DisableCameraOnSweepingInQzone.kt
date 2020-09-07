package me.singleneuron.hook

import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.BaseDelayableHookAdapter

object DisableCameraOnSweepingInQzone: BaseDelayableHookAdapter("disableCameraOnSweepingInQzone") {

    override fun doInit(): Boolean {
        val cameraClass = Class.forName("dov.com.qq.im.QIMCameraCaptureActivity")
        XposedBridge.hookAllMethods(cameraClass,"a",object : XposedMethodReplacementAdapter(){
            override fun replaceMethod(param: MethodHookParam?): Any? {
                return null
            }
        })
        return true
    }

}