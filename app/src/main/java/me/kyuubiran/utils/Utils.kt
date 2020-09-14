package me.kyuubiran.utils

import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.Toast
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.Nullable
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

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

fun setZeroHeightWeight(v: View) {
    v.layoutParams.width = 0
    v.layoutParams.height = 0
}

fun logd(msg: String) {
    Utils.logd("好耶 $msg")
}

fun logd(i: Int, msg: String? = "") {
    when (i) {
        0 -> logd("找到类了 $msg")
        1 -> logd("找到方法了 $msg")
        2 -> logd("开始Hook了 $msg")
        3 -> logd("开始搞事情了 $msg")
        4 -> logd("搞完事情了 $msg")
        5 -> logd("搞出大事情了 \n$msg")
    }
}

fun log(t: Throwable) {
    Utils.log(t)
}

@Nullable
fun getObjectOrNull(obj: Any?, str: String, clz: Class<*>? = null): Any? {
    return Utils.iget_object_or_null(obj, str, clz)
}

fun putObject(obj: Any?, name: String, value: Any, type: Class<*>? = null) {
    Utils.iput_object(obj, name, type, value)
}

fun loadClass(clzName: String): Class<*> {
    return Initiator.load(clzName)
}

fun getMethods(clzName: String): Array<Method> {
    return Initiator.load(clzName).declaredMethods
}