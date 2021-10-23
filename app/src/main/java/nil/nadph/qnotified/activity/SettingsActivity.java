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
import static nil.nadph.qnotified.ui.ViewBuilder.clickToProxyActAction;
import static nil.nadph.qnotified.ui.ViewBuilder.clickToUrl;
import static nil.nadph.qnotified.ui.ViewBuilder.doSetupForPrecondition;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButtonIfValid;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemConfigSwitchIfValid;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemSwitchConfigNext;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.PlayQQVersion.PlayQQ_8_2_9;
import static nil.nadph.qnotified.util.QQVersion.QQ_8_2_6;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.log;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.tencent.mobileqq.widget.BounceScrollView;

import java.io.File;
import java.io.IOException;

import cc.ioctl.activity.FakeBatCfgActivity;
import cc.ioctl.dialog.RikkaDialog;
import cc.ioctl.hook.FakeBatteryHook;
import cc.ioctl.hook.FileRecvRedirect;
import cc.ioctl.hook.JumpController;
import cn.lliiooll.hook.AntiMessage;
import me.ketal.hook.QWalletNoAD;
import me.ketal.hook.QZoneNoAD;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.drawable.HighContrastBorder;
import nil.nadph.qnotified.ui.widget.FunctionButton;
import nil.nadph.qnotified.util.NewsHelper;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.UpdateCheck;

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
        String _hostName = HostInfo.getHostInfo().getHostName();
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
        ViewGroup _t;
        try {
            LinearLayout nnn = subtitle(this, "");
            TextView t = (TextView) nnn.getChildAt(0);
            NewsHelper.getCachedNews(t);
            ll.addView(nnn);
            NewsHelper.asyncFetchNewsIfNeeded(t);
        } catch (Throwable e) {
            log(e);
        }
        ll.addView(subtitle(this, "基本功能"));
        if (HostInfo.requireMinQQVersion(QQ_8_2_6) || HostInfo
            .requireMinPlayQQVersion(PlayQQ_8_2_9)) {
            ll.addView(_t = newListItemButton(this, "自定义电量", "[QQ>=8.2.6]在线模式为我的电量时生效", "N/A",
                clickToProxyActAction(FakeBatCfgActivity.class)));
            __tv_fake_bat_status = ((FunctionButton) _t).getValue();
        }
        FunctionButton _tmp_vg = (FunctionButton) newListItemButton(this, "花Q", "若无另行说明, 所有功能开关都即时生效", null,
            v -> RikkaDialog.showRikkaFuncDialog(SettingsActivity.this));
        _tmp_vg.setOnLongClickListener(v -> {
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
        });
        mRikkaTitle = _tmp_vg.getTitle();
        mRikkaDesc = _tmp_vg.getDesc();
        ll.addView(_tmp_vg);
        ll.addView(
            _t = newListItemButton(this, "下载重定向", "N/A", "N/A", this::onFileRecvRedirectClick));
        _t.setId(R_ID_BTN_FILE_RECV);
        __recv_desc = ((FunctionButton) _t).getDesc();
        __recv_status = ((FunctionButton) _t).getValue();
        ll.addView(newListItemButtonIfValid(this, "静默指定类型消息通知", null, null, AntiMessage.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "隐藏空间好友热播和广告", null, QZoneNoAD.INSTANCE));
        ll.addView(newListItemConfigSwitchIfValid(this, "隐藏QQ钱包超值精选", null, QWalletNoAD.INSTANCE));
        ll.addView(subtitle(this, "好友列表"));
        ll.addView(newListItemConfigSwitchIfValid(this, "被删好友通知", "检测到你被好友删除后发出通知",
            ConfigItems.qn_notify_when_del));
        if (!HostInfo.isTim()) {
            ll.addView(newListItemSwitchConfigNext(this, "隐藏分组下方入口", "隐藏分组列表最下方的历史好友按钮",
                ConfigItems.qn_hide_ex_entry_group, false));
        }
        ll.addView(newListItemSwitchConfigNext(this, "禁用" + _hostName + "热补丁", "一般无需开启",
            ConfigItems.qn_disable_hot_patch));
        ll.addView(subtitle(this, "参数设定"));
        __jmp_ctl_cnt = ((FunctionButton) _t).getValue();
        ll.addView(subtitle(this, "关于"));
        UpdateCheck uc = new UpdateCheck();
        ll.addView(_t = newListItemButton(this, "更新通道", "自定义更新通道",
            uc.getCurrChannel(), null));
        _t.setOnClickListener(uc.listener(_t));
        ll.addView(_t = newListItemButton(this, "检查更新", null, "点击检查", uc));
        uc.setVersionTip(_t);
        ll.addView(subtitle(this, "调试"));
        ll.addView(newListItemButton(this, "Shell.exec", "正常情况下无需使用此功能", null, clickTheComing()));
        ll.addView(
            newListItemButton(this, "Github", "获取源代码 Bug -> Issue (star)", "ferredoxin/QNotified",
                clickToUrl("https://github.com/ferredoxin/QNotified")));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setContentBackgroundDrawable(ResUtils.skin_background);
        setRightButton("搜索", new View.OnClickListener() {
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
                .setNeutralButton("重启" + HostInfo.getHostInfo().getHostName(),
                    (dialog, which) -> {
                        try {
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } catch (Throwable e1) {
                            log(e1);
                        }
                    })
                .setMessage(
                    "Resources injection failure!\nApplication may misbehave.\n" + e.toString()
                        + "\n如果您刚刚更新了插件, 您可能需要重启" + HostInfo.getHostInfo()
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
        Toasts.info(this, "Coming soon...");
    }

}
