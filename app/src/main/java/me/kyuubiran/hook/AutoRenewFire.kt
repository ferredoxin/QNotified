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
package me.kyuubiran.hook

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.TextView
import xyz.nextalone.util.*
import me.kyuubiran.dialog.AutoRenewFireDialog
import me.kyuubiran.util.AutoRenewFireMgr
import me.kyuubiran.util.getExFriendCfg
import me.kyuubiran.util.showToastByTencent
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.ReflexUtil.invoke_virtual
import nil.nadph.qnotified.util.ReflexUtil.new_instance

//自动续火
@FunctionEntry
object AutoRenewFire : CommonDelayableHook("kr_auto_renew_fire") {
    var autoRenewFireStarted = false

    override fun initOnce(): Boolean {
        if (!autoRenewFireStarted) {
            AutoRenewFireMgr.doAutoSend()
            autoRenewFireStarted = true
        }
        return tryOrFalse {
            val FormSimpleItem = "com.tencent.mobileqq.widget.FormSwitchItem".clazz

            "com.tencent.mobileqq.activity.ChatSettingActivity".clazz?.method("doOnCreate")?.hookAfter(this) {

                //如果未启用 不显示按钮
                if (!getExFriendCfg().getBooleanOrFalse("kr_auto_renew_fire")) return@hookAfter
                //获取 设为置顶 SwitchItem
                val setToTopItem = it.thisObject.getAll(FormSimpleItem)?.first { item ->
                    item.get(TextView::class.java)?.text?.contains("置顶") ?: false
                }
                //如果SwitchItem不为空 说明为好友
                if (setToTopItem != null) {
                    //创建SwitchItem对象
                    val autoRenewFireItem =
                        new_instance(
                            FormSimpleItem,
                            it.thisObject,
                            Context::class.java
                        )
                    //拿到ViewGroup
                    val listView = (setToTopItem as View).parent as ViewGroup
                    //设置开关文本
                    invoke_virtual(
                        autoRenewFireItem,
                        "setText",
                        "自动续火",
                        CharSequence::class.java
                    )
                    //添加View
                    listView.addView(autoRenewFireItem as View, 7)
                    //拿到好友相关信息
                    val intent =it.thisObject.get(Intent::class.java)
                    //QQ
                    val uin = intent?.getStringExtra("uin")
                    //昵称
                    val uinName = intent?.getStringExtra("uinname")
                    //设置按钮是否启用
                    invoke_virtual(
                        autoRenewFireItem,
                        "setChecked",
                        AutoRenewFireMgr.hasEnabled(uin),
                        Boolean::class.java
                    )
                    //设置监听事件
                    invoke_virtual(
                        autoRenewFireItem,
                        "setOnCheckedChangeListener",
                        object : CompoundButton.OnCheckedChangeListener {
                            override fun onCheckedChanged(
                                p0: CompoundButton?,
                                p1: Boolean
                            ) {
                                if (p1) {
                                    AutoRenewFireMgr.add(uin)
                                    (it.thisObject as Context).showToastByTencent("已开启与${uinName}的自动续火")
                                } else {
                                    AutoRenewFireMgr.remove(uin)
                                    (it.thisObject as Context).showToastByTencent("已关闭与${uinName}的自动续火")
                                }
                            }
                        },
                        CompoundButton.OnCheckedChangeListener::class.java
                    )
                    if (LicenseStatus.isInsider()) {
                        autoRenewFireItem.setOnLongClickListener { _ ->
                            AutoRenewFireDialog.showSetMsgDialog(
                                it.thisObject as Context,
                                uin
                            )
                            true
                        }
                    }
                }
            }
        }
    }


    override fun isEnabled(): Boolean {
        return try {
            getExFriendCfg().getBooleanOrDefault("kr_auto_renew_fire", false)
        } catch (e: Exception) {
            false
        }
    }
}
