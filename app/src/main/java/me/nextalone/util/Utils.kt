package me.nextalone.util

import android.view.View
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import nil.nadph.qnotified.util.DexMethodDescriptor
import nil.nadph.qnotified.util.Initiator
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object Utils {
    val String.clazz: Class<*>
        get() = Initiator.load(this)

    val String.method: Method
        get() = DexMethodDescriptor(this).getMethodInstance(Initiator.getHostClassLoader())

    val String.methods: Array<Method>
        get() = Initiator.load(this).declaredMethods

    val Method.isStatic: Boolean
        get() = Modifier.isStatic(this.modifiers)

    val Method.isPrivate: Boolean
        get() = Modifier.isPrivate(this.modifiers)

    val Method.isPublic: Boolean
        get() = Modifier.isPublic(this.modifiers)

    fun Method.hook(callback: XC_MethodHook) {
        XposedBridge.hookMethod(this, callback)
    }

    fun Method.replace(callback: XC_MethodReplacement) {
        XposedBridge.hookMethod(this, callback)
    }

    fun View.setViewZeroSize() {
        this.layoutParams.height = 0
        this.layoutParams.width = 0
    }
}
