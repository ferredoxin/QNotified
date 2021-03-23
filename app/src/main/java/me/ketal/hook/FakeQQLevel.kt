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

package me.ketal.hook

import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.input
import ltd.nextalone.util.get
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.set
import ltd.nextalone.util.tryOrFalse
import me.ketal.data.ConfigData
import me.ketal.ui.view.ConfigView
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.ui.CommonContextWrapper
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Utils

@FunctionEntry
object FakeQQLevel : CommonDelayableHook("Ketal_FakeQQLevel", DexDeobfStep(DexKit.N_ProfileCardUtil_getCard)) {
    private val levelKey = ConfigData<String>("Ketal_FakeQQLevel_level")
    private var level
        get() = levelKey.getOrDefault("114514")
        set(value) {
            levelKey.value = value
        }

    fun listener() = View.OnClickListener {
        val context = CommonContextWrapper.createMaterialDesignContext(it.context)
        val vg = ConfigView(context)
        vg.setText("启用自定义QQ等级")
        val dialog = MaterialDialog(context).show {
            title(text = "自定义QQ等级")
            input(hint = "自定义QQ等级...", prefill = level, waitForPositiveButton = false) { dialog, text ->
                val inputField = dialog.getInputField()
                dialog.setActionButtonEnabled(WhichButton.POSITIVE, try {
                    text.toString().toInt()
                    inputField.error = null
                    true
                } catch (e : NumberFormatException) {
                    inputField.error = "请输入有效的数据"
                    false
                })
            }
            positiveButton(text = "保存") {
                val text = getInputField().text.toString()
                val enableFake = vg.isChecked
                isEnabled = enableFake
                if (enableFake) {
                    level = text
                    if (!isInited) ViewBuilder.doSetupAndInit(context, this@FakeQQLevel)
                }
                dismiss()
            }
            negativeButton(text = "取消")
        }
        vg.view = dialog.getCustomView()
        vg.isVisible = isEnabled
        vg.isChecked = isEnabled
        dialog.view.contentLayout.customView = null
        dialog.customView(view = vg)
    }

    override fun initOnce() = tryOrFalse {
        DexKit.doFindMethod(DexKit.N_ProfileCardUtil_getCard)?.hookAfter(this) {
            if (it.result.get("uin") == Utils.getAccount()) {
                it.result.set("iQQLevel", level.toInt())
            }
        }
    }
}
