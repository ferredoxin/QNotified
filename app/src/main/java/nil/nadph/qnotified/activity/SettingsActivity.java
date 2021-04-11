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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.rymmmmm.hook.RemoveMiniProgramAd;
import com.tencent.mobileqq.widget.BounceScrollView;

import java.io.File;
import java.io.IOException;

import cc.ioctl.activity.ExfriendListActivity;
import cc.ioctl.activity.FakeBatCfgActivity;
import cc.ioctl.activity.FriendlistExportActivity;
import cc.ioctl.activity.JefsRulesActivity;
import cc.ioctl.dialog.RepeaterIconSettingDialog;
import cc.ioctl.dialog.RikkaDialog;
import cc.ioctl.hook.*;
import cn.lliiooll.hook.AntiMessage;
import ltd.nextalone.hook.*;
import me.ketal.hook.HideFriendCardSendGift;
import me.ketal.hook.QWalletNoAD;
import me.ketal.hook.QZoneNoAD;
import me.singleneuron.activity.ChangeDrawerWidthActivity;
import me.singleneuron.hook.*;
import me.singleneuron.hook.decorator.*;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.ui.NewSettingsActivity;
import me.singleneuron.util.KotlinUtilsKt;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static me.ketal.util.PlayQQVersion.PlayQQ_8_2_9;
import static me.singleneuron.util.KotlinUtilsKt.addViewConditionally;
import static me.singleneuron.util.QQVersion.QQ_8_2_6;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.log;

@SuppressLint("Registered")
public class SettingsActivity extends IphoneTitleBarActivityCompat implements Runnable {

    private static final int R_ID_BTN_FILE_RECV = 0x300AFF91;
    private static final String qn_enable_fancy_rgb = "qn_enable_fancy_rgb";
    int color;
    int step;//(0-255)
    int stage;//0-5
    private TextView __tv_fake_bat_status, __recv_status, __recv_desc, __jmp_ctl_cnt;
    private boolean isVisible = false;
    private boolean rgbEnabled = false;
    private TextView mRikkaTitle, mRikkaDesc;
    private Looper mainLooper = Looper.getMainLooper();

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        String _hostName = HostInformationProviderKt.getHostInfo().getHostName();
        LinearLayout ll = new LinearLayout(this);
        ll.setId(R.id.rootMainLayout);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setId(R.id.rootBounceScrollView);
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
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        try {
            LinearLayout nnn = subtitle(this, "");
            TextView t = (TextView) nnn.getChildAt(0);
            NewsHelper.getCachedNews(t);
            ll.addView(nnn);
            NewsHelper.asyncFetchNewsIfNeeded(t);
        } catch (Throwable e) {
            log(e);
        }
        ll.addView(newListItemButton(this,"打开新版设置页面","WIP", null, clickToProxyActAction(NewSettingsActivity.class)));
        ll.addView(newListItemButton(this, "Beta测试性功能", "仅用于测试稳定性", null,
            clickToProxyActAction(BetaTestFuncActivity.class)));
        ll.addView(newListItemButton(this, "Omega测试性功能", "这是个不存在的功能", null,
            v -> KotlinUtilsKt.showEulaDialog(SettingsActivity.this)));
        ll.addView(subtitle(this, "基本功能"));
        if (HostInformationProviderKt.requireMinQQVersion(QQ_8_2_6) || HostInformationProviderKt.requireMinPlayQQVersion(PlayQQ_8_2_9)) {
            ll.addView(_t = newListItemButton(this, "自定义电量", "[QQ>=8.2.6]在线模式为我的电量时生效", "N/A",
                clickToProxyActAction(FakeBatCfgActivity.class)));
            __tv_fake_bat_status = _t.findViewById(R_ID_VALUE);
        }
        ViewGroup _tmp_vg = newListItemButton(this, "花Q", "若无另行说明, 所有功能开关都即时生效", null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RikkaDialog.showRikkaFuncDialog(SettingsActivity.this);
                }
            });
        _tmp_vg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                rgbEnabled = !rgbEnabled;
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                cfg.putBoolean(qn_enable_fancy_rgb, rgbEnabled);
                try {
                    cfg.save();
                } catch (IOException ignored) {
                }
                if (rgbEnabled) {
                    startRgbIfEnabled();
                }
                return true;
            }
        });
        mRikkaTitle = _tmp_vg.findViewById(R_ID_TITLE);
        mRikkaDesc = _tmp_vg.findViewById(R_ID_DESCRIPTION);
        ll.addView(_tmp_vg);
        ll.addView(newListItemButton(this, "QQ净化[WIP]", "开发中...", null,
            clickToProxyActAction(me.zpp0196.qqpurify.activity.MainActivity.class)));
        ll.addView(newListItemHookSwitchInit(this, "语音转发", "长按语音消息", PttForwardHook.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, " +1", "不是复读机", RepeaterHook.INSTANCE));
        ll.addView(newListItemButton(this, "自定义+1图标", null, null,
            RepeaterIconSettingDialog.OnClickListener_createDialog(this)));
        ll.addView(newListItemButton(this, "辅助功能", null, null,
            clickToProxyActAction(AuxFuncActivity.class)));
        ll.addView(newListItemButton(this, "娱乐功能", null, null,
            clickToProxyActAction(AmusementActivity.class)));
        ll.addView(subtitle(this, "净化设置"));
        ll.addView(newListItemConfigSwitchIfValid(this, "禁止回复自动@", "去除回复消息时自动@特性",
            ReplyNoAtHook.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "禁用$打开送礼界面", "禁止聊天时输入$自动弹出[选择赠送对象]窗口",
            $endGiftHook.INSTANCE));
        ll.addView(subtitle(this, "消息通知设置(不影响接收消息)屏蔽后可能仍有[橙字],但通知栏不会有通知,赞说说不提醒仅屏蔽通知栏的通知"));
        ll.addView(subtitle(this, "    注:屏蔽后可能仍有[橙字],但不会有通知"));
        ll.addView(newListItemHookSwitchInit(this, "被赞说说不提醒", "不影响评论,转发或击掌的通知",
            MuteQZoneThumbsUp.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "转发消息点头像查看详细信息", "仅限合并转发的消息",
            MultiForwardAvatarHook.INSTANCE));
        if (!HostInformationProviderKt.getHostInfo().isTim()) {
            ll.addView(subtitle(this, "图片相关"));
            ll.addView(newListItemHookSwitchInit(this, "禁止秀图自动展示", null, ShowPicGagHook.INSTANCE));
            ll.addView(newListItemHookSwitchInit(this, "禁用夜间模式遮罩", "移除夜间模式下聊天界面的深色遮罩",
                DarkOverlayHook.INSTANCE));
        }
        ll.addView(
            newListItemHookSwitchInit(this, "显示设置禁言的管理", "即使你只是普通群成员", GagInfoDisclosure.INSTANCE));
        addViewConditionally(ll, this, "小程序分享转链接（发送）", "感谢Alcatraz323开发远离小程序,神经元移植到Xposed",
            NoApplet.INSTANCE);
        ll.addView(subtitle(this, "实验性功能(未必有效)"));
        ll.addView(
            _t = newListItemButton(this, "下载重定向", "N/A", "N/A", this::onFileRecvRedirectClick));
        _t.setId(R_ID_BTN_FILE_RECV);
        __recv_desc = _t.findViewById(R_ID_DESCRIPTION);
        __recv_status = _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemButton(this, "添加账号", "需要手动登录, 核心代码由 JamGmilk 提供", null,
            this::onAddAccountClick));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽小程序广告", "需要手动关闭广告, 请勿反馈此功能无效",
            RemoveMiniProgramAd.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "收藏更多表情", "[暂不支持>=8.2.0]保存在本地",
            FavMoreEmo.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽更新提醒", null, PreUpgradeHook.INSTANCE));
        if (!HostInformationProviderKt.getHostInfo().isTim()) {
            ll.addView(newListItemHookSwitchInit(this, "自定义猜拳骰子", null, CheatHook.INSTANCE));
            ll.addView(
                newListItemHookSwitchInit(this, "简洁模式圆头像", "From Rikka", RoundAvatarHook.INSTANCE));
        }
        ll.addView(newListItemSwitchConfigNext(this, "新版简洁模式圆头像", "From Rikka, 支持8.3.6及更高，重启后生效",
            NewRoundHead.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "强制使用系统相机", "仅能录像，支持8.3.6及更高",
            ForceSystemCamera.INSTANCE));
        addViewConditionally(ll, this, "强制使用系统相册", "支持8.3.6及更高", ForceSystemAlbum.INSTANCE);
        ll.addView(
            newListItemHookSwitchInit(this, "强制使用系统文件", "支持8.3.6及更高", ForceSystemFile.INSTANCE));
        ll.addView(newListItemButton(this, "修改侧滑边距", "感谢祈无，支持8.4.1及更高，重启后生效", "",
            clickToProxyActAction(ChangeDrawerWidthActivity.class)));
        ll.addView(
            newListItemHookSwitchInit(this, DisableQzoneSlideCamera.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, SimpleReceiptMessage.INSTANCE));
        ll.addView(newListItemButtonIfValid(this, "静默指定类型消息通知", null, null, AntiMessage.INSTANCE));
        ll.addView(newListItemButtonIfValid(this, "聊天字数统计", null, null, ChatWordsCount.INSTANCE,
            v -> ChatWordsCount.INSTANCE.showChatWordsCountDialog(this)));
        ll.addView(
            newListItemButtonIfValid(this, "精简聊天气泡长按菜单", null, null, SimplifyChatLongItem.INSTANCE,
                SimplifyChatLongItem.INSTANCE.listener()));
        ll.addView(
            newListItemButtonIfValid(this, "精简加号菜单", null, null, SimplifyPlusPanel.INSTANCE));
        ll.addView(
            newListItemButtonIfValid(this, "精简设置菜单", null, null, SimplifyQQSettings.INSTANCE));
        ll.addView(
            newListItemButtonIfValid(this, "精简联系人页面", null, null, SimplifyContactTabs.INSTANCE));
        ll.addView(
            newListItemButtonIfValid(this, "精简主页对话框", null, null, SimplifyRecentDialog.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "精简表情菜单", null, SimplifyEmoPanel.INSTANCE));
        ll.addView(
            newListItemConfigSwitchIfValid(this, "精简底栏动态", null, SimplifyBottomQzone.INSTANCE));
        ll.addView(
            newListItemConfigSwitchIfValid(this, "隐藏底栏小红点", null, RemoveBottomRedDots.INSTANCE));
        ll.addView(
            newListItemConfigSwitchIfValid(this, "隐藏文本框上方快捷方式", null, RemoveShortCutBar.INSTANCE));
        ll.addView(
            newListItemConfigSwitchIfValid(this, "隐藏群聊群成员头衔", null, HideTroopLevel.INSTANCE));
        ll.addView(
            newListItemButtonIfValid(this, "聊天框添加提示文字", null, null,ChatInputHint.INSTANCE,
                v -> ChatInputHint.INSTANCE.showInputHintDialog(this)));
        ll.addView(
            newListItemHookSwitchInit(this, "聊天自动发送原图", null, AutoSendOriginalPhoto.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "聊天自动接收原图", null,
            AutoReceiveOriginalPhoto.INSTANCE));
        ll.addView(
            newListItemConfigSwitchIfValid(this, "移除消息前后的空格", null, TrimMessage.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "隐藏空间好友热播和广告", null, QZoneNoAD.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "隐藏QQ钱包超值精选", null, QWalletNoAD.INSTANCE));
        ll.addView(
            newListItemButton(this, "万象屏蔽卡片消息", "使用强大的正则表达式自由屏蔽卡片消息", null, RegexAntiMeg.INSTANCE));
        addViewConditionally(ll, this, "特别关心通知单独分组", "将特别关心发送的消息通知移动到单独的通知渠道",
            SpecialCareNewChannel.INSTANCE);
        ll.addView(newListItemHookSwitchInit(this, CardMsgToText.INSTANCE));
        ll.addView(
            newListItemHookSwitchInit(this, MiniAppToStruckMsg.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "屏蔽好友资料页送礼物按钮", null,
            HideFriendCardSendGift.INSTANCE));
        ll.addView(subtitle(this, "好友列表"));
        ll.addView(newListItemButton(this, "打开资料卡", "打开指定用户的资料卡", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.createFailsafe(SettingsActivity.this);
                Context ctx = dialog.getContext();
                EditText editText = new EditText(ctx);
                editText.setTextSize(16);
                int _5 = dip2px(SettingsActivity.this, 5);
                editText.setPadding(_5, _5, _5, _5);
                ViewCompat.setBackground(editText, new HighContrastBorder());
                LinearLayout linearLayout = new LinearLayout(ctx);
                linearLayout
                    .addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
                AlertDialog alertDialog = (AlertDialog) dialog.setTitle("输入对方QQ号")
                    .setView(linearLayout)
                    .setCancelable(true)
                    .setPositiveButton("确认", null)
                    .setNegativeButton("取消", null)
                    .create();
                alertDialog.show();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String text = editText.getText().toString();
                            if (text.equals("")) {
                                Toasts.error(SettingsActivity.this, "请输入QQ号");
                                return;
                            }
                            long uin = 0;
                            try {
                                uin = Long.parseLong(text);
                            } catch (NumberFormatException ignored) {
                            }
                            if (uin < 10000) {
                                Toasts.error(SettingsActivity.this, "请输入有效的QQ号");
                                return;
                            }
                            alertDialog.dismiss();
                            MainHook.openProfileCard(SettingsActivity.this, uin);
                        }
                    });
            }
        }));
        ll.addView(newListItemButton(this, "历史好友", null, null,
            clickToProxyActAction(ExfriendListActivity.class)));
        ll.addView(newListItemButton(this, "导出历史好友列表", "支持csv/json格式", null,
            clickToProxyActAction(FriendlistExportActivity.class)));
        ll.addView(newListItemConfigSwitchIfValid(this, "被删好友通知", "检测到你被好友删除后发出通知",
            ConfigItems.qn_notify_when_del));
        if (!HostInformationProviderKt.getHostInfo().isTim()) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏分组下方入口", "隐藏分组列表最下方的历史好友按钮",
                ConfigItems.qn_hide_ex_entry_group, false));
        }
        ll.addView(newListItemSwitchConfigNext(this, "禁用" + _hostName + "热补丁", "一般无需开启",
            ConfigItems.qn_disable_hot_patch));
        ll.addView(subtitle(this, "参数设定"));
        ll.addView(_t = newListItemButton(this, "跳转控制", "跳转自身及第三方Activity控制", "N/A",
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Initiator.load("com.tencent.mobileqq.haoliyou.JefsClass") != null) {
                        Context ctx = v.getContext();
                        ctx.startActivity(new Intent(ctx, JefsRulesActivity.class));
                    } else {
                        Toasts.error(SettingsActivity.this, "当前版本客户端版本不支持");
                    }
                }
            }));
        __jmp_ctl_cnt = _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemSwitchStub(this, "禁用特别关心长震动", "他女朋友都没了他也没开发这个功能", false));
        ll.addView(subtitle(this, "关于"));
        ll.addView(
            newListItemDummy(this, HostInformationProviderKt.getHostInfo().getHostName(), null,
                HostInformationProviderKt.getHostInfo().getVersionName() + "("
                    + HostInformationProviderKt.getHostInfo().getVersionCode() + ")"));
        ll.addView(newListItemDummy(this, "模块版本", null, Utils.QN_VERSION_NAME));
        UpdateCheck uc = new UpdateCheck();
        ll.addView(_t = newListItemButton(this, "检查更新", null, "点击检查", uc));
        uc.setVersionTip(_t);
        ll.addView(newListItemButton(this, "关于模块", null, null,
            clickToProxyActAction(AboutActivity.class)));
        ll.addView(newListItemButton(this, "用户协议", "《QNotified 最终用户许可协议》与《隐私条款》", null,
            clickToProxyActAction(EulaActivity.class)));
        ll.addView(newListItemButton(this, "展望未来", "其实都还没写", null,
            clickToProxyActAction(PendingFuncActivity.class)));
        ll.addView(newListItemButton(this, "特别鸣谢", "感谢卖动绘制图标", null,
            clickToProxyActAction(LicenseActivity.class)));
        ll.addView(subtitle(this, "调试"));
        ll.addView(newListItemButton(this, "故障排查", null, null,
            clickToProxyActAction(TroubleshootActivity.class)));
        ll.addView(newListItemButton(this, "Shell.exec", "正常情况下无需使用此功能", null, clickTheComing()));
        ll.addView(
            newListItemButton(this, "Github", "获取源代码 Bug -> Issue (star)", "ferredoxin/QNotified",
                clickToUrl("https://github.com/ferredoxin/QNotified")));
        ll.addView(subtitle(this, "本软件为免费软件,请尊重个人劳动成果,严禁倒卖\nLife feeds on negative entropy."));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setContentBackgroundDrawable(ResUtils.skin_background);
        setRightButton("更多", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOverflowPopupMenu();
            }
        });
        setTitle("高级");
        try {
            getString(R.string.res_inject_success);
        } catch (Resources.NotFoundException e) {
            CustomDialog.createFailsafe(this).setTitle("FATAL Exception").setCancelable(true)
                .setPositiveButton(getString(android.R.string.yes), null)
                .setNeutralButton("重启" + HostInformationProviderKt.getHostInfo().getHostName(),
                    (dialog, which) -> {
                        try {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } catch (Throwable e1) {
                            log(e1);
                        }
                    })
                .setMessage(
                    "Resources injection failure!\nApplication may misbehave.\n" + e.toString()
                        + "\n如果您刚刚更新了插件, 您可能需要重启" + HostInformationProviderKt.getHostInfo()
                        .getHostName()
                        + "(太/无极阴,应用转生,天鉴等虚拟框架)或者重启手机(EdXp, Xposed, 太极阳), 如果重启手机后问题仍然存在, 请向作者反馈, 并提供详细日志")
                .show();
        }
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ConfigManager.getDefaultConfig();//改这里的话可能会引发其他问题，所以只把红包和全体改了
        rgbEnabled = cfg.getBooleanOrFalse(qn_enable_fancy_rgb);
        if (__tv_fake_bat_status != null) {
            FakeBatteryHook bat = FakeBatteryHook.INSTANCE;
            if (bat.isEnabled()) {
                int cap = bat.getFakeBatteryCapacity();
                boolean c = bat.isFakeBatteryCharging();
                __tv_fake_bat_status.setText(cap + (c ? "%+ " : "% "));
            } else {
                __tv_fake_bat_status.setText("[系统电量]");
            }
        }
        updateRecvRedirectStatus();
        if (__jmp_ctl_cnt != null) {
            int cnt = JumpController.INSTANCE.getEffectiveRulesCount();
            if (cnt == -1) {
                __jmp_ctl_cnt.setText("[禁用]");
            } else {
                __jmp_ctl_cnt.setText("" + cnt);
            }
        }
        isVisible = true;
        startRgbIfEnabled();
    }

    @Override
    public void doOnPause() {
        isVisible = false;
        super.doOnPause();
    }

    private void startRgbIfEnabled() {
        if (!rgbEnabled || !isVisible) {
            return;
        }
        mRikkaTitle.setText("花Q[狐狸狸魔改版]");
        new Thread(this).start();
    }

    private void stopRgb() {
        isVisible = false;
    }

    public void onAddAccountClick(View v) {
        CustomDialog dialog = CustomDialog.createFailsafe(this);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setTextSize(16);
        int _5 = dip2px(SettingsActivity.this, 5);
        editText.setPadding(_5, _5, _5, _5);
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        AlertDialog alertDialog = (AlertDialog) dialog
            .setTitle("输入要添加的QQ号")
            .setView(linearLayout)
            .setPositiveButton("添加", null)
            .setNegativeButton("取消", null)
            .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v1 -> {
            String uinText = editText.getText().toString();
            long uin = -1;
            try {
                uin = Long.parseLong(uinText);
            } catch (NumberFormatException ignored) {
            }
            if (uin < 10000) {
                Toasts.error(SettingsActivity.this, "QQ号无效");
                return;
            }
            boolean success;
            File f = new File(getFilesDir(), "user/u_" + uin + "_t");
            try {
                success = f.createNewFile();
            } catch (IOException e) {
                Toasts.error(SettingsActivity.this,
                    e.toString().replaceAll("java\\.(lang|io)\\.", ""));
                return;
            }
            if (success) {
                Toasts.success(SettingsActivity.this, "已添加");
            } else {
                Toasts.info(SettingsActivity.this, "该账号已存在");
                return;
            }
            alertDialog.dismiss();
        });
    }

    public void onFileRecvRedirectClick(View v) {
        if (v.getId() == R_ID_BTN_FILE_RECV) {
            if (FileRecvRedirect.INSTANCE.checkPreconditions()) {
                showChangeRecvPathDialog();
            } else {
                new Thread(() -> {
                    doSetupForPrecondition(SettingsActivity.this, FileRecvRedirect.INSTANCE);
                    runOnUiThread(this::showChangeRecvPathDialog);
                }).start();
            }
        }
    }

    /**
     * 没良心的method
     */
    @Override
    public void run() {
        if (mRikkaTitle != null && mRikkaDesc != null && Looper.myLooper() == mainLooper) {
            if (rgbEnabled) {
                mRikkaTitle.setTextColor(color);
                mRikkaDesc.setTextColor(color);
            } else {
                mRikkaTitle.setText("花Q");
                mRikkaTitle.setTextColor(ResUtils.skin_black);
                mRikkaDesc.setTextColor(ResUtils.skin_gray3);
            }
            return;
        }
        while (isVisible && rgbEnabled) {
            try {
                Thread.sleep(75);
            } catch (InterruptedException ignored) {
            }
            step += 30;
            stage = (stage + step / 256) % 6;
            step = step % 256;
            switch (stage) {
                case 0:
                    color = Color.argb(255, 255, step, 0);//R-- RG-
                    break;
                case 1:
                    color = Color.argb(255, 255 - step, 255, 0);//RG- -G-
                    break;
                case 2:
                    color = Color.argb(255, 0, 255, step);//-G- -GB
                    break;
                case 3:
                    color = Color.argb(255, 0, 255 - step, 255);//-GB --B
                    break;
                case 4:
                    color = Color.argb(255, step, 0, 255);//--B R-B
                    break;
                case 5:
                    color = Color.argb(255, 255, 0, 255 - step);//R-B R--
                    break;
            }
            runOnUiThread(this);
        }
        runOnUiThread(this);
    }


    private void showChangeRecvPathDialog() {
        FileRecvRedirect recv = FileRecvRedirect.INSTANCE;
        String currPath = recv.getRedirectPath();
        if (currPath == null) {
            currPath = recv.getDefaultPath();
        }
        CustomDialog dialog = CustomDialog.createFailsafe(this);
        Context ctx = dialog.getContext();
        EditText editText = new EditText(ctx);
        editText.setText(currPath);
        editText.setTextSize(16);
        int _5 = dip2px(SettingsActivity.this, 5);
        editText.setPadding(_5, _5, _5, _5);
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        AlertDialog alertDialog = (AlertDialog) dialog
            .setTitle("输入重定向文件夹路径")
            .setView(linearLayout)
            .setPositiveButton("确认并激活", null)
            .setNegativeButton("取消", null)
            .setNeutralButton("使用默认值", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    recv.setEnabled(false);
                    updateRecvRedirectStatus();
                }
            })
            .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String path = editText.getText().toString();
                    if (path.equals("")) {
                        Toasts.error(SettingsActivity.this, "请输入路径");
                        return;
                    }
                    if (!path.startsWith("/")) {
                        Toasts.error(SettingsActivity.this, "请输入完整路径(以\"/\"开头)");
                        return;
                    }
                    File f = new File(path);
                    if (!f.exists() || !f.isDirectory()) {
                        Toasts.error(SettingsActivity.this, "文件夹不存在");
                        return;
                    }
                    if (!f.canWrite()) {
                        Toasts.error(SettingsActivity.this, "文件夹无访问权限");
                        return;
                    }
                    if (!path.endsWith("/")) {
                        path += "/";
                    }
                    recv.setRedirectPathAndEnable(path);
                    updateRecvRedirectStatus();
                    alertDialog.dismiss();
                }
            });
    }

    private void updateRecvRedirectStatus() {
        FileRecvRedirect recv = FileRecvRedirect.INSTANCE;
        if (recv.isEnabled()) {
            __recv_status.setText("[已启用]");
            __recv_desc.setText(recv.getRedirectPath());
        } else {
            __recv_status.setText("[禁用]");
            __recv_desc.setText(recv.getDefaultPath());
        }
    }

    private void showOverflowPopupMenu() {
        Toasts.info(this, "没有更多了");
    }

}
