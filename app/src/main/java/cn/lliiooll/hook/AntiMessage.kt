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

package cn.lliiooll.hook

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Looper
import android.view.View
import cc.ioctl.H
import cn.lliiooll.msg.MessageReceiver
import cn.lliiooll.util.MsgRecordUtil
import de.robv.android.xposed.XposedHelpers
import me.singleneuron.data.MsgRecordData
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

object AntiMessage : CommonDelayableHook("__NOT_USED__"), MessageReceiver {
    private const val qn_anti_message_items = "qn_anti_message_items"
    private val allItems = MsgRecordUtil.MSG.keys.toTypedArray()
    private const val defaultItems = ""
    private var activeItems
        get() = ConfigManager.getDefaultConfig().getStringOrDefault(qn_anti_message_items, defaultItems).split("|").toMutableList()
        set(value) {
            var ret = ""
            for (item in value)
                ret += "|$item"
            putValue(qn_anti_message_items, if (ret.isEmpty()) ret else ret.substring(1))
        }

    fun antiMessageItemClick() = View.OnClickListener {
        try {
            val cache = activeItems
            val ctx = it.context
            AlertDialog.Builder(ctx, CustomDialog.themeIdForDialog())
                .setTitle("选择要屏蔽的条目")
                .setMultiChoiceItems(allItems, getBoolAry()) { _: DialogInterface, i: Int, _: Boolean ->
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


    override fun onReceive(data: MsgRecordData?): Boolean {
        if (data?.selfUin.equals(data?.senderUin)) return false
        val items: List<Int> = MsgRecordUtil.parse(activeItems)
        if (items.contains(data?.msgType)) {
            XposedHelpers.setBooleanField(data?.msgRecord, "isread", true)
            return true
        }
        return false
    }

    override fun initOnce() = true

    override fun isValid(): Boolean = requireMinQQVersion(QQVersion.QQ_8_0_0)

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
