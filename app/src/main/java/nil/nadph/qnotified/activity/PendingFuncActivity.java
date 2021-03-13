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
package nil.nadph.qnotified.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.clickTheComing;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitchStub;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.ui.ResUtils;

@SuppressLint("Registered")
public class PendingFuncActivity extends IphoneTitleBarActivityCompat {

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT,
            dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(this,
            "牙膏要一点一点挤, 显卡要一刀一刀切, PPT要一张一张放, 代码要一行一行写, 单个功能预计自出现在commit之日起, 三年内开发完毕"));
        ll.addView(newListItemSwitchStub(this, "无视QQ电话与语音冲突", "允许在QQ电话时播放语音和短视频", false));
        ll.addView(newListItemSwitchStub(this, "QQ电话关麦时解除占用", "再开麦时如麦被其他程序占用可能崩溃", false));
        ll.addView(newListItemSwitchStub(this, "QQ视频通话旋转锁定", "可在通话界面设置旋转方向", false));
        ll.addView(newListItemButton(this, "隐藏联系人", "和自带的\"隐藏会话\"有所不同", "0人", clickTheComing()));
        ll.addView(newListItemButton(this, "自定义本地头像", "仅本机生效", "禁用", clickTheComing()));
        ll.addView(newListItemButton(this, "高级通知设置", "通知展开, channel等", null, clickTheComing()));
        ll.addView(
            newListItemButton(this, "QQ电话睡眠模式", "仅保持连麦, 暂停消息接收, 减少电量消耗", null, clickTheComing()));
        ll.addView(newListItemSwitchStub(this, "禁用QQ公交卡", "如果QQ在后台会干扰NFC的话", false));
        ll.addView(newListItemButton(this, "AddFriendReq.sourceID", "自定义加好友来源", "[不改动]",
            clickTheComing()));
        ll.addView(
            newListItemButton(this, "DelFriendReq.delType", "只能为1或2", "[不改动]", clickTheComing()));
        ll.addView(newListItemSwitchStub(this, "隐藏聊天界面右侧滑条", "强迫症专用", false));
        ll.addView(newListItemSwitchStub(this, "复制群公告", "希望能在关键时刻帮到你", false));
        ll.addView(newListItemSwitchStub(this, "隐藏底部消息数量", null, false));
        ll.addView(newListItemSwitchStub(this, "一键已读/去除批量已读动画", null, false));
        ll.addView(newListItemSwitchStub(this, "取消聊天中开通会员提示", "如果我们能触发关键词的话", false));
        ll.addView(newListItemSwitchStub(this, "去除底部动态或联系人页面", "如果你觉得你喜欢这样的QQ的话", false));
        ll.addView(newListItemSwitchStub(this, "空间说说自动回赞", "真正的友谊应该手动点", false));
        ll.addView(newListItemSwitchStub(this, "一键退出已封禁群聊", null, false));
        ll.addView(newListItemSwitchStub(this, "清理全部非置顶/清理群聊", null, false));

        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setTitle("咕咕咕");
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }


}
