/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package me.kyuubiran.util

import android.content.Context
import android.os.Looper
import android.view.View
import android.widget.Toast
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null
import nil.nadph.qnotified.util.ReflexUtil.iput_object
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

fun getObjectOrNull(obj: Any?, objName: String, clz: Class<*>? = null): Any? {
    return iget_object_or_null(obj, objName, clz)
}

fun putObject(obj: Any?, name: String, value: Any?, type: Class<*>? = null) {
    iput_object(obj, name, type, value)
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


val Method.isStatic: Boolean
    get() = Modifier.isStatic(this.modifiers)


val Method.isPrivate: Boolean
    get() = Modifier.isPrivate(this.modifiers)


val Method.isPublic: Boolean
    get() = Modifier.isPublic(this.modifiers)


fun makeSpaceMsg(str: String): String {
    val sb = StringBuilder()
    if (str.length > 1) {
        for (i in str.indices) {
            sb.append(str[i])
            if (i != str.length - 1) sb.append(" ")
        }
    } else {
        sb.append(str)
    }
    return sb.toString()
}
