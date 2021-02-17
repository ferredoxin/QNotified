package ltd.nextalone.util

import android.content.SharedPreferences
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.getDefaultCfg
import me.kyuubiran.util.getExFriendCfg
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.util.*
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.text.DateFormat
import java.util.*

internal val linearParams = LinearLayout.LayoutParams(0, 0)
internal val relativeParams = RelativeLayout.LayoutParams(0, 0)
internal val isSimpleUi by lazy {
    try {
        val sharedPreferences = "Lcom/tencent/mobileqq/theme/ThemeUtil;->getUinThemePreferences(Lmqq/app/AppRuntime;)Landroid/content/SharedPreferences;".method.invoke(null, Utils.getAppRuntime()) as SharedPreferences
        sharedPreferences.getBoolean("key_simple_ui_switch", false)
    } catch (t: Throwable) {
        false
    }
}

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

internal val hookNull: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = null
}

internal val hookEmpty: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = ""
}

internal val hookFalse: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = false
}

internal val hookTrue: (XC_MethodHook.MethodHookParam) -> Unit = {
    it.result = true
}

internal fun Any?.get(objName: String, clz: Class<*>? = null): Any? {
    return ReflexUtil.iget_object_or_null(this, objName, clz)
}

internal fun Member.hook(callback: XC_MethodHook) = try {
    XposedBridge.hookMethod(this, callback)
} catch (e: Throwable) {
    logThrowable(e)
    null
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

fun <T : View> T.qqId(name: String) = this.resources.getIdentifier(name, "id", Utils.PACKAGE_NAME_QQ)

internal fun <T : View?> View.findQQView(name: String): T? {
    this.let {
        return it.findViewById<T>(it.qqId(name))
    }
}

internal val Date.today: String
    get() = DateFormat.getDateInstance().format(this)

internal fun putValue(keyName: String, obj: Any, mgr: ConfigManager) {
    try {
        mgr.allConfig[keyName] = obj
        mgr.save()
    } catch (e: Exception) {
        Utils.log(e)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toasts.error(hostInfo.application, e.toString() + "")
        } else {
            SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
        }
    }
}

internal fun putDefault(keyName: String, obj: Any) {
    putValue(keyName, obj, getDefaultCfg())
}

internal fun putExFriend(keyName: String, obj: Any) {
    putValue(keyName, obj, getExFriendCfg())
}
