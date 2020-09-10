package me.singleneuron.hook.decorator

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import me.singleneuron.base.decorator.BaseStartActivityHookDecorator

object DisableQzoneSlideCamera: BaseStartActivityHookDecorator("disableCameraOnSweepingInQzone") {

    override fun doDecorate(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        if (intent.data?.toString()?.contains("qzoneSlideCamera") == true) {
            param.result = null
            return true
        }
        return false
    }

}