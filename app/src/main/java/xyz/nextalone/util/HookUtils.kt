/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package xyz.nextalone.util

import android.os.Looper
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
import xyz.nextalone.bridge.NAMethodHook
import xyz.nextalone.bridge.NAMethodReplacement
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

internal val String.clazz: Class<*>?
    get() = Initiator.load(this)

internal val String.method: Method
    get() = DexMethodDescriptor(
        this.replace(".", "/").replace(" ", "")
    ).getMethodInstance(Initiator.getHostClassLoader())

internal fun Class<*>.method(name: String): Method? = this.declaredMethods.run {
    this.forEach {
        if (it.name == name) {
            return it
        }
    }
    return null
}

internal fun Class<*>.method(name: String, returnType: Class<*>?, vararg argsTypes: Class<*>?): Method? = this.declaredMethods.run {
    this.forEach {
        if (name == it.name && returnType == it.returnType && it.parameterTypes.contentEquals(argsTypes)) {
            return it
        }
    }
    return null
}

internal fun Class<*>.method(
    condition: (method: Method) -> Boolean = { true }
): Method? = this.declaredMethods.run {
    this.forEach {
        if (condition(it)) {
            return it
        }
    }
    return null
}

internal fun Class<*>.method(
    size: Int,
    returnType: Class<*>?,
    condition: (method: Method) -> Boolean = { true }
): Method? = this.declaredMethods.run {
    this.forEach {
        if (it.returnType == returnType && it.parameterTypes.size == size && condition(it)) {
            return it
        }
    }
    return null
}

internal fun Class<*>.method(
    name: String,
    size: Int,
    returnType: Class<*>?,
    condition: (method: Method) -> Boolean = { true }
): Method? = this.declaredMethods.run {
    this.forEach {
        if (it.name == name && it.returnType == returnType && it.parameterTypes.size == size && condition(it)) {
            return it
        }
    }
    return null
}

internal val Member.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)

internal val Member.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)

internal val Member.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)

internal val Member.isFinal: Boolean
    get() = Modifier.isFinal(this.modifiers)

internal val Class<*>.isAbstract: Boolean
    get() = Modifier.isAbstract(this.modifiers)

internal fun <T : BaseDelayableHook> T.tryOrFalse(function: () -> Unit): Boolean {
    return try {
        if (!this.isValid) return false
        function()
        true
    } catch (t: Throwable) {
        logThrowable(t)
        false
    }
}

internal fun Any?.get(name: String): Any? = this.get(name, null)

internal fun <T> Any?.get(name: String, type: Class<out T>? = null): T? =
    ReflexUtil.iget_object_or_null(this, name, type)

internal fun <T> Any?.get(type: Class<out T>? = null): T? {
    var clz = this?.javaClass
    while (clz != null && clz != Any::class.java) {
        for (f in clz.declaredFields) {
            if (f.type != type) {
                continue
            }
            f.isAccessible = true
            try {
                return f[this] as T
            } catch (ignored: IllegalAccessException) {
                //should not happen
            }
        }
        clz = clz.superclass
    }
    return null
}

internal fun <T> Any?.getAll(type: Class<out T>? = null): MutableList<T>? {
    var clz = this?.javaClass
    val objMutableList = mutableListOf<T>()
    while (clz != null && clz != Any::class.java) {
        for (f in clz.declaredFields) {
            if (f.type != type) {
                continue
            }
            f.isAccessible = true
            try {
                objMutableList.add(f[this] as T)
            } catch (ignored: IllegalAccessException) {
                //should not happen
            }
        }
        clz = clz.superclass
    }
    return objMutableList
}

internal fun Any?.set(name: String, value: Any): Any = ReflexUtil.iput_object(this, name, value)

internal fun Any?.set(name: String, clz: Class<*>?, value: Any): Any =
    ReflexUtil.iput_object(this, name, clz, value)

internal fun Class<*>?.instance(vararg arg: Any?): Any = XposedHelpers.newInstance(this, *arg)

internal fun Class<*>?.instance(type: Array<Class<*>>, vararg arg: Any?): Any =
    XposedHelpers.newInstance(this, type, *arg)

internal fun Any?.invoke(name: String, vararg args: Any): Any? =
    ReflexUtil.invoke_virtual(this, name, *args)

internal fun Member.hook(callback: NAMethodHook) = try {
    XposedBridge.hookMethod(this, callback)
} catch (e: Throwable) {
    logThrowable(e)
    null
}

internal fun Member.hookBefore(
    baseHook: BaseDelayableHook,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
) = hook(object : NAMethodHook(baseHook) {
    override fun beforeMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }
})

internal fun Member.hookAfter(
    baseHook: BaseDelayableHook,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
) = hook(object : NAMethodHook(baseHook) {
    override fun afterMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }
})

internal fun Member.replace(baseHook: BaseDelayableHook, result: Any?) = this.replace(baseHook) {
    result
}

internal fun <T : Any> Member.replace(
    baseHook: BaseDelayableHook,
    hooker: (XC_MethodHook.MethodHookParam) -> T?
) = hook(object : NAMethodReplacement(baseHook) {
    override fun replaceMethod(param: MethodHookParam) = try {
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

internal fun Class<*>.hookBefore(
    baseHook: BaseDelayableHook,
    method: String?,
    vararg args: Any?,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
) = hook(method, *args, object : NAMethodHook(baseHook) {
    override fun beforeMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }
})

internal fun Class<*>.hookAfter(
    baseHook: BaseDelayableHook,
    method: String?,
    vararg args: Any?,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
) = hook(method, *args, object : NAMethodHook(baseHook) {
    override fun afterMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }

})

internal fun Class<*>.replace(
    method: String?,
    vararg args: Any?,
    hooker: (XC_MethodHook.MethodHookParam) -> Any?
) = hook(method, *args, object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
        null
    }
})

internal fun Class<*>.hookAllMethods(
    methodName: String?,
    hooker: XC_MethodHook
): Set<XC_MethodHook.Unhook> = try {
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

internal fun Class<*>.hookBeforeAllMethods(
    baseHook: BaseDelayableHook,
    methodName: String?,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : NAMethodHook(baseHook) {
    override fun beforeMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }
})

internal fun Class<*>.hookAfterAllMethods(
    baseHook: BaseDelayableHook,
    methodName: String?,
    hooker: (XC_MethodHook.MethodHookParam) -> Unit
): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : NAMethodHook(baseHook) {
    override fun afterMethod(param: MethodHookParam) = try {
        hooker(param)
    } catch (e: Throwable) {
        logThrowable(e)
    }
})

internal fun Class<*>.replaceAfterAllMethods(
    methodName: String?,
    hooker: (XC_MethodHook.MethodHookParam) -> Any?
): Set<XC_MethodHook.Unhook> = hookAllMethods(methodName, object : XC_MethodReplacement() {
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

internal fun Class<*>.hookBeforeAllConstructors(hooker: (XC_MethodHook.MethodHookParam) -> Unit) =
    hookAllConstructors(object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam) = try {
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    })

internal fun Class<*>.hookAfterAllConstructors(hooker: (XC_MethodHook.MethodHookParam) -> Unit) =
    hookAllConstructors(object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) = try {
            hooker(param)
        } catch (e: Throwable) {
            logThrowable(e)
        }
    })

internal fun putValue(keyName: String, obj: Any, mgr: ConfigManager) {
    try {
        mgr.putObject(keyName, obj)
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

internal fun putDefault(keyName: String, obj: Any) = putValue(keyName, obj, getDefaultCfg())

internal fun putExFriend(keyName: String, obj: Any) = putValue(keyName, obj, getExFriendCfg())
