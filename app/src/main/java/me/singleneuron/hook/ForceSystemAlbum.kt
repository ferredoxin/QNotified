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

import android.content.Intent
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.singleneuron.activity.ChooseAlbumAgentActivity
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.ui.base.UiDescription
import nil.nadph.qnotified.base.annotation.FunctionEntry

@FunctionEntry
@UiItem
object ForceSystemAlbum : CommonDelayAbleHookBridge("forceSystemAlbum") {

    override fun initOnce(): Boolean {
        //特征字符串:"onAlbumBtnClicked"
        val photoListPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")
        XposedBridge.hookAllMethods(photoListPanelClass, "e", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val context = hostInfo.application
                val intent = Intent(context, ChooseAlbumAgentActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                param!!.result = null
            }
        })
        return true
    }

    override val preference: UiDescription = uiSwitchPreference {
        title = "强制使用系统相机"
        summary = "支持8.3.6及更高"
    }

    override val preferenceLocate: Array<String> = arrayOf("增强功能")

}
