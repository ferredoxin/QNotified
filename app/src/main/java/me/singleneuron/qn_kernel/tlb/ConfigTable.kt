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
package me.singleneuron.qn_kernel.tlb

import me.singleneuron.qn_kernel.data.hostInfo

object ConfigTable {

    public val cacheMap: Map<String?, Any?> by lazy {
        val map: HashMap<String?, Any?> = HashMap()
        val versionCode = hostInfo.versionCode
        val table: ConfigTableInterface = if (hostInfo.isTim) TIMConfigTable() else QQConfigTable()
        for (pair in table.rangingConfigs) {
            for (i in versionCode downTo 1) {
                if (pair.value.containsKey(i)) {
                    map[pair.key] = pair.value[i]
                    break
                }
            }
        }
        for (pair in table.configs) {
            if (pair.value.containsKey(versionCode)) {
                map[pair.key] = pair.value[versionCode]
            }
        }
        map
    }

    fun <T> getConfig(className: String?): T {
        val config = cacheMap[className]
        return config as T
                ?: throw RuntimeException("$className :Unsupported Version: "+ hostInfo.versionName)
    }

}
