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

package me.singleneuron.qn_kernel.tlb

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import me.ketal.data.ConfigData
import me.singleneuron.qn_kernel.ui.activity.Material3Activity
import me.singleneuron.qn_kernel.ui.activity.MaterialActivity
import me.singleneuron.qn_kernel.ui.activity.NewSettingsActivity
import me.singleneuron.qn_kernel.ui.activity.SettingActivity
import nil.nadph.qnotified.R
import org.ferredoxin.ferredoxinui.common.base.UiChangeableItemFactory

object ActivityRouter : UiChangeableItemFactory<String>() {
    private val activityMap = mapOf<String, Class<*>>(
        "QQ主题" to NewSettingsActivity::class.java,
        "关怀模式" to SettingActivity::class.java,
        "Android Classic" to MaterialActivity::class.java,
        "Material You" to Material3Activity::class.java,
    )
    override var title: String = "设置界面样式"
    val configData = ConfigData<String>(title)
    override val value: MutableStateFlow<String?> by lazy {
        MutableStateFlow(configData.getOrDefault("关怀模式")).apply {
            GlobalScope.launch {
                collect {
                    configData.value = it
                }
            }
        }
    }
    override var onClickListener: (Activity) -> Boolean = {
        MaterialAlertDialogBuilder(it, R.style.MaterialDialog)
            .setTitle("选择设置界面样式")
            .setItems(activityMap.keys.toTypedArray()) { _: DialogInterface, i: Int ->
                value.value = activityMap.keys.toTypedArray()[i]
                it.startActivity(Intent(it, activityMap[activityMap.keys.toTypedArray()[i]]))
                it.finish()
            }
            .create()
            .show()
        true
    }

    fun getActivityClass(): Class<*> {
        return activityMap[value.value] ?: NewSettingsActivity::class.java
    }
}
