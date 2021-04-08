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
package nil.nadph.qnotified.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import com.tencent.mobileqq.widget.BounceScrollView
import me.ketal.hook.FakeBalance
import me.ketal.hook.FakeQQLevel
import me.ketal.hook.HideSearch
import me.ketal.hook.HideTab
import me.kyuubiran.hook.AutoMosaicName
import me.kyuubiran.hook.ShowSelfMsgByLeft
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit
import nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitchConfigNext

@SuppressLint("Registered")
class AmusementActivity : IphoneTitleBarActivityCompat() {
    override fun doOnCreate(bundle: Bundle?): Boolean {
        super.doOnCreate(bundle)
        val ll = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT)
        }
        setContentView(BounceScrollView(this, null).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
            addView(ll)
        })

        ll.addView(
            newListItemHookSwitchInit(this, "昵称/群名字打码", "娱乐功能 不进行维护", AutoMosaicName))
        ll.addView(newListItemHookSwitchInit(this, "自己的消息和头像居左显示", "娱乐功能 不进行维护",
            ShowSelfMsgByLeft))
        ll.addView(
            ViewBuilder.newListItemButton(this, "自定义钱包余额", "仅供娱乐", null, FakeBalance.listener()))
        ll.addView(
            ViewBuilder.newListItemButton(this, "自定义QQ等级", "仅本地生效", null, FakeQQLevel.listener()))
        if (HideTab.isValid) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏底栏", "底栏项目移到侧滑", HideTab))
        }
        if (HideSearch.isValid) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏搜索编辑框", "谨慎开启", HideSearch))
        }

        setContentBackgroundDrawable(ResUtils.skin_background)
        title = "娱乐功能"
        return true
    }
}
