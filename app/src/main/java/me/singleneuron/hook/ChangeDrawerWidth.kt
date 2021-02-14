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
package me.singleneuron.hook

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.WindowManager
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.util.Utils.PACKAGE_NAME_QQ

object ChangeDrawerWidth : BaseDelayableConditionalHookAdapter("changeDrawerWidth") {

    override fun doInit(): Boolean {
        XposedHelpers.findAndHookMethod(Resources::class.java, "getDimensionPixelSize", Int::class.javaPrimitiveType, object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                if (param!!.args[0] == hostInfo.application.resources.getIdentifier("akx", "dimen", PACKAGE_NAME_QQ)) {
                    param.result = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, width.toFloat(), (param.thisObject as Resources).displayMetrics).toInt()
                }
            }
        })
        return true
    }

    override fun setEnabled(enabled: Boolean) {}

    override fun isEnabled(): Boolean {
        return width!=0
    }

    private const val ChangeDrawerWidth_width = "ChangeDrawerWidth_width"

    var width: Int
        get() {
            return ConfigManager.getDefaultConfig().getIntOrDefault(ChangeDrawerWidth_width, 0)
        }
        set(value) {
            ConfigManager.getDefaultConfig().apply { putInt(ChangeDrawerWidth_width, value); save() }
        }

    fun getMaxWidth(context: Context): Float {
        val dm = DisplayMetrics()
        val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(dm)
        return (dm.widthPixels / dm.density)
    }

    override val condition: Boolean
        get() = true
}
