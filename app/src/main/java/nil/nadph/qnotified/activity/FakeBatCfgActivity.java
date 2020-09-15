/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.core.view.ViewCompat;

import com.tencent.mobileqq.widget.BounceScrollView;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class FakeBatCfgActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_DISABLE = 0x300AFF82;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;
    private static final int R_ID_CHARGING = 0x300AFF84;
    private static final int R_ID_FAK_BAT_STATUS = 0x300AFF85;

    TextView tvStatus;

    private boolean mMsfResponsive = false;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(FakeBatCfgActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(FakeBatCfgActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(FakeBatCfgActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(FakeBatCfgActivity.this, "!!! 此功能仅在 QQ>=8.2.6 且在线状态为 我的电量 时生效"));
        ll.addView(subtitle(FakeBatCfgActivity.this, "服务器的电量数据有6分钟的延迟属于正常情况"));
        ll.addView(subtitle(FakeBatCfgActivity.this, "请不要把电量设置为 0 ,因为 0 会被TX和谐掉"));
        FakeBatteryHook bat = FakeBatteryHook.get();
        boolean enabled = bat.isEnabled();
        LinearLayout _t;
        ll.addView(_t = subtitle(FakeBatCfgActivity.this, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(FakeBatCfgActivity.this, "设置自定义电量百分比:"));
        int _5dp = dip2px(FakeBatCfgActivity.this, 5);
        EditText pct = new EditText(FakeBatCfgActivity.this);
        pct.setId(R_ID_PERCENT_VALUE);
        pct.setInputType(TYPE_CLASS_NUMBER);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(FakeBatCfgActivity.this, 18));
        //pct.setBackgroundDrawable(null);
        ViewCompat.setBackground(pct,null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        //pct.setBackgroundDrawable(new HighContrastBorder());
        ViewCompat.setBackground(pct,new HighContrastBorder());
        pct.setHint("电量百分比, 取值范围 [1,100]");
        pct.setText(bat.getFakeBatteryCapacity() + "");
        pct.setSelection(pct.getText().length());
        ll.addView(pct, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        CheckBox charging = new CheckBox(FakeBatCfgActivity.this);
        charging.setId(R_ID_CHARGING);
        charging.setText("正在充电");
        charging.setTextSize(17);
        charging.setTextColor(ResUtils.skin_black);
        charging.setButtonDrawable(ResUtils.getCheckBoxBackground());
        charging.setPadding(_5dp, _5dp, _5dp, _5dp);
        charging.setChecked(FakeBatteryHook.get().isFakeBatteryCharging());
        ll.addView(charging, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 3 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button apply = new Button(FakeBatCfgActivity.this);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button dis = new Button(FakeBatCfgActivity.this);
        dis.setId(R_ID_DISABLE);
        dis.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(dis);
        dis.setText("停用");
        ll.addView(dis, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        showStatus();
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("自定义电量");
        return true;
    }

    private void showStatus() {
        FakeBatteryHook bat = FakeBatteryHook.get();
        boolean enabled = bat.isEnabled();
        String desc = "当前状态: ";
        if (enabled) {
            desc += "已开启 " + bat.getFakeBatteryCapacity() + "%";
            if (bat.isFakeBatteryCharging()) desc += "+";
        } else {
            desc += "禁用";
        }
        tvStatus.setText(desc);
        Button apply, disable;
        apply = FakeBatCfgActivity.this.findViewById(R_ID_APPLY);
        disable = FakeBatCfgActivity.this.findViewById(R_ID_DISABLE);
        if (!enabled) {
            apply.setText("保存并启用");
        } else {
            apply.setText("确认");
        }
        if (!enabled) {
            disable.setVisibility(View.GONE);
        } else {
            disable.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        switch (v.getId()) {
            case R_ID_APPLY:
                if (mMsfResponsive) {
                    doUpdateBatCfg();
                } else {
                    final Dialog waitDialog = CustomDialog.create(this).setCancelable(true).setTitle("请稍候")
                            .setMessage("等待 :MSF 进程响应").show();
                    SyncUtils.enumerateProc(this, SyncUtils.PROC_MSF, 3000, new SyncUtils.EnumCallback() {
                        private boolean mFinished = false;

                        @Override
                        public void onResponse(SyncUtils.EnumRequestHolder holder, SyncUtils.ProcessInfo process) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = true;
                            waitDialog.dismiss();
                            doUpdateBatCfg();
                        }

                        @Override
                        public void onEnumResult(SyncUtils.EnumRequestHolder holder) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = holder.result.size() > 0;
                            waitDialog.dismiss();
                            if (mMsfResponsive) {
                                doUpdateBatCfg();
                            } else {
                                CustomDialog.create(FakeBatCfgActivity.this).setTitle("操作失败")
                                        .setCancelable(true).setPositiveButton("确认", null)
                                        .setMessage("发生错误:\n" + getApplication().getPackageName() + ":MSF 进程响应超时\n" +
                                                "如果您的" + Utils.getHostAppName() + "刚刚启动,您可以在十几秒后再试一次\n" +
                                                "如果您是太极(含无极)用户,请确认您的太极版本至少为 湛泸-6.0.2(1907) ,如低于此版本,请尽快升级").show();
                            }
                        }
                    });
                }
                break;
            case R_ID_DISABLE:
                cfg.putBoolean(FakeBatteryHook.qn_fake_bat_enable, false);
                try {
                    cfg.save();
                } catch (Exception e) {
                    Utils.showToast(FakeBatCfgActivity.this, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                    log(e);
                }
                showStatus();
        }
    }

    private void doUpdateBatCfg() {
        FakeBatteryHook bat = FakeBatteryHook.get();
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        EditText pct;
        CheckBox charging;
        int val;
        pct = FakeBatCfgActivity.this.findViewById(R_ID_PERCENT_VALUE);
        charging = FakeBatCfgActivity.this.findViewById(R_ID_CHARGING);
        try {
            val = Integer.parseInt(pct.getText().toString());
        } catch (NumberFormatException e) {
            Utils.showToast(FakeBatCfgActivity.this, TOAST_TYPE_ERROR, "请输入电量", Toast.LENGTH_SHORT);
            return;
        }
        if (val < 0 || val > 100) {
            Utils.showToast(FakeBatCfgActivity.this, TOAST_TYPE_ERROR, "电量取值范围: [1,100]", Toast.LENGTH_SHORT);
            return;
        }
        if (charging.isChecked()) val |= 128;
        bat.setFakeSendBatteryStatus(val);
        if (!bat.isEnabled()) {
            cfg.putBoolean(FakeBatteryHook.qn_fake_bat_enable, true);
            try {
                cfg.save();
                boolean success = true;
                if (!bat.isInited()) success = bat.init();
                SyncUtils.requestInitHook(bat.getId(), bat.getEffectiveProc());
                if (!success)
                    Utils.showToast(FakeBatCfgActivity.this, TOAST_TYPE_ERROR, "初始化错误: 可能是版本不支持", Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utils.showToast(FakeBatCfgActivity.this, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                log(e);
            }
        }
        showStatus();
    }

}
