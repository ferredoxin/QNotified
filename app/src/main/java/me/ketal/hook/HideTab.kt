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

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TabHost
import android.widget.TextView
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.plusAssign
import ltd.nextalone.hook.SimplifyBottomQzone
import ltd.nextalone.util.*
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.ReflexUtil.getFirstByType
import java.util.ArrayList

@SuppressLint("StaticFieldLeak")
@FunctionEntry
object HideTab : CommonDelayableHook("ketal_HideTab") {
    private lateinit var tab: TabHost

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_6_0)

    override fun initOnce() = tryOrFalse {
        "com.tencent.mobileqq.activity.home.impl.TabFrameControllerImpl".clazz?.method("addFrame")
            ?.hookAfter(this) {
                val frame = it.args[0]
                tab = getFirstByType(frame, "com.tencent.mobileqq.widget.QQTabHost".clazz) as TabHost
                val blur = tab.findViewByType("com.tencent.mobileqq.widget.QQBlurView".clazz!!) as View
                tab.tabWidget.isVisible = !isEnabled
                blur.hide()
            }
        "com.tencent.mobileqq.activity.QQSettingMe".clazz?.hookAfterAllConstructors {
            if (!isEnabled) return@hookAfterAllConstructors
            val linearLayout = if (requireMinQQVersion(QQVersion.QQ_8_6_5)) {
                it.thisObject.get("c", LinearLayout::class.java)!!
            } else {
                val midcontentName = if (requireMinQQVersion(QQVersion.QQ_8_6_0)) "n" else "k"
                it.thisObject.get(midcontentName, View::class.java) as LinearLayout
            }
            addSettingItem(linearLayout, "skin_tab_icon_conversation_normal", "消息") {
                tab.currentTab = 0
            }
            addSettingItem(linearLayout, "skin_tab_icon_contact_normal", "联系人") {
                tab.currentTab = 1
            }
            if (!SimplifyBottomQzone.isEnabled) {
                addSettingItem(linearLayout, "skin_tab_icon_plugin_normal", "动态") {
                    val size = (tab.get("mTabSpecs") as ArrayList<*>).size
                    tab.currentTab = if (size == 3) 2 else 3
                }
            }
        }
    }

    private fun addSettingItem(linearLayout: LinearLayout, resName: String, label: String, clickListener: View.OnClickListener) {
        val ctx = linearLayout.context
        val view = View.inflate(ctx, ctx.hostLayout("b2g")!!, null) as LinearLayout
        val imgView = view[0] as ImageView
        val textView = view[1] as TextView
        imgView.setImageResource(ctx.hostDrawable(resName)!!)
        textView.text = label
        view.setOnClickListener(clickListener)
        linearLayout += view
    }
}

