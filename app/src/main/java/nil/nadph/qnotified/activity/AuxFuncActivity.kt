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
import androidx.core.view.plusAssign
import cc.ioctl.hook.BlockFluxThief
import cc.ioctl.hook.InterceptZipBomb
import cc.ioctl.hook.PicMd5Hook
import com.tencent.mobileqq.widget.BounceScrollView
import me.ketal.hook.*
import me.ketal.ui.activity.ModifyLeftSwipeReplyActivity
import nil.nadph.qnotified.ui.ResUtils
import nil.nadph.qnotified.ui.ViewBuilder.*

@SuppressLint("Registered")
class AuxFuncActivity : IphoneTitleBarActivityCompat() {
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

        ll += newListItemHookSwitchInit(this, "拦截异常zip", null, InterceptZipBomb.INSTANCE)
        ll += newListItemHookSwitchInit(this, "拦截异常体积图片加载", null, BlockFluxThief.INSTANCE)
        ll += newListItemHookSwitchInit(this, "显示图片MD5", "长按图片消息点击MD5", PicMd5Hook.INSTANCE)
        ll += newListItemConfigSwitchIfValid(this, "修改@界面排序", "排序由群主管理员至正常人员", SortAtPanel)
        ll += newListItemConfigSwitchIfValid(this, "发送收藏消息添加分组", null, SendFavoriteHook)
        ll += newListItemConfigSwitchIfValid(this, "消息显示发送者QQ号和时间", null, ChatItemShowQQUin)
        ll += newListItemConfigSwitchIfValid(this, "消息显示At对象", null, ShowMsgAt)
        ll += newListItemHookSwitchInit(this, "批量撤回消息", "多选消息后撤回", MultiActionHook)
        ll += newListItemButtonIfValid(this, "修改消息左滑动作", null, null, LeftSwipeReplyHook,
                ModifyLeftSwipeReplyActivity::class.java)
        ll += newListItemButton(this, "管理QQ组件", null, null, ManageComponent.listener)

        setContentBackgroundDrawable(ResUtils.skin_background)
        title = "辅助功能"
        return true
    }
}
