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
import android.os.Parcelable
import android.view.View
import android.widget.EditText
import cc.ioctl.hook.InputButtonHook
import mqq.app.AppRuntime
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.Step

abstract class BaseInputButtonDecorator @JvmOverloads constructor(keyName: String, targetProcess: Int = SyncUtils.PROC_MAIN, defEnabled: Boolean = false, vararg preconditions: Step = emptyArray()) : CommonDelayableHook(keyName, targetProcess, defEnabled, *preconditions) {

    abstract fun decorate(text: String, session: Parcelable, input: EditText, sendBtn: View, ctx1: Context, qqApp: AppRuntime): Boolean

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        InputButtonHook.INSTANCE.isEnabled = enabled
    }


}
