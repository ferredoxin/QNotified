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
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.ketal.data.ConfigData
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.DirectResourceProvider
import org.ferredoxin.ferredoxinui.common.base.ResourceProvider
import org.ferredoxin.ferredoxinui.common.base.UiSwitchItem
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference

abstract class BaseDecorator : UiSwitchItem {

    val cfg: String = this::class.java.simpleName
    abstract override val preference: UiSwitchPreference

    fun uiSwitchPreference(init: UiSwitchPreferenceItemFactory.() -> Unit): UiSwitchPreference {
        val uiSwitchPreferenceFactory = UiSwitchPreferenceItemFactory()
        uiSwitchPreferenceFactory.init()
        return uiSwitchPreferenceFactory
    }

    inner class UiSwitchPreferenceItemFactory() : UiSwitchPreference {
        override lateinit var title: String
        override var summary: String? = null
        private lateinit var titleProviderCache: ResourceProvider<String>
        override var titleProvider: ResourceProvider<String>
            get() {
                if (this::titleProviderCache.isInitialized) {
                    return titleProviderCache
                } else {
                    return DirectResourceProvider(title)
                }
            }
            set(value) {
                titleProviderCache = value
            }
        private lateinit var summaryProviderCache: ResourceProvider<String?>
        override var summaryProvider: ResourceProvider<String?>
            get() {
                if (this::summaryProviderCache.isInitialized) {
                    return summaryProviderCache
                } else {
                    return DirectResourceProvider(summary)
                }
            }
            set(value) {
                summaryProviderCache = value
            }
        override val subSummary: String? = null
        override val clickAble: Boolean = true
        override var onClickListener: (Context) -> Boolean = { true }
        override var valid: Boolean = true
        private val configData = ConfigData<Boolean>(cfg)

        override val value: MutableStateFlow<Boolean?> by lazy {
            MutableStateFlow<Boolean?>(true).apply {
                SyncUtils.post {
                    try {
                        this.value = configData.getOrDefault(false)
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                    GlobalScope.launch {
                        collect {
                            configData.value = it
                        }
                    }
                }
            }
        }
    }
}
