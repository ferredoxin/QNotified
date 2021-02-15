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
package me.nextalone.hook

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Looper
import android.view.View
import de.robv.android.xposed.XC_MethodHook
import me.nextalone.util.hookBefore
import me.nextalone.util.method
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

object SimplifyPlusPanel : CommonDelayableHook("__NOT_USED__") {
    private const val na_simplify_plus_panel = "na_simplify_plus_panel"
    private val allItems = "图片|拍摄|语音通话|视频通话|一起派对|戳一戳|视频包厢|红包|位置|文件|一起听歌|分享屏幕|收藏|热图|一起玩|涂鸦|转账|名片|送礼物|腾讯文档|厘米秀|一起K歌|礼物|直播间|签到|匿名|群课堂|健康收集|一起看|投票|收钱|坦白说".split("|")
    private const val defaultItems = "语音通话|视频通话|位置|文件"
    private var activeItems
        get() = ConfigManager.getDefaultConfig().getStringOrDefault(na_simplify_plus_panel, defaultItems).split("|").toMutableList()
        set(value) {
            var ret = ""
            for (item in value)
                ret += "|$item"
            putValue(na_simplify_plus_panel, if (ret.isEmpty()) ret else ret.substring(1))
        }

    fun simplifyPlusPanelClick() = View.OnClickListener {
        try {
            val cache = activeItems
            val ctx = it.context
            AlertDialog.Builder(ctx, CustomDialog.themeIdForDialog())
                .setTitle("选择要保留的条目")
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


    override fun initOnce() = try {
        val callback: (XC_MethodHook.MethodHookParam) -> Unit = {
            val list = (it.args[0] as MutableList<*>).listIterator()
            while (list.hasNext()) {
                val item = list.next()
                if (item != null) {
                    val str = (item.javaClass.getDeclaredField("a").get(item) as String).toString()
                    if (activeItems.all { string ->
                            string !in str
                        }) {
                        list.remove()
                    }
                }
            }
        }
        if (hostInfo.versionCode >= QQVersion.QQ_8_5_0) {
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->a(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/coreui/pluspanel/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->b(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/coreui/pluspanel/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
        } else {
            "Lcom/tencent/mobileqq/activity/aio/PlusPanel;->a(Ljava/util/ArrayList;)V".method.hookBefore(this, callback)
            "Lcom/tencent/mobileqq/activity/aio/PlusPanel;->b(Ljava/util/ArrayList;)V".method.hookBefore(this, callback)
        }
        true
    } catch (t: Throwable) {
        Utils.log(t)
        false
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_0_0)

    override fun isEnabled(): Boolean = activeItems.isNotEmpty()

    private fun getBoolAry(): BooleanArray {
        val ret = BooleanArray(allItems.size)
        for ((i, item) in allItems.withIndex()) {
            ret[i] = activeItems.contains(item)
        }
        return ret
    }

    private fun putValue(keyName: String, obj: Any) = try {
        val mgr = ConfigManager.getDefaultConfig()
        mgr.allConfig[keyName] = obj
        mgr.save()
    } catch (e: Exception) {
        Utils.log(e)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Toasts.error(hostInfo.application, e.toString() + "")
        } else {
            SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
        }
    }

    override fun setEnabled(enabled: Boolean) = Unit
}
