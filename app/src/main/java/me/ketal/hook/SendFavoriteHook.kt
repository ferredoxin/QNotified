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
import android.content.Intent
import android.view.View
import android.widget.TextView
import me.ketal.util.HookUtil.getMethod
import me.nextalone.util.hookAfter
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils


object SendFavoriteHook: CommonDelayableHook("ketal_send_favorite", SyncUtils.PROC_ANY, DexDeobfStep(DexKit.N_PluginProxyActivity__initPlugin)) {
    var isHooked: Boolean = false

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

    override fun initOnce(): Boolean {
        return try {
            DexKit.doFindMethod(DexKit.N_PluginProxyActivity__initPlugin)!!
                .hookAfter(this) {
                    val context = it.thisObject as Activity
                    val intent = context.intent
                    if (intent.check()) {
                        val classLoader = "Lcom/tencent/mobileqq/pluginsdk/PluginStatic;->getClassLoader(Ljava/lang/String;)Ljava/lang/ClassLoader;"
                            .getMethod()
                            ?.invoke(null, "qqfav.apk") as ClassLoader
                        startHook(classLoader)
                    }
                }
            true
        } catch (e: Exception) {
            Utils.log(e)
             false
        }
    }

    private fun startHook(classLoader: ClassLoader) {
        "Lcom/qqfav/activity/FavoritesListActivity;->onCreate(Landroid/os/Bundle;)V"
            .getMethod(classLoader)
            ?.hookAfter(this) {
                val thisObj = it.thisObject as Activity
                isHooked = thisObj.intent.getBooleanExtra("bEnterToSelect", false)
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

    private fun getClassName(intent: Intent): String? {
        return when (intent.getIntExtra("nOperation", -1)) {
            0, 1, 3, 6, 7, 8, 11 -> "com.qqfav.FavoriteIpcDelegate"
            2 -> "com.qqfav.activity.FavoritesListActivity"
            9 -> "com.qqfav.group.activity.QfavGroupActivity"
            else -> {
                val component = intent.component ?: return null
                component.className
            }
        }
    }

    private fun Intent.check(): Boolean =
        "com.qqfav.activity.FavoritesListActivity" == getClassName(this)

    private fun String.findClass(classLoader: ClassLoader, init: Boolean = false): Class<*> =
        Class.forName(this, init, classLoader)
}
