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
package me.kyuubiran.dialog

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.kyuubiran.util.AutoRenewFireMgr
import me.kyuubiran.util.getExFriendCfg
import me.kyuubiran.util.showToastByTencent
import nil.nadph.qnotified.R
import nil.nadph.qnotified.databinding.KyuubiranAutoRenewFireBinding
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

object AutoRenewFireDialog {
    private var currentEnable: Boolean? = null
    private lateinit var binding: KyuubiranAutoRenewFireBinding
    var replyMsg: String = getExFriendCfg().getStringOrDefault(AutoRenewFireMgr.MESSAGE, "[续火]")

    fun showMainDialog(context: Context) {
        val dialog = CustomDialog.createFailsafe(context)
        val ctx = dialog.context
        val enable = getExFriendCfg().getBooleanOrFalse(AutoRenewFireMgr.ENABLE)
        currentEnable = enable
        val editText = EditText(ctx)
        editText.textSize = 16f
        editText.hint = "续火消息内容（不可更改)"
        val _5 = Utils.dip2px(context, 5f)
        editText.setPadding(_5, _5, _5, _5 * 2)
        editText.setText(replyMsg)
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (LicenseStatus.isInsider()) replyMsg = s.toString()
            }

            override fun afterTextChanged(s: Editable?) =
                Unit
        })
        val checkBox = CheckBox(ctx)
        checkBox.text = "开启自动续火"
        checkBox.isChecked = enable
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentEnable = isChecked
            when (isChecked) {
                true -> Toasts.showToast(ctx, Toasts.TYPE_INFO, "已开启自动续火", Toasts.LENGTH_SHORT)
                false -> Toasts.showToast(ctx, Toasts.TYPE_INFO, "已关闭自动续火", Toasts.LENGTH_SHORT)
            }
        }
        val linearLayout = LinearLayout(ctx)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(ViewBuilder.subtitle(context,
            "说明:启用后将会在每天0点之后给对方发一条消息。\n此处开关为总开关，请单独在好友的设置页面打开自动续火开关。\n无论你是否给TA发过消息，本功能都会发送续火消息。\n如果你在续火消息发送前添加了好友，那么之后将会发送给这个好友。\n如果今天已经发送过续火消息了，则再添加好友并不会发送续火消息。"))
        linearLayout.addView(checkBox, ViewBuilder.newLinearLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, _5 * 2))
        linearLayout.addView(editText, ViewBuilder.newLinearLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, _5 * 2))
        val alertDialog = dialog.setTitle("自动续火设置")
            .setView(linearLayout)
            .setCancelable(true)
            .setPositiveButton("确认") { _, _ ->
                if (replyMsg == "") {
                    Toasts.showToast(context, Utils.TOAST_TYPE_ERROR, "请输入自动续火内容", Toast.LENGTH_SHORT)
                } else {
                    save()
                    Toasts.showToast(context, Utils.TOAST_TYPE_INFO, "设置已保存", Toast.LENGTH_SHORT)
                    dialog.dismiss()
                }
            }.setNeutralButton("使用默认值") { _, _ ->
                replyMsg = "[续火]"
                save()
            }
            .setNegativeButton("取消", null)
            .create() as AlertDialog
        alertDialog.show()
    }

    fun showSettingsDialog(ctx: Context) {
        MaterialAlertDialogBuilder(ctx, R.style.MaterialDialog)
            .setTitle("自动续火设置")
            .setItems(arrayOf("重置列表", "重置时间")) { _, i ->
                when (i) {
                    0 -> {
                        AutoRenewFireMgr.resetList()
                        ctx.showToastByTencent("已清空自动续火列表")
                    }
                    1 -> {
                        AutoRenewFireMgr.resetTime()
                        ctx.showToastByTencent("已重置下次续火时间")
                    }
                }
            }
            .create()
            .show()
    }

    fun showSetMsgDialog(context: Context, uin: String?) {
        if (uin == null || uin.isEmpty() || !AutoRenewFireMgr.hasEnabled(uin)) return
        val dialog = CustomDialog.createFailsafe(context)
        val ctx = dialog.context
        val editText = EditText(ctx)
        editText.textSize = 16f
        editText.hint = "续火消息内容"
        val _5 = Utils.dip2px(context, 5f)
        editText.setPadding(_5, _5, _5, _5 * 2)
        editText.setText(AutoRenewFireMgr.getMsg(uin))
        val linearLayout = LinearLayout(ctx)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(editText, ViewBuilder.newLinearLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, _5 * 2))
        val alertDialog = dialog.setTitle("设置单独续火消息")
            .setView(linearLayout)
            .setCancelable(true)
            .setPositiveButton("确认") { _, _ ->
                Toasts.showToast(context, Utils.TOAST_TYPE_INFO, "设置自动续火消息成功", Toast.LENGTH_SHORT)
                dialog.dismiss()
                AutoRenewFireMgr.setMsg(uin, editText.text.toString())
            }.setNeutralButton("使用默认值") { _, _ ->
                AutoRenewFireMgr.setMsg(uin, "")
                Toasts.showToast(context, Utils.TOAST_TYPE_ERROR, "已使用默认值", Toast.LENGTH_SHORT)
            }
            .setNegativeButton("取消", null)
            .create() as AlertDialog
        alertDialog.show()
    }

    fun save() {
        val cfg = getExFriendCfg()
        currentEnable?.let { cfg.setBooleanConfig(AutoRenewFireMgr.ENABLE, it) }
        cfg.putString(AutoRenewFireMgr.MESSAGE, replyMsg)
        cfg.save()
    }
}
