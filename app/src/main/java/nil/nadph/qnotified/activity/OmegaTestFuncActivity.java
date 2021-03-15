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
import static cc.ioctl.util.SendBatchMsg.clickToBatchMsg;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemHookSwitchInit;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.ioctl.hook.CardMsgHook;
import com.tencent.mobileqq.widget.BounceScrollView;
import me.kyuubiran.dialog.AutoRenewFireDialog;
import me.singleneuron.hook.CopyCardMsg;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;

@SuppressLint("Registered")
public class OmegaTestFuncActivity extends IphoneTitleBarActivityCompat {

    TextView __tv_chat_tail_status;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        RelativeLayout _t;
        String _hostName = HostInformationProviderKt.getHostInfo().getHostName();
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
        ll.addView(subtitle(this, "Omega功能[本功能将会收集您使用本功能发送的消息内容与当前QQ号 请酌情开启]", Color.RED));
        ll.addView(subtitle(this, "该页面内容随时会有增减或不可用等情况,我们不会受理任何关于此页面问题的报告,请遵循QNotified与QQ用户协议"
            + "本模块将不会对你所作的任何行为负责,同时请注意您对Omega Project的使用方式,我们随时有可能因被滥用而下线该功能", Color.RED));
        View autoRenewFire = newListItemButton(this, "[特供版]自动续火", "芜湖", null,
            view -> AutoRenewFireDialog.INSTANCE.showMainDialog(this));
        autoRenewFire.setOnLongClickListener(view -> {
            AutoRenewFireDialog.INSTANCE.showSettingsDialog(this);
            return true;
        });
        ll.addView(autoRenewFire);
        ll.addView(newListItemHookSwitchInit(this, "复制卡片消息", "", CopyCardMsg.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "发送卡片消息", "ArkAppMsg(json)+StructMsg(xml)",
            CardMsgHook.INSTANCE));
        ll.addView(subtitle(this, "卡片消息使用说明:先输入卡片代码(聊天界面),后长按发送按钮\n勿滥用此功能! 频繁使用此功能被举报可能封号"));
        ll.addView(
            newListItemButton(this, "群发文本消息" + (LicenseStatus.isAsserted() ? "" : "（仅限五个字以内）"),
                "年少不知号贵-理性使用以免永冻", null, clickToBatchMsg()));
        ll.addView(
            subtitle(this, "警告: 请勿发送违规内容! 在您使用 群发文本消息 与 发送卡片消息 时，本模块会向服务器报告您 群发/卡片的消息内容 以及当前QQ号。"
                    + "继续使用 群发 与 发送卡片 功能代表您同意放弃自己的一切权利，并允许QNotified开发组及管理组在非匿名的前提下任意存储、分析、使用、分享您的数据",
                Color.RED));
        ll.addView(subtitle(this, "想要隐私就不要去玩 群发 或者 卡片消息, 是否开启功能是你们的自由", Color.RED));
        ll.addView(subtitle(this, "如果您看不懂, 或无法理解以上内容, 请勿使用 群发 或 卡片消息 功能!", Color.RED));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;

        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("Omega测试性功能");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        return true;
    }

}
