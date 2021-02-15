package me.nextalone.util

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.util.DexMethodDescriptor
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal val linearParams = LinearLayout.LayoutParams(0, 0)
internal val relativeParams = RelativeLayout.LayoutParams(0, 0)
internal val String.clazz: Class<*>
    get() = Initiator.load(this)

internal val String.method: Method
    get() = DexMethodDescriptor(this).getMethodInstance(Initiator.getHostClassLoader())

internal val String.methods: Array<Method>
    get() = Initiator.load(this).declaredMethods

internal val Member.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)

internal val Member.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)

internal val Member.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)

internal fun Member.hook(callback: XC_MethodHook) = try {
    XposedBridge.hookMethod(this, callback)
} catch (e: Throwable) {
    logThrowable(e)
    null
}

internal val hookNull: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = null
}

internal val hookFalse: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = false
}

internal val hookTrue: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = true
}


internal inline fun Member.hookBefore(baseHook: BaseDelayableHook, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hook(object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Member.hookAfter(baseHook: BaseDelayableHook, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hook(object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Member.replace(crossinline hooker: (XC_MethodHook.MethodHookParam) -> Any?) = hook(object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
        null
    }
})

internal fun Class<*>.hook(method: String?, vararg args: Any?) = try {
    XposedHelpers.findAndHookMethod(this, method, *args)
} catch (e: NoSuchMethodError) {
    logThrowable(e)
    null
} catch (e: XposedHelpers.ClassNotFoundError) {
    logThrowable(e)
    null
} catch (e: ClassNotFoundException) {
    logThrowable(e)
    null
}

internal inline fun Class<*>.hookBefore(baseHook: BaseDelayableHook, method: String?, vararg args: Any?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hook(method, *args, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Class<*>.hookAfter(baseHook: BaseDelayableHook, method: String?, vararg args: Any?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hook(method, *args, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Class<*>.replace(method: String?, vararg args: Any?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Any?) = hook(method, *args, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
        null
    }
})

internal fun Class<*>.hookAllMethods(methodName: String?, hooker: XC_MethodHook): Set<XC_MethodHook.Unhook> = try {
    XposedBridge.hookAllMethods(this, methodName, hooker)
} catch (e: NoSuchMethodError) {
    logThrowable(e)
    emptySet()
} catch (e: XposedHelpers.ClassNotFoundError) {
    logThrowable(e)
    emptySet()
} catch (e: ClassNotFoundException) {
    logThrowable(e)
    emptySet()
}

internal inline fun Class<*>.hookBeforeAllMethods(baseHook: BaseDelayableHook, methodName: String?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Class<*>.hookAfterAllMethods(baseHook: BaseDelayableHook, methodName: String?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            if (!baseHook.isEnabled or LicenseStatus.sDisableCommonHooks) return
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Class<*>.replaceAfterAllMethods(methodName: String?, crossinline hooker: (XC_MethodHook.MethodHookParam) -> Any?): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
        null
    }
})

internal fun Class<*>.hookAllConstructors(hooker: XC_MethodHook): Set<XC_MethodHook.Unhook> = try {
    XposedBridge.hookAllConstructors(this, hooker)
} catch (e: NoSuchMethodError) {
    logThrowable(e)
    emptySet()
} catch (e: XposedHelpers.ClassNotFoundError) {
    logThrowable(e)
    emptySet()
} catch (e: ClassNotFoundException) {
    logThrowable(e)
    emptySet()
}

internal inline fun Class<*>.hookBeforeAllConstructors(crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hookAllConstructors(object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

internal inline fun Class<*>.hookAfterAllConstructors(crossinline hooker: (XC_MethodHook.MethodHookParam) -> Unit) = hookAllConstructors(object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        try {
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    }
})

fun View.setViewZeroSize() {
    this.layoutParams.height = 0
    this.layoutParams.width = 0
}


fun View.hide() {
    this.visibility = View.GONE
    val viewGroup = this.parent as ViewGroup
    if (viewGroup is LinearLayout) {
        this.layoutParams = linearParams
    } else if (viewGroup is RelativeLayout) {
        this.layoutParams = relativeParams
    }
}
