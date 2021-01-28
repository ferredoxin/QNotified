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
package me.singleneuron.base.decorator

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import nil.nadph.qnotified.util.Utils

abstract class BaseStartActivityHookDecorator(cfg: String): BaseDecorator(cfg) {

    fun decorate(intent: Intent ,param: XC_MethodHook.MethodHookParam): Boolean {
        if (!checkEnabled()) return false
        return try {
            doDecorate(intent,param)
        } catch (e:Exception) {
            Utils.log(e)
            false
        }
    }

    protected abstract fun doDecorate(intent: Intent,param: XC_MethodHook.MethodHookParam): Boolean

}
