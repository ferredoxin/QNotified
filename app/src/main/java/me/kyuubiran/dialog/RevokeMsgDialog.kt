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

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.kyuubiran.hook.RevokeMsg
import me.kyuubiran.util.getDefaultCfg
import nil.nadph.qnotified.R
import nil.nadph.qnotified.databinding.KyuubiranRevokeMsgDialogBinding
import nil.nadph.qnotified.ui.CommonContextWrapper

object RevokeMsgDialog {
    private var currentEnable: Boolean? = null
    private var currentMsgEnable: Boolean? = null
    private var currentRevokeTips = ""
    private var currentUnreceivedTips = ""

    fun onShow(baseContext: Context) {
        val ctx = CommonContextWrapper.createMaterialDesignContext(baseContext)
        val binding = KyuubiranRevokeMsgDialogBinding.inflate(LayoutInflater.from(ctx))
        val cfg = getDefaultCfg()
        val enable = cfg.getBooleanOrFalse(RevokeMsg.kr_revoke_msg)
        val showMsgEnable = cfg.getBooleanOrFalse(RevokeMsg.kr_revoke_msg_show_msg_text_enabled)
        val revokedMsgTips = cfg.getStringOrDefault(RevokeMsg.kr_revoke_msg_tips_text, "尝试撤回一条消息")
        val unreceivedRevokedMsgTips =
            cfg.getStringOrDefault(RevokeMsg.kr_revoke_unreceived_msg_tips_text, "撤回了一条消息(没收到)")

        currentEnable = enable
        currentMsgEnable = showMsgEnable

        val mDialog = MaterialAlertDialogBuilder(ctx, R.style.MaterialDialog)

        binding.krRevokeMsgEnabled.isChecked = enable
        binding.krRevokeMsgPanel.visibility = if (enable) View.VISIBLE else View.GONE
        binding.krRevokeMsgShowMsgTextEnabled.isChecked = showMsgEnable
        if (binding.krRevokedMsgTipsText.text.isEmpty()) binding.krRevokedMsgTipsText.setText(
            revokedMsgTips
        )
        if (binding.krUnreceivedRevokedMsgTipsText.text.isEmpty()) binding.krUnreceivedRevokedMsgTipsText.setText(
            unreceivedRevokedMsgTips
        )
        if (currentRevokeTips.isEmpty()) currentRevokeTips = revokedMsgTips
        if (currentUnreceivedTips.isEmpty()) currentUnreceivedTips = unreceivedRevokedMsgTips

        binding.krRevokeMsgEnabled.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            binding.krRevokeMsgPanel.visibility = if (b) View.VISIBLE else View.GONE
            currentEnable = b
        }

        binding.krRevokeMsgShowMsgTextEnabled.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            currentMsgEnable = b
        }

        binding.krRevokedMsgTipsText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentRevokeTips = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
        binding.krUnreceivedRevokedMsgTipsText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentUnreceivedTips = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
        })

        mDialog.setView(binding.root)
        mDialog.setPositiveButton("保存") { _, _ -> save() }
        mDialog.setNegativeButton("取消") { _, _ -> }
        mDialog.setCancelable(false)
        mDialog.show()
    }

    fun save() {
        val cfg = getDefaultCfg()
        currentEnable?.let { cfg.setBooleanConfig(RevokeMsg.kr_revoke_msg, it) }
        currentMsgEnable?.let {
            cfg.setBooleanConfig(
                RevokeMsg.kr_revoke_msg_show_msg_text_enabled,
                it
            )
        }
        cfg.setStringConfig(RevokeMsg.kr_revoke_msg_tips_text, currentRevokeTips)
        cfg.setStringConfig(RevokeMsg.kr_revoke_unreceived_msg_tips_text, currentUnreceivedTips)
        cfg.save()
    }
}
