package me.kyuubiran.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.LinearLayout
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.kyuubiran.hook.RevokeMsg
import me.kyuubiran.utils.getDefaultCfg
import nil.nadph.qnotified.R

object RevokeMsgDialog {
    private var currentEnable: Boolean? = null
    private var currentMsgEnable: Boolean? = null
    private var currentRevokeTips = ""
    private var currentUnreceivedTips = ""

    fun onShow(ctx: Context) {
        val cfg = getDefaultCfg()
        val enable = cfg.getBooleanOrFalse(RevokeMsg.kr_revoke_msg)
        val showMsgEnable = cfg.getBooleanOrFalse(RevokeMsg.kr_revoke_msg_show_msg_text_enabled)
        val revokedMsgTips = cfg.getStringOrDefault(RevokeMsg.kr_revoke_msg_tips_text, "尝试撤回一条消息")
        val unreceivedRevokedMsgTips = cfg.getStringOrDefault(RevokeMsg.kr_revoke_unreceived_msg_tips_text, "撤回了一条消息(没收到)")

        currentEnable = enable
        currentMsgEnable = showMsgEnable

        val mViewGroup = LayoutInflater.from(ctx).inflate(R.layout.kyuubiran_revoke_msg_dialog, null)
        val mEnable = mViewGroup.findViewById<CheckBox>(R.id.kr_revoke_msg_enabled)
        val mShowMsgEnable = mViewGroup.findViewById<CheckBox>(R.id.kr_revoke_msg_show_msg_text_enabled)
        val mPanel = mViewGroup.findViewById<LinearLayout>(R.id.kr_revoke_msg_panel)
        val mRevokedMsgTips = mViewGroup.findViewById<EditText>(R.id.kr_revoked_msg_tips_text)
        val mUnreceivedRevokedMsgTips = mViewGroup.findViewById<EditText>(R.id.kr_unreceived_revoked_msg_tips_text)
        val mDialog = MaterialAlertDialogBuilder(ctx, R.style.MaterialDialog)

        mEnable.isChecked = enable
        mPanel.visibility = if (enable) View.VISIBLE else View.GONE
        mShowMsgEnable.isChecked = showMsgEnable
        if (mRevokedMsgTips.text.isEmpty()) mRevokedMsgTips.setText(revokedMsgTips)
        if (mUnreceivedRevokedMsgTips.text.isEmpty()) mUnreceivedRevokedMsgTips.setText(unreceivedRevokedMsgTips)
        if (currentRevokeTips.isEmpty()) currentRevokeTips = revokedMsgTips
        if (currentUnreceivedTips.isEmpty()) currentUnreceivedTips = unreceivedRevokedMsgTips


        mEnable.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            mPanel.visibility = if (b) View.VISIBLE else View.GONE
            currentEnable = b
        }

        mShowMsgEnable.setOnCheckedChangeListener { _: CompoundButton, b: Boolean ->
            currentMsgEnable = b
        }

        mRevokedMsgTips.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                //empty
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentRevokeTips = s.toString()
            }
        })
        mUnreceivedRevokedMsgTips.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentUnreceivedTips = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                //empty
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //empty
            }
        })

        mDialog.setView(mViewGroup)
        mDialog.setPositiveButton("保存") { _, _ -> save() }
        mDialog.setNegativeButton("取消") { _, _ -> }
        mDialog.setCancelable(false)
        mDialog.show()
    }

    fun save() {
        val cfg = getDefaultCfg()
        currentEnable?.let { cfg.setBooleanConfig(RevokeMsg.kr_revoke_msg, it) }
        currentMsgEnable?.let { cfg.setBooleanConfig(RevokeMsg.kr_revoke_msg_show_msg_text_enabled, it) }
        cfg.setStringConfig(RevokeMsg.kr_revoke_msg_tips_text, currentRevokeTips)
        cfg.setStringConfig(RevokeMsg.kr_revoke_unreceived_msg_tips_text, currentUnreceivedTips)
        cfg.save()
    }
}