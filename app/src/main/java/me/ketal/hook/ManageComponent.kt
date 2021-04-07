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

import android.app.AlertDialog
import android.content.ComponentName
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import me.ketal.ui.activity.QFileShareToIpadActivity.Companion.ENABLE_SEND_TO_IPAD
import me.ketal.ui.activity.QFileShareToIpadActivity.Companion.ENABLE_SEND_TO_IPAD_STATUS
import me.ketal.ui.activity.QFileShareToIpadActivity.Companion.SEND_TO_IPAD_CMD
import me.ketal.util.getEnable
import me.ketal.util.setEnable
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.BuildConfig
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.startup.HookEntry
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Toasts

object ManageComponent : CommonDelayableHook("Ketal_ManageComponent") {
    override fun initOnce() = true
    private val components = mapOf(
        "发送到我的电脑" to ComponentName(hostInfo.packageName, "com.tencent.mobileqq.activity.qfileJumpActivity"),
        "保存到QQ收藏" to ComponentName(hostInfo.packageName, "cooperation.qqfav.widget.QfavJumpActivity"),
        "面对面快传" to ComponentName(hostInfo.packageName, "cooperation.qlink.QlinkShareJumpActivity"),
        "发送到我的iPad" to ComponentName(HookEntry.PACKAGE_NAME_SELF, "me.ketal.ui.activity.QFileShareToIpadActivity")
    )

    val listener = View.OnClickListener {
        val ctx = it.context
        val keys = components.keys.toTypedArray()
        val enable = keys.run {
            val ary = BooleanArray(size)
            forEachIndexed { i, k ->
                ary[i] = components[k]?.getEnable(ctx) == true
            }
            ary
        }
        val cache = enable.clone()
        AlertDialog.Builder(ctx, CustomDialog.themeIdForDialog())
            .setTitle("选择要启用的组件")
            .setMultiChoiceItems(keys, enable) { _: DialogInterface, i: Int, _: Boolean ->
                cache[i] = !cache[i]
            }
            .setNegativeButton("取消", null)
            .setPositiveButton("确定") { _: DialogInterface, _: Int ->
                cache.forEachIndexed { i, b ->
                    if (!cache[keys.indexOf("发送到我的电脑")] && cache[keys.indexOf("发送到我的iPad")]) {
                        Toasts.error(ctx, "启用发送到iPad需启用发送到电脑")
                        it.performClick()
                        return@setPositiveButton
                    }
                    val k = keys[i]
                    if (k == "发送到我的iPad" && b != components[k]?.getEnable(ctx)) {
                        val intent = Intent().apply {
                            component = ComponentName(BuildConfig.APPLICATION_ID, "nil.nadph.qnotified.activity.ConfigV2Activity")
                            putExtra(SEND_TO_IPAD_CMD, ENABLE_SEND_TO_IPAD)
                            putExtra(ENABLE_SEND_TO_IPAD_STATUS, b)
                        }
                        Toasts.info(ctx, "已保存组件状态")
                        ctx.startActivity(intent)
                        return@setPositiveButton
                    }
                    components[k]?.setEnable(ctx, b)
                }
                Toasts.info(ctx, "已保存组件状态")
            }
            .show()
    }
}
