package me.kyuubiran.utils

import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.Toast
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Nullable
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier

fun Context.showToastBySystem(text: CharSequence, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.getMainLooper() == Looper.myLooper())
        Toast.makeText(this, text, duration).show()
    else Utils.runOnUiThread { showToastBySystem(text, duration) }
}

fun Context.showToastByTencent(text: CharSequence, type: Int = Utils.TOAST_TYPE_INFO, duration: Int = Toast.LENGTH_SHORT) {
    if (Looper.getMainLooper() == Looper.myLooper())
        Utils.showToast(this, type, text, duration)
    else Utils.runOnUiThread { showToastByTencent(text, duration) }
}

fun View.setViewZeroSize() {
    this.layoutParams.height = 0
    this.layoutParams.width = 0
}

fun log(t: Throwable) {
    Utils.log(t)
}

@Nullable
fun getObjectOrNull(obj: Any?, objName: String, clz: Class<*>? = null): Any? {
    return Utils.iget_object_or_null(obj, objName, clz)
}

fun putObject(obj: Any?, name: String, value: Any?, type: Class<*>? = null) {
    Utils.iput_object(obj, name, type, value)
}

fun loadClass(clzName: String): Class<*> {
    return Initiator.load(clzName)
}

fun getMethods(clzName: String): Array<Method> {
    return Initiator.load(clzName).declaredMethods
}

fun getMethods(clz: Class<Any>): Array<Method> {
    return clz.declaredMethods
}

fun Method.isStatic(): Boolean {
    return Modifier.isStatic(this.modifiers)
}

fun Method.isPrivate(): Boolean {
    return Modifier.isPrivate(this.modifiers)
}

fun Method.isPublic(): Boolean {
    return Modifier.isPublic(this.modifiers)
}