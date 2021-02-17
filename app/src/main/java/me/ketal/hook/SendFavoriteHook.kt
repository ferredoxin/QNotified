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
package me.ketal.hook

import android.app.Activity
import android.view.View
import android.widget.TextView
import ltd.nextalone.util.hookAfter
import me.ketal.base.PluginDelayableHook
import me.ketal.util.HookUtil.findClass
import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils


object SendFavoriteHook: PluginDelayableHook("ketal_send_favorite") {
    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

    override val pluginID = "qqfav.apk"

    override fun startHook(classLoader: ClassLoader) = try {
        "Lcom/qqfav/activity/FavoritesListActivity;->onCreate(Landroid/os/Bundle;)V"
            .getMethod(classLoader)
            ?.hookAfter(this) {
                val thisObj = it.thisObject as Activity
                val isHooked = thisObj.intent.getBooleanExtra("bEnterToSelect", false)
                if (!isHooked) return@hookAfter
                val tv = findCancelTV(thisObj, "com.qqfav.activity.QfavBaseActivity".findClass(classLoader))
                val logic = ReflexUtil.new_instance("com.qqfav.activity.FavoriteGroupLogic".findClass(classLoader),
                    thisObj, tv, thisObj::class.java, View::class.java)
                tv?.setOnClickListener {
                    try {
                        ReflexUtil.invoke_virtual(logic, "b")
                        val b = ReflexUtil.iget_object_or_null(logic, "b", View::class.java)
                        if (b.visibility != 0) {
                            ReflexUtil.invoke_virtual(logic, "a")
                        } else {
                            ReflexUtil.invoke_virtual(logic, "a", true, Boolean::class.java)
                        }
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                }
            }
        true
    }  catch (t: Throwable) {
        Utils.log(t)
        false
    }

    private fun findCancelTV(thisObject: Any, clazz: Class<*>) : TextView? {
        for (field in clazz.declaredFields) {
            field.isAccessible = true
            if (field[thisObject] is TextView) {
                val tv = field[thisObject] as TextView
                if (tv.text == "取消") {
                    tv.text = "选择分组"
                    return tv
                }
            }
        }
        return null
    }
}
