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

package me.ketal.hook

import ltd.nextalone.util.hookBefore
import me.ketal.base.PluginDelayableHook
import me.ketal.util.HookUtil.getField
import me.ketal.util.HookUtil.getMethod
import me.ketal.util.TIMVersion
import me.singleneuron.qn_kernel.data.requireMinVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.ReflexUtil
import nil.nadph.qnotified.util.Utils

object QZoneNoAD : PluginDelayableHook("ketal_qzone_hook") {
    override fun isValid(): Boolean = requireMinVersion(QQVersion.QQ_8_0_0, TIMVersion.TIM_1_0_0)

    override val pluginID = "qzone_plugin.apk"

    override fun startHook(classLoader: ClassLoader) = try {
        "Lcom/qzone/module/feedcomponent/ui/FeedViewBuilder;->setFeedViewData(Landroid/content/Context;Lcom/qzone/proxy/feedcomponent/ui/AbsFeedView;Lcom/qzone/proxy/feedcomponent/model/BusinessFeedData;ZZ)V"
            .getMethod(classLoader)
            ?.hookBefore(this) {
                val obj = "Lcom/qzone/proxy/feedcomponent/model/BusinessFeedData;->cellOperationInfo:Lcom/qzone/proxy/feedcomponent/model/CellOperationInfo;"
                    .getField(classLoader)
                    ?.get(it.args[2])!!
                val hashMap = ReflexUtil.iget_object_or_null(obj,"busiParam", Map::class.java)
                for (num in hashMap.keys) {
                    if (num == 194) {
                        it.result = null
                    }
                    if (num == 101 && (hashMap[num] as String).contains("v.gdt.qq.com")) {
                        it.result = null
                    }
                }

            }
        true
    }catch (e: Exception) {
        Utils.log(e)
        false
    }
}
