package me.ketal.util

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import nil.nadph.qnotified.util.DexFieldDescriptor
import nil.nadph.qnotified.util.DexMethodDescriptor
import nil.nadph.qnotified.util.Initiator
import java.lang.reflect.Field
import java.lang.reflect.Method

object HookUtil {

    internal fun String.findClass(classLoader: ClassLoader, init: Boolean = false): Class<*> =
        Class.forName(this, init, classLoader)

    internal fun String.getMethod(classLoader: ClassLoader = Initiator.getHostClassLoader()) =
        try {
            DexMethodDescriptor(this).getMethodInstance(classLoader)
        } catch (e: Throwable) {
            null
        }

    internal fun Array<String>.getMethod(classLoader: ClassLoader = Initiator.getHostClassLoader()): Method? {
        this.forEach {
            it.getMethod(classLoader)?.apply {
                return this
            }
        }
        return null
    }

    internal fun String.getField(classLoader: ClassLoader = Initiator.getHostClassLoader()) =
        try {
            DexFieldDescriptor(this).getFieldInstance(classLoader)
        } catch (e: Throwable) {
            null
        }

    internal fun Array<String>.getField(classLoader: ClassLoader = Initiator.getHostClassLoader()): Field? {
        this.forEach {
            it.getField(classLoader)?.apply {
                return this
            }
        }
        return null
    }

    internal fun String.hookMethod(callback: XC_MethodHook) = getMethod()?.hookMethod(callback)

    internal fun Method.hookMethod(callback: XC_MethodHook) {
        XposedBridge.hookMethod(this, callback)
    }
}
