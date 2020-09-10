package me.singleneuron.hook

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHookAdapter
import me.singleneuron.hook.decorator.DisableQzoneSlideCamera
import nil.nadph.qnotified.SyncUtils

object StartActivityHook : BaseDelayableHookAdapter(cfgName = "startActivityHook",proc = SyncUtils.PROC_ANY) {

    val decorators = arrayOf(
            DebugDump,
            DisableQzoneSlideCamera
    )

    override fun doInit(): Boolean {
        //dump startActivity
        val hook = object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val intent : Intent = param!!.args[0] as Intent
                for (decorator in decorators) {
                    if (decorator.decorate(intent,param)){
                        return
                    }
                }
            }
        }
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(ContextWrapper::class.java,"startActivityForResult", hook)
        XposedBridge.hookAllMethods(Activity::class.java,"startActivity", hook)
        XposedBridge.hookAllMethods(Activity::class.java,"startActivityForResult", hook)
        return true
    }

    override fun setEnabled(enabled: Boolean) {}
    override fun isEnabled(): Boolean {
        return true
    }

}