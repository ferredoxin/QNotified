/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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
package me.ketal.hook

import android.app.Activity
import android.view.View
import android.widget.TextView
import ltd.nextalone.util.hookAfter
import me.ketal.base.PluginDelayableHook
import me.ketal.util.BaseUtil.tryVerbosely
import me.ketal.util.HookUtil.findClass
import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.ReflexUtil

@FunctionEntry
object SendFavoriteHook : PluginDelayableHook("ketal_send_favorite") {
    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

    override val pluginID = "qqfav.apk"

    override fun startHook(classLoader: ClassLoader) = tryVerbosely(false) {
        "Lcom/qqfav/activity/FavoritesListActivity;->onCreate(Landroid/os/Bundle;)V"
            .getMethod(classLoader)
            ?.hookAfter(this) {
                val thisObj = it.thisObject as Activity
                val isHooked = thisObj.intent.getBooleanExtra("bEnterToSelect", false)
                if (!isHooked) return@hookAfter
                val tv = findCancelTV(
                    thisObj,
                    "com.qqfav.activity.QfavBaseActivity".findClass(classLoader)
                )
                val logic = ReflexUtil.new_instance(
                    "com.qqfav.activity.FavoriteGroupLogic".findClass(classLoader),
                    thisObj, tv, thisObj::class.java, View::class.java
                )
                tv?.setOnClickListener {
                    tryVerbosely(false) {
                        ReflexUtil.invoke_virtual(logic, "b")
                        val b = ReflexUtil.iget_object_or_null(logic, "b", View::class.java)
                        if (b.visibility != 0) {
                            ReflexUtil.invoke_virtual(logic, "a")
                        } else {
                            ReflexUtil.invoke_virtual(logic, "a", true, Boolean::class.java)
                        }
                    }
                }
            }
        true
    }

    private fun findCancelTV(thisObject: Any, clazz: Class<*>): TextView? {
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
