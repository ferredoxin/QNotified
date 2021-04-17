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
package me.singleneuron.hook

import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import com.google.android.material.slider.Slider
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.ui.base.MaterialAlertDialogPreferenceFactory
import me.singleneuron.qn_kernel.ui.base.UiItem
import me.singleneuron.qn_kernel.ui.base.uiDialogPreference
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Utils.PACKAGE_NAME_QQ

@FunctionEntry
@me.singleneuron.qn_kernel.annotation.UiItem
object ChangeDrawerWidth : CommonDelayableHook("changeDrawerWidth"), UiItem {

    override fun initOnce(): Boolean {
        XposedHelpers.findAndHookMethod(Resources::class.java, "getDimensionPixelSize", Int::class.javaPrimitiveType, object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                if (param!!.args[0] == hostInfo.application.resources.getIdentifier(
                        "akx",
                        "dimen",
                        PACKAGE_NAME_QQ
                    )
                ) {
                    param.result = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        width.toFloat(),
                        (param.thisObject as Resources).displayMetrics
                    ).toInt()
                }
            }
        })
        return true
    }

    override fun isEnabled(): Boolean {
        return width != 0
    }

    private const val ChangeDrawerWidth_width = "ChangeDrawerWidth_width"

    var width: Int
        get() {
            return ConfigManager.getDefaultConfig().getIntOrDefault(ChangeDrawerWidth_width, 0)
        }
        set(value) {
            preference.value.value = "$value dp"
            ConfigManager.getDefaultConfig()
                .apply { putInt(ChangeDrawerWidth_width, value); save() }
        }

    fun getMaxWidth(context: Context): Float {
        val dm = DisplayMetrics()
        val windowManager: WindowManager =
            context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(dm)
        return (dm.widthPixels / dm.density)
    }

    override val preference: MaterialAlertDialogPreferenceFactory by lazy {
        uiDialogPreference {
            title = "修改侧滑边距"
            summary = "感谢祈无，支持8.4.1及更高，重启后生效"
            value.value = "$width dp"
            val slider = Slider(context)
            slider.valueFrom = 0f
            slider.valueTo = getMaxWidth(hostInfo.application).toInt().toFloat()
            slider.stepSize = 1f
            slider.value = width.toFloat()
            setPositiveButton("确定") { dialog: DialogInterface, _: Int ->
                width = slider.value.toInt()
                dialog.dismiss()
            }
            setTitle("修改侧滑边距（设置为0dp以禁用）")
            setView(slider)
        }
    }

    override val preferenceLocate: Array<String> = arrayOf("辅助功能")

}
