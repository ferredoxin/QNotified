package me.singleneuron.activity

import android.R
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nil.nadph.qnotified.HookEntry
import nil.nadph.qnotified.MainHook

class QQPurifyAgentActivity :AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var pkg: String? = null
        pkg = HookEntry.PACKAGE_NAME_QQ
        val intent = Intent()
        intent.component = ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity")
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(MainHook.JUMP_ACTION_CMD, MainHook.JUMP_ACTION_START_ACTIVITY)
        intent.putExtra(MainHook.JUMP_ACTION_TARGET, "me.zpp0196.qqpurify.activity.MainActivity")
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this).setTitle("出错啦")
                    .setMessage("拉起模块设置失败, 请确认 $pkg 已安装并启用(没有被关冰箱或被冻结停用)\n$e")
                    .setCancelable(true).setPositiveButton(R.string.ok, null).show()
        }
        //finish()
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

}