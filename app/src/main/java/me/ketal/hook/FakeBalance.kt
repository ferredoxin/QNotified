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

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.widget.doAfterTextChanged
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.input.input
import ltd.nextalone.util.clazz
import ltd.nextalone.util.hookAfter
import me.ketal.base.PluginDelayableHook
import me.ketal.data.ConfigData
import me.ketal.ui.view.ConfigView
import me.ketal.util.BaseUtil.tryVerbosely
import me.ketal.util.HookUtil.findClass
import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.R
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.ui.CommonContextWrapper
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.ReflexUtil

@FunctionEntry
object FakeBalance : PluginDelayableHook("ketal_qwallet_fakebalance") {
    override val pluginID = "qwallet_plugin.apk"
    private val moneyKey = ConfigData<String>("ketal_qwallet_fakebalance_money")
    private var money
        get() = moneyKey.getOrDefault("114514")
        set(value) {
            moneyKey.value = value
        }

    fun listener() = View.OnClickListener {
        showDialog(it.context, null)
    }

    private fun showDialog(ctx: Context, textView: TextView?) {
        tryVerbosely(false) {
            val context = CommonContextWrapper(ctx, if (ResUtils.isInNightMode()) R.style.Theme_MaiTungTMDesignNight else R.style.Theme_MaiTungTMDesign)
            val vg = ConfigView(context)
            vg.setText("启用自定义钱包余额")
            val dialog = MaterialDialog(context).show {
                title(text = "自定义钱包余额")
                input(hint = "请输入自定义金额...", prefill = money) { dialog, text ->
                    val enableFake = vg.isChecked
                    isEnabled = enableFake
                    if (enableFake) {
                        money = text.toString()
                        if (!isInited) init()
                    }
                    dialog.dismiss()
                    textView?.text = "114514"
                }
                positiveButton(text = "保存")
                negativeButton(text = "取消")
            }
            vg.view = dialog.getCustomView()
            vg.isVisible = isEnabled
            vg.isChecked = isEnabled
            dialog.view.contentLayout.customView = null
            dialog.customView(view = vg)
        }
    }

    override fun startHook(classLoader: ClassLoader) = tryVerbosely(false) {
        arrayOf(
            "Lcom/qwallet/activity/QWalletHomeActivity;->onCreate(Landroid/os/Bundle;)V",
            "Lcom/qwallet/activity/QvipPayWalletActivity;->onCreate(Landroid/os/Bundle;)V"
        ).getMethod(classLoader)
            ?.hookAfter(this) {
                val ctx = it.thisObject as Activity
                val id = ctx.resources.getIdentifier("root", "id", hostInfo.packageName)
                val rootView = ctx.findViewById<ViewGroup>(id)
                val headerClass = "com.qwallet.view.QWalletHeaderView".findClass(classLoader)
                val headerView = ReflexUtil.getFirstByType(rootView, headerClass)
                val numAnimClass = "com.tencent.mobileqq.activity.qwallet.widget.NumAnim".clazz
                    ?: "com.tencent.mobileqq.qwallet.widget.NumAnim".clazz
                for (f in headerClass.declaredFields) {
                    if (f.type == numAnimClass) {
                        f.isAccessible = true
                        val numAnim = f.get(headerView)
                        val tv = ReflexUtil.getFirstByType(numAnim, TextView::class.java)
                        tv.doAfterTextChanged { v ->
                            if (isEnabled && v.toString() != money)
                                tv.text = money
                        }
                        tv.setOnLongClickListener { v ->
                            showDialog(v.context, tv)
                            true
                        }
                    }
                }
            }
        true
    }
}
