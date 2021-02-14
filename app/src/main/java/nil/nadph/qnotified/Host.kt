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

package nil.nadph.qnotified

import android.app.Application
import me.singleneuron.qn_kernel.data.hostInformationProvider

/**
 * This class is for kotlin use ony. In case that you are writing java, use [nil.nadph.qnotified.H] .
 * To use this H in your kotlin, add `import nil.nadph.qnotified.Host.H` .
 * So you can use `H.application` with ease.
 */
internal class Host {
    object H {
        val application: Application by lazy {
            hostInformationProvider.applicationContext
        }
        val packageName: String by lazy {
            hostInformationProvider.packageName
        }
        val appName: String by lazy {
            hostInformationProvider.hostName
        }
        val versionName: String by lazy {
            hostInformationProvider.versionName
        }
        val versionCode: Int by lazy {
            hostInformationProvider.versionCode32
        }
        val longVersionCode: Long by lazy {
            hostInformationProvider.versionCode
        }
        val isTIM: Boolean by lazy {
            hostInformationProvider.isTim
        }
        val isQQ: Boolean by lazy {
            //Improve this method when supporting more clients.
            !hostInformationProvider.isTim
        }
    }
}
