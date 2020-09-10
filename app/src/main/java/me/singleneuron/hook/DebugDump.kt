package me.singleneuron.hook

import android.app.Activity
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.decorator.BaseStartActivityHookDecorator
import me.singleneuron.util.dump
import nil.nadph.qnotified.util.Utils

object DebugDump : BaseStartActivityHookDecorator("debugDump") {

    override fun doDecorate(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        Utils.logd("debugDump: startActivity "+param.thisObject::class.java.name)
        intent.dump()
        return false
    }

    override fun doInit(): Boolean {
        //dump setResult
        XposedBridge.hookAllMethods(Activity::class.java,"setResult", object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args.size!=2) return
                val intent = param.args[1] as Intent
                Utils.logd("debugDump: setResult "+param.thisObject::class.java.name)
                intent.dump()
            }
        })
        return true
    }
}