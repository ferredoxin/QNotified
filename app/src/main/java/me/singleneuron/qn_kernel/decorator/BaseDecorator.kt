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
package me.singleneuron.qn_kernel.decorator

import android.content.Context
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import me.ketal.data.ConfigData
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxin_ui.base.UiSwitchItem
import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference

abstract class BaseDecorator : UiSwitchItem {

    val cfg: String = this::class.java.simpleName
    abstract override val preference: UiSwitchPreference

    fun uiSwitchPreference(init: UiSwitchPreferenceItemFactory.() -> Unit): UiSwitchPreference {
        val uiSwitchPreferenceFactory = UiSwitchPreferenceItemFactory()
        uiSwitchPreferenceFactory.init()
        return uiSwitchPreferenceFactory
    }

    inner class UiSwitchPreferenceItemFactory: UiSwitchPreference {

        override lateinit var title: String
        override var summary: String? = null
        override var onClickListener: (Context) -> Boolean = { true }
        override var valid: Boolean = true
        private val configData = ConfigData<Boolean>(cfg)

        override val value: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>().apply {
                SyncUtils.post {
                    try {
                        postValue(configData.getOrDefault(false))
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                    observeForever {
                        Utils.logd("Config will change: $title=$it, now $title is ${configData.value}")
                        configData.value = it
                        Utils.logd("Config changed: $title=${configData.value}")
                    }
                }
            }
        }

    }

}
