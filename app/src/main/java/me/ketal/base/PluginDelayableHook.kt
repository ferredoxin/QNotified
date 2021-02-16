/*
 * QNotified - An Xposed module for QQ/TIM
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

package me.ketal.base

import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.BuildConfig
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils

abstract  class PluginDelayableHook(keyName: String) : CommonDelayableHook(keyName, SyncUtils.PROC_ANY) {
    abstract val pluginID: String

    abstract fun startHook(classLoader: ClassLoader) : Boolean

    override fun initOnce() = try {
        val classLoader = "Lcom/tencent/mobileqq/pluginsdk/PluginStatic;->getOrCreateClassLoader(Landroid/content/Context;Ljava/lang/String;)Ljava/lang/ClassLoader;"
            .getMethod()
            ?.invoke(null, hostInfo.application, pluginID) as ClassLoader
        startHook(classLoader)
    } catch (t: Throwable) {
        if (BuildConfig.DEBUG) {
            Utils.log(t)
        }
        false
    }
}
