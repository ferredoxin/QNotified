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
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import me.ketal.base.PluginDelayableHook
import me.ketal.util.HookUtil.getField
import me.ketal.util.HookUtil.getMethod
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.qn_kernel.tlb.净化_扩展
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.util.QQVersion
import nil.nadph.qnotified.util.ReflexUtil.getFirstByType
import nil.nadph.qnotified.util.Utils
import xyz.nextalone.util.clazz
import xyz.nextalone.util.hookAfter
import xyz.nextalone.util.tryOrFalse

@FunctionEntry
@UiItem
object QWalletNoAD : PluginDelayableHook("ketal_qwallet_noad") {
    override val preference = uiSwitchPreference {
        title = "隐藏QQ钱包超值精选"
    }
    override val preferenceLocate = 净化_扩展

    override val pluginID = "qwallet_plugin.apk"

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

    override fun startHook(classLoader: ClassLoader) = tryOrFalse {
        arrayOf("Lcom/qwallet/activity/QWalletHomeActivity;->onCreate(Landroid/os/Bundle;)V", "Lcom/qwallet/activity/QvipPayWalletActivity;->onCreate(Landroid/os/Bundle;)V").getMethod(classLoader)
            ?.hookAfter(this) {
                val ctx = it.thisObject as Activity
                val id = ctx.resources.getIdentifier("root", "id", Utils.PACKAGE_NAME_QQ)
                val rootView = ctx.findViewById<ViewGroup>(id)
                val midView = rootView.getChildAt(rootView.childCount - 1)
                if (!requireMinQQVersion(QQVersion.QQ_8_8_17)) {
                    rootView.removeView(midView)
                }
                val headerView =
                    "Lcom/qwallet/view/QWalletHeaderViewRootLayout;->a:Lcom/qwallet/view/QWalletHeaderView;"
                        .getField(classLoader)
                        ?.get(rootView) as ViewGroup
                headerView.viewTreeObserver.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        val webView =
                            getFirstByType(headerView,
                                "com.tencent.biz.ui.TouchWebView".clazz) as? View
                                ?: return
                        headerView.removeView(webView)
                        headerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
    }
}
