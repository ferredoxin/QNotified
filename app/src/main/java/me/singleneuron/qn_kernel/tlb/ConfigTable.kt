/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package me.singleneuron.qn_kernel.tlb

import nil.nadph.qnotified.util.Utils

object ConfigTable {

    private val presentConfigMap: Map<String?, Map<Long, Any>> by lazy {
        return@lazy if (Utils.isTim()) {
            TIMConfigTable.configs
        } else {
            QQConfigTable.configs
        }
    }
    private val presentRangeConfigMap: Map<String?, Map<Long, Any>> by lazy {
        return@lazy if (Utils.isTim()) {
            TIMConfigTable.rangingConfigs
        } else {
            QQConfigTable.rangingConfigs
        }
    }

    public val cacheMap: Map<String?, Any?> by lazy {
        val map: HashMap<String?, Any?> = HashMap()
        val versionCode = Utils.getHostVersionCode()
        for (pair in presentRangeConfigMap) {
            for (i in versionCode downTo 1) {
                if (pair.value.containsKey(i)) {
                    map[pair.key] = pair.value[i]
                    break
                }
            }
        }
        for (pair in presentConfigMap) {
            if (pair.value.containsKey(versionCode)) {
                map[pair.key] = pair.value[versionCode]
            }
        }
        map
    }

    fun <T> getConfig(className: String?): T {
        val config = cacheMap[className]
        return config as T
                ?: throw RuntimeException("$className :Unsupported Version: "+Utils.getHostVersionCode())
    }

}
