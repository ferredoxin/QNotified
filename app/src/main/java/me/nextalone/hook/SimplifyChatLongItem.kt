/* QNotified - An Xposed module for QQ/TIM
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
package me.nextalone.hook

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Build
import android.os.Looper
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import me.nextalone.util.Utils.hook
import me.nextalone.util.Utils.method
import nil.nadph.qnotified.H
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

object SimplifyChatLongItem : CommonDelayableHook("__NOT_USED__") {
    private val THEME_LIGHT = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) R.style.Theme_DeviceDefault_Light_Dialog_Alert else AlertDialog.THEME_DEVICE_DEFAULT_LIGHT
    private val THEME_DARK = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) R.style.Theme_DeviceDefault_Dialog_Alert else AlertDialog.THEME_DEVICE_DEFAULT_DARK
    private const val na_simplify_chat_long_item = "na_simplify_chat_long_item"
    private val allItems = "复制|转发|收藏|回复|多选|撤回|删除|一起写|设为精华|待办|私聊|截图|存表情|相关表情".split("|")
    private const val defaultItems = "一起写|私聊|相关表情|待办"
    var activeItems
    get() = ConfigManager.getDefaultConfig().getStringOrDefault(na_simplify_chat_long_item, defaultItems).split("|")
    set(value) {
        var ret = ""
        for (item in value)
            ret += "|$item"
        putValue(na_simplify_chat_long_item, ret.substring(1))
    }

    fun simplifyChatLongItemClick(): View.OnClickListener {
        return View.OnClickListener {
            try {
                val cache = activeItems as MutableList
                Utils.logd("list:$cache,class:${cache::class.java}")
                val ctx = it.context
                AlertDialog.Builder(ctx, if (ResUtils.isInNightMode())  THEME_DARK else THEME_LIGHT)
                    .setTitle("精简聊天气泡长按菜单")
                    .setMultiChoiceItems(allItems.toTypedArray(), getBoolAry()) { _: DialogInterface, i: Int, _: Boolean ->
                        val item = allItems[i]
                        if (!cache.contains(item)) cache.add(item)
                        else cache.remove(item)
                    }
                    .setNegativeButton("取消", null)
                    .setPositiveButton("确定") { _: DialogInterface, _: Int ->
                        activeItems = cache
                    }
                    .show()
            } catch (e: Exception) {
                Utils.log(e)
            }
        }
    }

    override fun initOnce(): Boolean {
        return try {
            val callback = object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam) {
                    if (!isEnabled) return
                    val str = param.args[1] as String
                    if (activeItems.contains(str))
                        param.result = null
                }
            }
            "Lcom/tencent/mobileqq/utils/dialogutils/QQCustomMenu;->a(ILjava/lang/String;II)V"
                .method
                .hook(callback)
            "Lcom/tencent/mobileqq/utils/dialogutils/QQCustomMenu;->a(ILjava/lang/String;I)V"
                .method
                .hook(callback)
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean = activeItems.isNotEmpty()

    private fun getBoolAry(): BooleanArray {
        val ret = BooleanArray(allItems.size)
        for ((i, item) in allItems.withIndex()) {
            ret[i] = activeItems.contains(item)
        }
        return ret
    }

    private fun putValue(keyName: String, obj: Any) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[keyName] = obj
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(H.getApplication(), e.toString() + "")
            } else {
                SyncUtils.post { Toasts.error(H.getApplication(), e.toString() + "") }
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {}
}
