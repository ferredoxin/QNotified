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
package cn.lliiooll.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.mobileqq.widget.BounceScrollView;

import cn.lliiooll.hook.MuteAllMsgHook;
import cn.lliiooll.hook.MuteCardMsgHook;
import cn.lliiooll.hook.MuteRedPacketMsgHook;
import cn.lliiooll.hook.MuteTroopAtAllMsgHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.ui.ResUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

@SuppressLint("Registered")
public class MuteCfgActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {
    
    private boolean mMsfResponsive = false;
    
    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(MuteCfgActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(MuteCfgActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(MuteCfgActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ll.addView(subtitle(this, "以下所有功能重启后生效!!!"));
        ll.addView(subtitle(this, "以下所有功能重启后生效!!!"));
        ll.addView(subtitle(this, "以下所有功能重启后生效!!!"));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽全部消息", "请谨慎开启", MuteAllMsgHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽卡片消息", "去死吧!烦人的卡片!", MuteCardMsgHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽红包消息", "红包?不存在的!", MuteRedPacketMsgHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽群聊全体消息", "全体成员?不存在的!", MuteTroopAtAllMsgHook.get()));
        
        setContentView(bounceScrollView);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("自定义电量");
        return true;
    }
    
    
    @Override
    public void onClick(View view) {
    
    }
}
