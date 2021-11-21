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

package me.singleneuron.qn_kernel.base

import android.content.Context
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import me.ketal.data.ConfigData
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxin_ui.base.UiItem
import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference

abstract class CommonDelayAbleHookBridge
@JvmOverloads
constructor(
    targetProcess: Int = SyncUtils.PROC_MAIN,
    vararg preconditions: Step = emptyArray(),
) : CommonDelayableHook("", targetProcess, *preconditions), UiItem {

    constructor(vararg preconditions: Step = emptyArray()) : this(
        SyncUtils.PROC_MAIN,
        *preconditions
    )

    val configName: String = this::class.java.simpleName

    abstract override val preference: UiSwitchPreference

    fun uiSwitchPreference(init: UiSwitchPreferenceItemFactory.() -> Unit): UiSwitchPreference {
        val uiSwitchPreferenceFactory = this.UiSwitchPreferenceItemFactory()
        uiSwitchPreferenceFactory.init()
        return uiSwitchPreferenceFactory
    }

    override fun isEnabled(): Boolean {
        return preference.value.value == true
    }

    override fun setEnabled(enabled: Boolean) {
        preference.value.postValue(enabled)
    }

    open inner class UiSwitchPreferenceItemFactory constructor() : UiSwitchPreference {
        override lateinit var title: String
        override var summary: String? = null
        override var valid: Boolean = isValid
        private val configData: ConfigData<Boolean> = ConfigData(configName)

        @JvmOverloads
        constructor(title: String, summary: String? = null) : this() {
            this.title = title
            this.summary = summary
        }

        override val value: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>().apply {
                SyncUtils.post {
                    try {
                        postValue(configData.getOrDefault(false))
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                    observeForever {
                        configData.value = it
                    }
                }
            }
        }
        override var onClickListener: (Context) -> Boolean = { true }
    }

}
