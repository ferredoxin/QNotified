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

package me.singleneuron.hook.decorator

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import de.robv.android.xposed.XC_MethodHook
import me.singleneuron.qn_kernel.annotation.UiItem
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.decorator.BaseStartActivityHookDecorator
import me.singleneuron.qn_kernel.tlb.辅助功能
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference

@UiItem
object FxxkQQBrowser : BaseStartActivityHookDecorator() {

    @SuppressLint("ResourceType")
    override fun doDecorate(intent: Intent, param: XC_MethodHook.MethodHookParam): Boolean {
        val url = intent.getStringExtra("url")
        /*intent.dump()
        val check1 = !url.isNullOrBlank()
        val check2 = url?.contains(Regex("http|https",RegexOption.IGNORE_CASE))
        val check3 = intent.component?.shortClassName?.contains("QQBrowserActivity")
        Utils.logd("check1=$check1 check2=$check2 check3=$check3")*/
        return if (!url.isNullOrBlank()
            && url.contains(Regex("http|https", RegexOption.IGNORE_CASE))
            && !url.contains(Regex("qq.com|tenpay.com"))
            && intent.component?.shortClassName?.contains("QQBrowserActivity") == true
        ) {
            val customTabsIntent = CustomTabsIntent.Builder()
                .apply {
                    try {
                        val color = getColorPrimary()
                        setDefaultColorSchemeParams(CustomTabColorSchemeParams.Builder()
                            .setToolbarColor(color)
                            .build())
                    } catch (e: Exception) {
                        Utils.log(e)
                    }
                }
                .setShowTitle(true)
                .build()
            customTabsIntent.intent.putExtra("from_fqb", true)
            customTabsIntent.launchUrl(hostInfo.application, Uri.parse(url))
            param.result = null
            true
        } else {
            false
        }
    }

    @ColorInt
    @Throws(Exception::class)
    fun getColorPrimary(): Int {
        val typedValue = TypedValue()
        hostInfo.application.theme.resolveAttribute(android.R.attr.colorAccent, typedValue, true)
        return typedValue.data
    }

    override val preference: UiSwitchPreference = uiSwitchPreference {
        title = "去你大爷的QQ浏览器"
        summary = "致敬 “去你大爷的内置浏览器”"
    }
    override val preferenceLocate = 辅助功能

    fun processJefs(intent: Intent): Boolean {
        return preference.value.value ?: false && intent.getBooleanExtra("from_fqb", false)
    }

}
