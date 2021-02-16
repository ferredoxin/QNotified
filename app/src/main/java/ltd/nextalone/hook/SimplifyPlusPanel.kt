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
package ltd.nextalone.hook

import de.robv.android.xposed.XC_MethodHook
import ltd.nextalone.base.MultiItemDelayableHook
import ltd.nextalone.util.hookBefore
import ltd.nextalone.util.method
import me.singleneuron.qn_kernel.data.hostInfo
import me.singleneuron.qn_kernel.data.requireMinQQVersion
import me.singleneuron.util.QQVersion
import nil.nadph.qnotified.util.Utils

object SimplifyPlusPanel : MultiItemDelayableHook("na_simplify_plus_panel", "保留") {
    override val allItems = "图片|拍摄|语音通话|视频通话|一起派对|戳一戳|视频包厢|红包|位置|文件|一起听歌|分享屏幕|收藏|热图|一起玩|涂鸦|转账|名片|送礼物|腾讯文档|厘米秀|一起K歌|礼物|直播间|签到|匿名|群课堂|健康收集|一起看|投票|收钱|坦白说".split("|").toMutableList()
    override val defaultItems = "语音通话|视频通话|位置|文件"

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
        if (hostInfo.versionCode >= QQVersion.QQ_8_5_5) {
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->a(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/coreui/pluspanel/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->b(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/coreui/pluspanel/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
        } else if (hostInfo.versionCode == QQVersion.QQ_8_5_0) {
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->a(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
            "Lcom/tencent/mobileqq/activity/aio/pluspanel/PlusPanelViewBinder;->b(Ljava/util/ArrayList;Lcom/tencent/mobileqq/activity/aio/PanelAdapter;Lcom/tencent/mobileqq/emoticonview/EmoticonPagerRadioGroup;)V".method.hookBefore(this, callback)
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
}
