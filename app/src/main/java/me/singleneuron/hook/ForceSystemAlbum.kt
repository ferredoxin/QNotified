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

import android.content.Intent
import de.robv.android.xposed.XposedBridge
import me.singleneuron.activity.ChooseAlbumAgentActivity
import me.singleneuron.base.adapter.BaseDelayableConditionalHookAdapter
import me.singleneuron.qn_kernel.data.hostInformationProvider

object ForceSystemAlbum : BaseDelayableConditionalHookAdapter("forceSystemAlbum") {

    override fun doInit(): Boolean {
        //特征字符串:"onAlbumBtnClicked"
        val photoListPanelClass = Class.forName("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel")
        XposedBridge.hookAllMethods(photoListPanelClass,"e",object : XposedMethodHookAdapter() {
            override fun beforeMethod(param: MethodHookParam?) {
                val context = hostInformationProvider.applicationContext
                context.startActivity(Intent(context,ChooseAlbumAgentActivity::class.java))
                param!!.result = null
            }
        })
        return true
    }

}
