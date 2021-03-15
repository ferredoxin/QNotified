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
package ltd.nextalone.hook

import android.app.Activity
import android.view.View
import android.widget.CheckBox
import ltd.nextalone.util.findHostView
import ltd.nextalone.util.hookAfter
import ltd.nextalone.util.method
import ltd.nextalone.util.tryOrFalse
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook

@FunctionEntry
object AutoSendOriginalPhoto :
    CommonDelayableHook("na_auto_send_origin_photo", SyncUtils.PROC_MAIN or SyncUtils.PROC_PEAK) {

    override fun initOnce() = tryOrFalse {
        "Lcom.tencent.mobileqq.activity.aio.photo.PhotoListPanel;->a(Z)V".method.hookAfter(this) {
            val ctx = it.thisObject as View
            val sendOriginPhotoCheckbox = ctx.findHostView<CheckBox>("h1y")
            sendOriginPhotoCheckbox?.isChecked = true
        }
        if (requireMinQQVersion(QQVersion.QQ_8_2_0)) {
            "Lcom.tencent.mobileqq.activity.photo.album.NewPhotoPreviewActivity;->onCreate(Landroid/os/Bundle;)V".method.hookAfter(
                this
            ) {
                val ctx = it.thisObject as Activity
                val checkBox = ctx.findHostView<CheckBox>("h1y")
                checkBox?.isChecked = true
            }
        }
    }
}
