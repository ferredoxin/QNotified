/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.SimpleBgDrawable;
import nil.nadph.qnotified.util.NewsHelper;
import nil.nadph.qnotified.util.UpdateCheck;
import nil.nadph.qnotified.util.Utils;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.ActProxyMgr.*;
import static nil.nadph.qnotified.util.SendBatchMsg.clickToBatchMsg;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class SettingsActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private static final int R_ID_BTN_FILE_RECV = 0x300AFF91;

    private TextView __tv_muted_atall, __tv_muted_redpacket, __tv_fake_bat_status, __recv_status, __recv_desc, __js_status;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //bounceScrollView.setBackgroundDrawable(ResUtils.qq_setting_item_bg_nor);
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
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
        ll.addView(subtitle(this, "遗留功能"));//群发已不再维护
        ll.addView(newListItemButton(this, "群发文本消息", "年少不知号贵-理性使用以免永冻", null, clickToBatchMsg()));
        ll.addView(subtitle(this, "基本功能"));
        if (!Utils.isTim(this)) {
            ll.addView(_t = newListItemButton(this, "自定义电量", "[QQ>=8.2.6]在线模式为我的电量时生效", "N/A", clickToProxyActAction(ACTION_FAKE_BAT_CONFIG_ACTIVITY)));
            __tv_fake_bat_status = (TextView) _t.findViewById(R_ID_VALUE);
        }
        ll.addView(newListItemHookSwitchInit(this, "语音转发", "长按语音消息", PttForwardHook.get()));
        ll.addView(newListItemHookSwitchInit(this, "发送卡片消息", "ArkAppMsg(json)+StructMsg(xml)", CardMsgHook.get()));
        ll.addView(subtitle(this, "卡片消息使用说明:先输入卡片代码(聊天界面),后长按发送按钮\n勿滥用此功能! 频繁使用此功能被举报可能封号"));
        ll.addView(newListItemHookSwitchInit(this, " +1", "不是复读机", RepeaterHook.get()));
        ll.addView(subtitle(this, "净化设置"));
        if (!Utils.isTim(this)) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏小程序入口", "隐藏消息列表下拉出现的小程序列表", ConfigItems.qn_hide_msg_list_miniapp, false));
            ll.addView(newListItemHookSwitchInit(this, "隐藏送礼动画", null, HideGiftAnim.get()));
            ll.addView(newListItemHookSwitchInit(this, "禁止回复自动@", "[>=8.1.3]去除回复消息时自动@特性", ReplyNoAtHook.get()));
            ll.addView(newListItemHookSwitchInit(this, "禁用$打开送礼界面", "禁止聊天时输入$自动弹出[选择赠送对象]窗口", $endGiftHook.get()));
            ll.addView(newListItemHookSwitchInit(this, "强制使用默认气泡", "无视个性聊天气泡", DefaultBubbleHook.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "签到文本化", null, SimpleCheckInHook.get()));
        ll.addView(subtitle(this, "消息通知设置(不影响接收消息)屏蔽后可能仍有[橙字],但通知栏不会有通知,赞说说不提醒仅屏蔽通知栏的通知"));
        ll.addView(subtitle(this, "    注:屏蔽后可能仍有[橙字],但不会有通知"));
        ll.addView(_t = newListItemButton(this, "屏蔽指定群@全体成员通知", Html.fromHtml("<font color='" + get_RGB(hiColor.getDefaultColor()) + "'>[@全体成员]</font>就这点破事"), "%d个群", clickToProxyActAction(ACTION_MUTE_AT_ALL)));
        __tv_muted_atall = (TextView) _t.findViewById(R_ID_VALUE);
        ll.addView(_t = newListItemButton(this, "屏蔽指定群的红包通知", Html.fromHtml("<font color='" + get_RGB(hiColor.getDefaultColor()) + "'>[QQ红包][有红包]</font>恭喜发财"), "%d个群", clickToProxyActAction(ACTION_MUTE_RED_PACKET)));
        __tv_muted_redpacket = (TextView) _t.findViewById(R_ID_VALUE);
        ll.addView(newListItemHookSwitchInit(this, "赞说说不提醒", "不影响评论,转发或击掌的通知", MuteQZoneThumbsUp.get()));
        ll.addView(subtitle(this, "图片相关"));
        ll.addView(newListItemHookSwitchInit(this, "以图片方式打开闪照", null, FlashPicHook.get()));
        if (!Utils.isTim(this)) {
            ll.addView(newListItemHookSwitchInit(this, "禁止秀图自动展示", null, ShowPicGagHook.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "转发消息点击头像查看资料卡", "长按头像可查看详细信息", MultiForwardAvatarHook.get()));
        if (!Utils.isTim(this)) {
            ll.addView(newListItemHookSwitchInit(this, "以图片方式打开表情", null, EmoPicHook.get()));
        }
        ll.addView(newListItemHookSwitchInit(this, "防撤回", "自带撤回灰字提示", RevokeMsgHook.get()));
        //ll.addView(newListItemSwitchConfigInit(this, "聊天图片背景透明", null, qn_gallery_bg, false, GalleryBgHook.get()));
        ll.addView(subtitle(this, "实验性功能(未必有效)"));
        ll.addView(newListItemHookSwitchInit(this, "收藏更多表情", "[暂不支持>=8.2.0]保存在本地", FavMoreEmo.get()));
        ll.addView(newListItemHookSwitchInit(this, "屏蔽更新提醒", null, PreUpgradeHook.get()));
        if (!Utils.isTim(this)) {
            ll.addView(newListItemHookSwitchInit(this, "自定义猜拳骰子", null, CheatHook.get()));
            ll.addView(newListItemHookSwitchInit(this, "简洁模式圆头像", "From Rikka", RoundAvatarHook.get()));
        }
        ll.addView(subtitle(this, "好友列表"));
        ll.addView(newListItemButton(this, "历史好友", null, null, clickToProxyActAction(ACTION_EXFRIEND_LIST)));
        ll.addView(newListItemButton(this, "导出历史好友列表", "支持csv/json格式", null, clickToProxyActAction(ACTION_FRIENDLIST_EXPORT_ACTIVITY)));
        ll.addView(newListItemConfigSwitchIfValid(this, "被删好友通知", "检测到你被好友删除后发出通知", ConfigItems.qn_notify_when_del));
        if (!Utils.isTim(this)) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏分组下方入口", "隐藏分组列表最下方的历史好友按钮", ConfigItems.qn_hide_ex_entry_group, false));
        }
        ll.addView(_t = newListItemButton(this, "下载重定向[不支持>=8.2.8]", "N/A", "N/A", this));
        _t.setId(R_ID_BTN_FILE_RECV);
        __recv_desc = (TextView) _t.findViewById(R_ID_DESCRIPTION);
        __recv_status = (TextView) _t.findViewById(R_ID_VALUE);
        ll.addView(subtitle(this, "还没完成的功能(咕咕咕)"));
        ll.addView(newListItemSwitchConfigStub(this, "屏蔽回执消息的通知", null, ConfigItems.qn_mute_talk_back, false));
        ll.addView(newListItemSwitchConfigStub(this, "禁用QQ热补丁", "一般无需开启", ConfigItems.qn_disable_qq_hot_patch, false));
        ll.addView(subtitle(this, "参数设定"));
        ll.addView(newListItemButton(this, "小尾巴", "请勿在多个模块同时开启小尾巴", "[无]", clickTheComing()));
        ll.addView(newListItemSwitchStub(this, "禁用特别关心长震动", "等我找到女朋友就开发这个功能", false));
        ll.addView(newListItemButton(this, "AddFriendReq.sourceID", "自定义加好友来源", "[不改动]", clickTheComing()));
        ll.addView(newListItemButton(this, "DelFriendReq.delType", "只能为1或2", "[不改动]", clickTheComing()));
        ll.addView(_t = newListItemButton(this, "管理脚本(.js/.java)", "请注意安全, 合理使用", "N/A", clickToProxyActAction(ManageScriptsActivity.class)));
        __js_status = (TextView) _t.findViewById(R_ID_VALUE);
        ll.addView(subtitle(this, "关于"));
        PackageInfo pi = Utils.getHostInfo(this);
        ll.addView(newListItemDummy(this, pi.applicationInfo.loadLabel(this.getPackageManager()), null, pi.versionName + "(" + pi.versionCode + ")"));
        ll.addView(newListItemDummy(this, "模块版本", null, Utils.QN_VERSION_NAME));
        UpdateCheck uc = new UpdateCheck();
        ll.addView(_t = newListItemButton(this, "检查更新", null, "点击检查", uc));
        uc.setVersionTip(_t);
        ll.addView(newListItemButton(this, "关于模块", null, null, clickToProxyActAction(ACTION_ABOUT)));
        ll.addView(subtitle(this, "调试"));
        ll.addView(newListItemButton(this, "故障排查", null, null, clickToProxyActAction(ACTION_TROUBLESHOOT_ACTIVITY)));
        ll.addView(newListItemButton(this, "Shell.exec", "正常情况下无需使用此功能", null, clickTheComing()));
        ll.addView(newListItemButton(this, "捐赠", "请选择扶贫方式", null, clickToProxyActAction(ACTION_DONATE_ACTIVITY)));
        ll.addView(newListItemButton(this, "Github", "获取源代码 Bug -> Issue (star)", "cinit/QNotified", clickToUrl("https://github.com/cinit/QNotified")));
        ll.addView(subtitle(this, "本软件为免费软件,请尊重个人劳动成果,严禁倒卖\nLife feeds on negative entropy."));
        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("高级");
        //TextView rightBtn=(TextView)invoke_virtual(this,"getRightTextView");
        //log("Title:"+invoke_virtual(this,"getTextTitle"));
        return true;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        String str = cfg.getString(ConfigItems.qn_muted_at_all);
        int n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_atall.setText(n + "个群");
        str = cfg.getString(ConfigItems.qn_muted_red_packet);
        n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_redpacket.setText(n + "个群");
        if (__tv_fake_bat_status != null) {
            FakeBatteryHook bat = FakeBatteryHook.get();
            if (bat.isEnabled()) {
                int cap = bat.getFakeBatteryCapacity();
                boolean c = bat.isFakeBatteryCharging();
                __tv_fake_bat_status.setText(cap + (c ? "%+ " : "% "));
            } else {
                __tv_fake_bat_status.setText("[系统电量]");
            }
        }
        updateRecvRedirectStatus();
        __js_status.setText("0/1");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R_ID_BTN_FILE_RECV) {
            if (FileRecvRedirect.get().checkPreconditions()) {
                showChangeRecvPathDialog();
            } else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        doSetupForPrecondition(SettingsActivity.this, FileRecvRedirect.get());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showChangeRecvPathDialog();
                            }
                        });
                    }
                }).start();
            }
        }
    }

    private void showChangeRecvPathDialog() {
        final FileRecvRedirect recv = FileRecvRedirect.get();
        String currPath = recv.getRedirectPath();
        if (currPath == null) currPath = recv.getDefaultPath();
        final EditText editText = new EditText(SettingsActivity.this);
        editText.setText(currPath);
        editText.setTextColor(Color.BLACK);
        editText.setTextSize(16);
        int _5 = dip2px(SettingsActivity.this, 5);
        editText.setPadding(_5, _5, _5, _5);
        editText.setBackgroundDrawable(new SimpleBgDrawable(0, 0x80000000, 1));
        LinearLayout linearLayout = new LinearLayout(SettingsActivity.this);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        final AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
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
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                if (path.equals("")) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入路径", Toast.LENGTH_SHORT);
                    return;
                }
                if (!path.startsWith("/")) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "请输入完整路径(以\"/\"开头)", Toast.LENGTH_SHORT);
                    return;
                }
                File f = new File(path);
                if (!f.exists() || !f.isDirectory()) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "文件夹不存在", Toast.LENGTH_SHORT);
                    return;
                }
                if (!f.canWrite()) {
                    showToast(SettingsActivity.this, TOAST_TYPE_ERROR, "文件夹无访问权限", Toast.LENGTH_SHORT);
                    return;
                }
                if (!path.endsWith("/")) path += "/";
                recv.setRedirectPathAndEnable(path);
                updateRecvRedirectStatus();
                alertDialog.dismiss();
            }
        });
    }

    private void updateRecvRedirectStatus() {
        FileRecvRedirect recv = FileRecvRedirect.get();
        if (recv.isEnabled()) {
            __recv_status.setText("[已启用]");
            __recv_desc.setText(recv.getRedirectPath());
        } else {
            __recv_status.setText("[禁用]");
            __recv_desc.setText(recv.getDefaultPath());
        }
    }

}
