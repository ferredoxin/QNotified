package me.kyuubiran.utils

import android.content.Context
import android.os.Looper
import android.widget.Toast
import nil.nadph.qnotified.util.Utils

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
