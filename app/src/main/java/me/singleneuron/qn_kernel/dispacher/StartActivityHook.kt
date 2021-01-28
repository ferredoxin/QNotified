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
package me.singleneuron.qn_kernel.dispacher

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.adapter.BaseDelayableHookAdapter
import me.singleneuron.hook.DebugDump
import me.singleneuron.hook.decorator.DisableQzoneSlideCamera
import nil.nadph.qnotified.SyncUtils

object StartActivityHook : BaseDelayableHookAdapter(cfgName = "startActivityHook",proc = SyncUtils.PROC_ANY) {

    val decorators = arrayOf(
            DebugDump,
            DisableQzoneSlideCamera
    )

    override fun doInit(): Boolean {
        //dump startActivity
        val hook = object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val intent : Intent = param!!.args[0] as Intent
                for (decorator in decorators) {
                    if (decorator.decorate(intent,param)){
                        return
                    }
                }
            }
        }
        XposedBridge.hookAllMethods(ContextWrapper::class.java, "startActivity", hook)
        XposedBridge.hookAllMethods(ContextWrapper::class.java,"startActivityForResult", hook)
        XposedBridge.hookAllMethods(Activity::class.java,"startActivity", hook)
        XposedBridge.hookAllMethods(Activity::class.java,"startActivityForResult", hook)
        return true
    }

    override fun setEnabled(enabled: Boolean) {}
    override fun isEnabled(): Boolean {
        return true
    }

}
