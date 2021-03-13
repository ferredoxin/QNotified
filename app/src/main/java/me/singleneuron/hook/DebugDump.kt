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
package me.singleneuron.hook

import android.app.Activity
import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.base.decorator.BaseStartActivityHookDecorator
import me.singleneuron.util.dump
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.Utils

@FunctionEntry
object DebugDump : BaseStartActivityHookDecorator("debugDump") {

    override fun doDecorate(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        Utils.logd("debugDump: startActivity " + param.thisObject::class.java.name)
        intent.dump()
        return false
    }

    override fun doInit(): Boolean {
        //dump setResult
        XposedBridge.hookAllMethods(
            Activity::class.java,
            "setResult",
            object : XposedMethodHookAdapter() {
                override fun beforeMethod(param: MethodHookParam?) {
                    if (param!!.args.size != 2) return
                    val intent = param.args[1] as Intent
                    Utils.logd("debugDump: setResult " + param.thisObject::class.java.name)
                    intent.dump()
                }
            })
        return true
    }
}
