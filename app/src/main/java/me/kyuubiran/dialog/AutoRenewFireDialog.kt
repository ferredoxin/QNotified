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
package me.kyuubiran.dialog

import android.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.EditText
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.kyuubiran.util.AutoRenewFireMgr
import me.kyuubiran.util.getExFriendCfg
import me.kyuubiran.util.showToastByTencent
import nil.nadph.qnotified.R
import nil.nadph.qnotified.databinding.KyuubiranAutoRenewFireBinding
import nil.nadph.qnotified.util.LicenseStatus

object AutoRenewFireDialog {
    private var currentEnable: Boolean? = null
    private lateinit var binding: KyuubiranAutoRenewFireBinding
    var replyMsg: String = getExFriendCfg().getStringOrDefault(AutoRenewFireMgr.MESSAGE, "[续火]")

    fun showMainDialog(ctx: Context) {
        binding = KyuubiranAutoRenewFireBinding.inflate(LayoutInflater.from(ctx))
        val enable = getExFriendCfg().getBooleanOrFalse(AutoRenewFireMgr.ENABLE)
        currentEnable = enable

        val mDialog = MaterialAlertDialogBuilder(ctx, R.style.MaterialDialog)

        binding.autoRenewFireEnable.isChecked = enable
        binding.autoRenewFirePanel.visibility = if (enable) View.VISIBLE else View.GONE
        if (binding.autoRenewFireKeywordEt.text.isEmpty()) binding.autoRenewFireKeywordEt.setText(replyMsg)

        binding.autoRenewFireEnable.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            binding.autoRenewFirePanel.visibility = if (b) View.VISIBLE else View.GONE
            currentEnable = b
        }

        if (!LicenseStatus.isInsider()) {
            binding.autoRenewFireKeywordEt.filters = arrayOf(InputFilter.LengthFilter(4))
        }

        binding.autoRenewFireKeywordEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (LicenseStatus.isInsider()) replyMsg = s.toString()
            }

            override fun afterTextChanged(s: Editable?) =
                Unit
        })


        mDialog.setView(binding.root)
        mDialog.setPositiveButton("保存") { _, _ -> save() }
        mDialog.setNegativeButton("取消") { _, _ -> }
        mDialog.setCancelable(false)
        mDialog.show()
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

    fun showSetMsgDialog(ctx: Context, uin: String?) {
        if (uin == null || uin.isEmpty() || !AutoRenewFireMgr.hasEnabled(uin)) return
        val et = EditText(ctx)
        et.setText(AutoRenewFireMgr.getMsg(uin))
        et.setPadding(15, 15, 15, 15)
        AlertDialog.Builder(ctx, R.style.MaterialDialog)
            .setTitle("设置单独续火消息")
            .setView(et)
            .setPositiveButton("确定") { _, _ ->
                AutoRenewFireMgr.setMsg(uin, et.text.toString())
                ctx.showToastByTencent("设置自动续火消息成功!")
            }
            .setNegativeButton("取消") { _, _ -> }
            .create()
            .show()
    }

    fun save() {
        val cfg = getExFriendCfg()
        currentEnable?.let { cfg.setBooleanConfig(AutoRenewFireMgr.ENABLE, it) }
        cfg.putString(AutoRenewFireMgr.MESSAGE, replyMsg)
        cfg.save()
    }
}
