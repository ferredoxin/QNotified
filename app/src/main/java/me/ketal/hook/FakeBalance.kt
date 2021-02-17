/*
 * QNotified - An Xposed module for QQ/TIM
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

package me.ketal.hook

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import ltd.nextalone.util.hookAfter
import me.ketal.base.PluginDelayableHook
import me.ketal.util.HookUtil.getField
import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.R
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

object FakeBalance : PluginDelayableHook("ketal_qwallet_fakebalance") {
    override val pluginID = "qwallet_plugin.apk"
    private const val moneyKey = "ketal_qwallet_fakebalance_money"
    private var money
        get() = ConfigManager.getDefaultConfig().getStringOrDefault(moneyKey, "114514")
        set(value) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[moneyKey] = value
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(hostInfo.application, e.toString() + "")
            } else {
                SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
            }
        }
    }

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

    fun listener() = View.OnClickListener {
        try {
            val ctx = it.context
            var enableFake = isEnabled
            val vg = LayoutInflater.from(ctx).inflate(R.layout.rikka_select_splash_dialog, null) as LinearLayout
            val input = vg.findViewById<TextView>(R.id.selectSplash_editTextPicLocation)
            val enable = vg.findViewById<CheckBox>(R.id.checkBoxEnableCustomStartupPic)
            val panel = vg.findViewById<RelativeLayout>(R.id.layoutSplashPanel)
            enable.text = "启用自定义钱包余额"
            enable.setTextColor(Color.BLACK)
            enable.isChecked = enableFake
            enable.setOnCheckedChangeListener { _, isChecked ->
                enableFake = isChecked
                panel.visibility = if (enableFake) View.VISIBLE else View.GONE
            }
            panel.visibility = if (enableFake) View.VISIBLE else View.GONE
            input.text = money
            input.hint = "请输入自定义金额..."
            val dialog = CustomDialog.createFailsafe(it.context)
                .setTitle("自定义钱包余额")
                .setView(vg)
                .setNegativeButton("取消", null)
                .setPositiveButton("保存") { _: DialogInterface, _: Int ->
                    if (!enableFake) {
                        isEnabled = false
                    } else {
                        val inputMoney = input.text.toString()
                        if (inputMoney.isEmpty()) {
                            Toasts.error(ctx, "请输入金额")
                            return@setPositiveButton
                        }
                        isEnabled = true
                        money = inputMoney
                        if (enableFake && !this.isInited) {
                            this.init()
                        }
                    }
                }
                .create() as AlertDialog
            dialog.show()
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    override fun startHook(classLoader: ClassLoader) = try {
        "Lcom/qwallet/activity/QWalletHomeActivity;->onCreate(Landroid/os/Bundle;)V"
            .getMethod(classLoader)
            ?.hookAfter(this) {
                val ctx = it.thisObject as Activity
                val id = ctx.resources.getIdentifier("root", "id", Utils.PACKAGE_NAME_QQ)
                val rootView = ctx.findViewById<ViewGroup>(id)
                val headerView = "Lcom/qwallet/view/QWalletHeaderViewRootLayout;->a:Lcom/qwallet/view/QWalletHeaderView;"
                    .getField(classLoader)
                    ?.get(rootView)
                val tv = "Lcom/qwallet/view/QWalletHeaderView;->a:Landroid/widget/TextView;"
                    .getField(classLoader)
                    ?.get(headerView) as TextView
                tv.doAfterTextChanged {
                    if (this.isEnabled && it.toString() != money)
                        tv.text = money
                }
            }
        true
    } catch (e: Exception) {
        Utils.log(e)
        false
    }
}
