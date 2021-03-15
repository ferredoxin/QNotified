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
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import me.ketal.data.ConfigData
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.ui.CustomDialog
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils

abstract class MultiItemDelayableHook constructor(keyName: String) :
    CommonDelayableHook("__NOT_USED__") {
    private val itemsConfigKeys = ConfigData<String>(keyName)
    private val allItemsConfigKeys = ConfigData<String>("$keyName\\_All")
    abstract val allItems: String
    abstract val defaultItems: String
    internal open var items
        get() = allItemsConfigKeys.getOrDefault(allItems).split("|").toMutableList()
        set(value) {
            allItemsConfigKeys.value = value.joinToString("|")
        }
    internal var activeItems
        get() = itemsConfigKeys.getOrDefault(defaultItems).split("|").toMutableList()
        set(value) {
            itemsConfigKeys.value = value.joinToString("|")
        }

    open fun listener() = View.OnClickListener {
        try {
            val cache = activeItems.toMutableList()
            val ctx = it.context
            AlertDialog.Builder(ctx, CustomDialog.themeIdForDialog())
                .setTitle("选择要精简的条目")
                .setMultiChoiceItems(
                    items.toTypedArray(),
                    getBoolAry()
                ) { _: DialogInterface, i: Int, _: Boolean ->
                    val item = items[i]
                    if (!cache.contains(item)) cache.add(item)
                    else cache.remove(item)
                }
                .setNegativeButton("取消", null)
                .setPositiveButton("确定") { _: DialogInterface, _: Int ->
                    Toasts.info(ctx, "已保存精简项目")
                    activeItems = cache
                }
                .setNeutralButton("自定义") { _: DialogInterface, _: Int ->
                    val dialog = CustomDialog.createFailsafe(ctx)
                    val context = dialog.context
                    val editText = EditText(context)
                    editText.textSize = 16f
                    val _5 = Utils.dip2px(context, 5f)
                    editText.setPadding(_5, _5, _5, _5 * 2)
                    editText.setText(items.joinToString("|"))
                    val linearLayout = LinearLayout(ctx)
                    linearLayout.orientation = LinearLayout.VERTICAL
                    linearLayout.addView(ViewBuilder.subtitle(context, "使用|分割，请确保格式正确！", Color.RED))
                    linearLayout.addView(
                        editText,
                        ViewBuilder.newLinearLayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            _5 * 2
                        )
                    )
                    val alertDialog = dialog.setTitle("自定义精简项目")
                        .setView(linearLayout)
                        .setCancelable(true)
                        .setPositiveButton("确认", null)
                        .setNegativeButton("取消", null)
                        .setNeutralButton("使用默认值", null)
                        .create() as AlertDialog
                    alertDialog.show()
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val text = editText.text.toString()
                        if (text.isEmpty()) {
                            Toasts.error(context, "不可为空")
                            return@setOnClickListener
                        }
                        text.split("|").forEach { item ->
                            if (item.isEmpty()) {
                                Toasts.error(context, "请确保格式正确！")
                                return@setOnClickListener
                            }
                        }
                        Toasts.info(context, "已保存自定义项目")
                        allItemsConfigKeys.value = editText.text.toString()
                        alertDialog.cancel()
                    }
                    alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener {
                        allItemsConfigKeys.remove()
                        Toasts.info(context, "已使用默认值")
                        alertDialog.cancel()
                    }
                }
                .show()
        } catch (e: Exception) {
            Utils.log(e)
        }
    }

    override fun initOnce() = true

    override fun isEnabled(): Boolean = activeItems.isNotEmpty() && isValid

    internal open fun getBoolAry(): BooleanArray {
        val ret = BooleanArray(items.size)
        for ((i, item) in items.withIndex()) {
            ret[i] = item in activeItems
        }
        return ret
    }

    override fun setEnabled(enabled: Boolean) = Unit
}
