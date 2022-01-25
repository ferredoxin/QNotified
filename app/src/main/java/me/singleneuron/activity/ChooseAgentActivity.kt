/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.*
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.singleneuron.base.AbstractChooseActivity
import nil.nadph.qnotified.R

class ChooseAgentActivity : AbstractChooseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.noDisplay)
        super.onCreate(savedInstanceState)
        bundle = intent.extras
        val intent = if (intent.getBooleanExtra("use_ACTION_PICK", false))
            Intent(ACTION_PICK).apply {
                type = "image/*"
                putExtra(EXTRA_ALLOW_MULTIPLE, false)
            }
        else Intent(ACTION_GET_CONTENT).apply {
            type = intent.type ?: "*/*"
            putExtra(EXTRA_ALLOW_MULTIPLE, false)
        }
        startActivityForResult(intent, REQUEST_CODE)
    }

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
                    val intent = Intent().apply {
                        component = ComponentName(
                            "com.tencent.mobileqq",
                            "com.tencent.mobileqq.activity.SplashActivity"
                        )
                        flags = 0x14000000
                        if (data != null) {
                            putExtras(data)
                        }
                        putExtra("open_chatfragment", true)
                        putExtra("isBack2Root", true)
                        putExtra("forward_from_jump", true)
                        putExtra("preAct", "JumpActivity")
                        putExtra("miniAppShareFrom", 0)
                        putExtra("system_share", true)
                        putExtra("leftBackText", "消息")
                        putExtra("task_launched_for_result", true)
                        putExtra("isFromShare", true)
                        putExtra("needShareCallBack", false)
                        putExtra("key_forward_ability_type", 0)
                        putExtra("moInputType", 2)
                        putExtra("chooseFriendFrom", 1)
                        putExtra("forward_source_business_type", -1)
                        if (intent.type == "image/*") {
                            putExtra("forward_type", 1)
                        } else {
                            putExtra("forward_type", 0)
                        }
                        bundle?.let {
                            val uin = it.getString("targetUin")
                            if (uin != null) {
                                putExtra("uin", uin)
                            }
                            val type = it.getInt("peerType", -1)
                            if (type != -1) {
                                putExtra("uintype", type)
                            }
                            putExtras(it)
                        }
                        putExtra("selection_mode", 2)
                    }
                    initSendCacheDir()
                    if (data != null) {
                        val uri = data.data
                        if (uri != null) {
                            convertUriToPath(uri)?.let {
                                intent.putExtra("forward_filepath", it)
                            }
                            intent.putExtra("sendMultiple", false)
                        }
                    }
                    startActivity(intent)
                }
                finish()
            }
        }

    }

}
