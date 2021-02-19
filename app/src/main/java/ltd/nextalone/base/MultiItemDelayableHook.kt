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
package ltd.nextalone.base

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Looper
import android.view.View
import me.singleneuron.qn_kernel.data.hostInfo
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

abstract class MultiItemDelayableHook constructor(keyName: String, val listenerTitle: String) : CommonDelayableHook("__NOT_USED__") {
    private val itemsConfigKeys = keyName
    abstract val allItems: List<String>
    abstract val defaultItems: String
    internal var activeItems
        get() = ConfigManager.getDefaultConfig().getStringOrDefault(itemsConfigKeys, defaultItems).split("|").toMutableList()
        set(value) {
            var ret = ""
            for (item in value)
                ret += "|$item"
            putValue(itemsConfigKeys, if (ret.isEmpty()) ret else ret.substring(1))
        }

    open fun listener() = View.OnClickListener {
        try {
            val cache = activeItems.toMutableList()
            val ctx = it.context
            AlertDialog.Builder(ctx, CustomDialog.themeIdForDialog())
                .setTitle("选择要${listenerTitle}的条目")
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

    override fun initOnce() = true

    override fun isEnabled(): Boolean = activeItems.isNotEmpty()

    internal open fun getBoolAry(): BooleanArray {
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
                Toasts.error(hostInfo.application, e.toString() + "")
            } else {
                SyncUtils.post { Toasts.error(hostInfo.application, e.toString() + "") }
            }
        }
    }

    override fun setEnabled(enabled: Boolean) {}
}
