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

package me.ketal.hook

import android.view.View
import de.robv.android.xposed.XC_MethodHook
import me.ketal.base.PluginDelayableHook
import me.ketal.util.HookUtil.findClass
import me.ketal.util.HookUtil.hookMethod
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.tlb.辅助功能
import nil.nadph.qnotified.SyncUtils.PROC_MAIN
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.ReflexUtil
import xyz.nextalone.util.invoke
import xyz.nextalone.util.tryOrFalse
import java.lang.reflect.Field

@FunctionEntry
@UiItem
object TroopFileSaveLasting : PluginDelayableHook("ketal_TroopFileSaveLasting") {
    override val preference = uiSwitchPreference {
        title = "群文件长按转存永久"
    }
    override val preferenceLocate = 辅助功能
    override val pluginID = "troop_plugin.apk"
    override fun getEffectiveProc() = PROC_MAIN

    override fun startHook(classLoader: ClassLoader) = tryOrFalse {
        val troopFileShowAdapter = "com.tencent.mobileqq.troop.data.TroopFileShowAdapter\$1"
            .findClass(classLoader).getDeclaredField("this$0").type
        val infoClass = troopFileShowAdapter.declaredFields.find {
            it.type == List::class.java
        }?.actualTypeArguments?.get(0) as Class<*>

        val itemClass = troopFileShowAdapter.declaredFields.find {
            it.type == Map::class.java
        }?.actualTypeArguments?.get(1) as Class<*>

        itemClass.declaredMethods.find {
            it.returnType == Boolean::class.java
                && it.parameterTypes.contentEquals(arrayOf(View::class.java))
        }?.hookMethod(object : XC_MethodHook() {
            lateinit var fields: List<Field>
            lateinit var tag: Any
            override fun beforeHookedMethod(param: MethodHookParam) {
                if (!isEnabled or LicenseStatus.sDisableCommonHooks) return
                tryOrFalse {
                    val view = param.args[0] as View
                    tag = view.tag
                    val info = ReflexUtil.getFirstByType(param.thisObject, infoClass)
                    fields = infoClass.declaredFields.filter {
                        it.isAccessible = true
                        it.type == Int::class.java
                            && it.get(info) == 102
                    }
                    fields.forEach {
                        it.set(info, 114514)
                    }
                    view.tag = info
                }
            }

            override fun afterHookedMethod(param: MethodHookParam) {
                if (!isEnabled or LicenseStatus.sDisableCommonHooks) return
                tryOrFalse {
                    val view = param.args[0] as View
                    val info = view.tag
                    fields.forEach {
                        it.set(info, 102)
                    }
                    view.tag = tag
                }
            }
        })
    }

    private val Field.actualTypeArguments: Array<*>
        get() {
            return genericType.invoke("getActualTypeArguments") as Array<*>
        }
}
