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

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.core.view.plusAssign
import com.tencent.mobileqq.app.BaseActivity
import ltd.nextalone.util.*
import me.ketal.util.findViewByType
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.ReflexUtil.getFirstByType

@FunctionEntry
object HideSearch : CommonDelayableHook("Ketal_HideSearch"){
    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_6_0)

    override fun initOnce() = tryOrFalse {
        copeConversation()
        copeContacts()
        copeLeba()
    }

    //处理首页
    private fun copeConversation() {
        "Lcom/tencent/mobileqq/activity/home/Conversation;->c()V"
            .method.hookAfter(this) {
                val relativeLayout = it.thisObject.get("b", RelativeLayout::class.java)
                relativeLayout?.isVisible = false
                //隐藏顶栏
                val list = getFirstByType(it.thisObject, "com.tencent.mobileqq.fpsreport.FPSSwipListView".clazz) as View
                val searchView = it.thisObject.get("b", View::class.java)!!
                list.invoke("removeHeaderView", searchView, View::class.java)
                //移除消息列表搜索框
                val parent = relativeLayout?.parent as ViewGroup
                val toolbar = RelativeLayout(parent.context)
                toolbar.layoutParams = relativeLayout.layoutParams
                parent += toolbar
                //顶栏添加toolbar
                addButton(toolbar, searchView as RelativeLayout)
                //添加按钮
            }
    }

    private fun addButton(toolbar: RelativeLayout, searchView: ViewGroup) {
        val ctx = toolbar.context
        val density = toolbar.resources.displayMetrics.density
        val widthPixels = toolbar.resources.displayMetrics.widthPixels
        val w = (30.0f * density + 0.5f).toInt()
        val plusButton = ImageView(ctx)
        val layoutParams = RelativeLayout.LayoutParams(w, w)
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL)
        layoutParams.rightMargin = 60
        plusButton.layoutParams = layoutParams
        plusButton.setImageResource(ctx.hostDrawable("header_btn_add")!!)
        plusButton.setOnClickListener {
            tryOrFalse {
                val n2 = (widthPixels - (180.0f * density + 0.5f) - (6.0f * density + 0.5f)).toInt()
                val n3 = (density * 1.0f + 0.5f).toInt()
                val popBar = "com.tencent.mobileqq.activity.recent.RecentOptPopBar".clazz
                    ?.getConstructor(BaseActivity::class.java)?.newInstance(ctx)
                popBar?.invoke("a", toolbar, n2, n3, View::class.java, Int::class.java, Int::class.java)
            }
        }
        toolbar += plusButton
        val searchButton = ImageView(ctx)
        val layoutParams2 = RelativeLayout.LayoutParams(w, w)
        layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL)
        layoutParams2.rightMargin = 60 + w +30
        searchButton.layoutParams = layoutParams2
        searchButton.setImageResource(ctx.hostDrawable("qb_group_menu_search_group")!!)
        searchButton.setColorFilter(Color.WHITE)
        searchButton.setOnClickListener {
            searchView.findViewByType(EditText::class.java)?.performClick()
        }
        toolbar += searchButton
    }

    //处理联系人页
    private fun copeContacts() {
        "Lcom/tencent/mobileqq/activity/contacts/base/Contacts;->l()V"
            .method.hookAfter(this) {
                val searchView = getFirstByType(it.thisObject, "com.tencent.mobileqq.activity.contacts.base.SearchBarAssistant".clazz)
                    .get("a", EditText::class.java)?.parent as View
                searchView.hide()
            }
    }

    //处理动态页
    private fun  copeLeba() {
        "Lcom/tencent/mobileqq/leba/business/mainbiz/LebaSearchPart;->a(Landroid/view/View;)V"
            .method.hookAfter(this) {
                val searchView = getFirstByType(it.thisObject, RelativeLayout::class.java)
                searchView.hide()
            }
    }
}
