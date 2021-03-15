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
package me.ketal.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import androidx.core.view.plusAssign
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.setActionButtonEnabled
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import com.tencent.mobileqq.widget.BounceScrollView
import me.ketal.hook.LeftSwipeReplyHook
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat
import nil.nadph.qnotified.ui.CommonContextWrapper
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.ui.ViewBuilder

@SuppressLint("Registered")
class ModifyLeftSwipeReplyActivity : IphoneTitleBarActivityCompat() {
    override fun doOnCreate(bundle: Bundle?): Boolean {
        super.doOnCreate(bundle)
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            val hook = LeftSwipeReplyHook
            val ctx = CommonContextWrapper.createMaterialDesignContext(this@ModifyLeftSwipeReplyActivity)
            this += ViewBuilder.newListItemHookSwitchInit(ctx, "总开关", "打开后才可使用以下功能", hook)
            this += ViewBuilder.newListItemSwitch(ctx, "取消消息左滑动作", "取消取消，一定要取消", hook.isNoAction
            ) { _: CompoundButton?, on: Boolean -> hook.isNoAction = on }
            this += ViewBuilder.newListItemButton(ctx, "修改左滑消息灵敏度", "妈妈再也不用担心我误触了", null) {
                val dialog = MaterialDialog(ctx).show {
                    title(text = "输入响应消息左滑的距离")
                    input(prefill = hook.replyDistance.toString(), waitForPositiveButton = false) { dialog, text ->
                        val inputField = dialog.getInputField()
                        val isValid = try {
                            text.toString().toInt()
                            true
                        } catch (e : NumberFormatException) {
                            false
                        }
                        inputField.error = if (isValid) null else "请输入有效的数据"
                        dialog.setActionButtonEnabled(WhichButton.POSITIVE, isValid)
                    }
                    positiveButton(text = "确定") {
                        val text = it.getInputField().text.toString()
                        hook.replyDistance = text.toInt()
                        it.dismiss()
                    }
                    negativeButton(text = "取消")
                }
                dialog.getInputLayout().addView(ViewBuilder.subtitle(ctx,
                    "若显示为-1，代表为初始化，请先在消息界面使用一次消息左滑回复，即可获得初始阈值。\n当你修改出错时，输入一个小于0的值，即可使用默认值"), 0)
            }
            this += ViewBuilder.newListItemSwitch(ctx, "左滑多选消息", "娱乐功能，用途未知", hook.isMultiChose
            ) { _: CompoundButton?, on: Boolean -> hook.isMultiChose = on }
        }
        BounceScrollView(this, null).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            addView(ll, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            setContentView(this)
        }
        setContentBackgroundDrawable(ResUtils.skin_background)
        title = "修改消息左滑动作"
        return true
    }
}
