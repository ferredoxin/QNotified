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
package ltd.nextalone.hook

import android.view.View
import android.view.ViewGroup
import ltd.nextalone.util.*
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook

@FunctionEntry
object SimplifyEmoPanel : CommonDelayableHook("na_simplify_emo_panel_kt") {
    // todo fix scroll
    override fun initOnce() = tryOrFalse {
        ("com.tencent.mobileqq.emoticonview.BasePanelView".clazz
            ?: "com.tencent.mobileqq.emoticonview.EmoticonPanelController".clazz
            )?.method("initTabView")?.hookBefore(
                this
            ) {
                logBefore("initTabView")
                val mutableList: MutableList<*> = if ("com.tencent.mobileqq.emoticonview.BasePanelModel".clazz != null) {
                    it.thisObject.get("mPanelController").get("mBasePanelModel").get("panelDataList") as MutableList<*>
                } else {
                    it.thisObject.get("panelDataList") as MutableList<*>
                }
                val list = mutableList.listIterator()
                while (list.hasNext()) {
                    val item = list.next()
                    if (item != null) {
                        val i = item.javaClass.getDeclaredField("type").get(item) as Int
                        if (i !in arrayListOf(4, 7)) {
                            list.remove()
                        }
                    }
                }
                "Lcom/tencent/mobileqq/emoticonview/EmoticonTabAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;".method.hookAfter(
                    this@SimplifyEmoPanel
                ) { it2 ->
                    val view: View = it2.result as View
                    val layoutParams: ViewGroup.LayoutParams = view.layoutParams
                    layoutParams.width = hostInfo.application.resources.displayMetrics.widthPixels / 2
                    view.layoutParams = layoutParams
                    it2.result = view
                }
            }
    }

    override fun isValid() = requireMinQQVersion(QQVersion.QQ_8_5_5)
}
