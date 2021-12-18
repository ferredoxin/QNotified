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
package me.singleneuron.activity

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nil.nadph.qnotified.R
import nil.nadph.qnotified.lifecycle.JumpActivityEntryHook
import nil.nadph.qnotified.startup.HookEntry

class QQPurifyAgentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MaiTungTMUI)
        super.onCreate(savedInstanceState)
        var pkg: String? = null
        pkg = HookEntry.PACKAGE_NAME_QQ
        val intent = Intent()
        intent.component = ComponentName(pkg, "com.tencent.mobileqq.activity.JumpActivity")
        intent.action = Intent.ACTION_VIEW
        intent.putExtra(
            JumpActivityEntryHook.JUMP_ACTION_CMD,
            JumpActivityEntryHook.JUMP_ACTION_START_ACTIVITY
        )
        intent.putExtra(
            JumpActivityEntryHook.JUMP_ACTION_TARGET, "me.zpp0196.qqpurify.activity.MainActivity"
        )
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            AlertDialog.Builder(this).setTitle("出错啦")
                .setMessage("拉起模块设置失败, 请确认 $pkg 已安装并启用(没有被关冰箱或被冻结停用)\n$e")
                .setCancelable(true).setPositiveButton(android.R.string.ok, null).show()
        }
    }

    override fun onStart() {
        super.onStart()
        setVisible(true)
    }

}
