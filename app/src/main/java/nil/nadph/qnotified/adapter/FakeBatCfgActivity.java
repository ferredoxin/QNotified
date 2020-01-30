package nil.nadph.qnotified.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.ui.DebugDrawable;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class FakeBatCfgActivity implements ActivityAdapter, View.OnClickListener {
    private Activity self;

    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_DISABLE = 0x300AFF82;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;
    private static final int R_ID_CHARGING = 0x300AFF84;
    private static final int R_ID_FAK_BAT_STATUS = 0x300AFF85;

    TextView tvStatus;

    public FakeBatCfgActivity(Activity activity) {
        self = activity;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        LinearLayout ll = new LinearLayout(self);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(self);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = (ViewGroup) new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"), self, null, Context.class, AttributeSet.class);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(self, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(self, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(self, "!!! 此功能仅在 QQ>=6.2.8 且在线状态为 我的电量 时生效"));
        ll.addView(subtitle(self, "服务器的电量数据有6分钟的延迟属于正常情况"));
        FakeBatteryHook bat = FakeBatteryHook.get();
        boolean enabled = bat.isEnabled();
        LinearLayout _t;
        ll.addView(_t = subtitle(self, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(self, "设置自定义电量百分比:"));
        int _5dp = dip2px(self, 5);
        EditText pct = new EditText(self);
        pct.setId(R_ID_PERCENT_VALUE);
        pct.setInputType(TYPE_CLASS_NUMBER);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(self, 18));
        pct.setBackgroundDrawable(null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        pct.setBackgroundDrawable(new DebugDrawable(self));
        pct.setHint("电量百分比, 取值范围 [0,100]");
        pct.setText(bat.getFakeBatteryCapacity() + "");
        ll.addView(pct, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        CheckBox charging = new CheckBox(self);
        charging.setId(R_ID_CHARGING);
        charging.setText("正在充电");
        charging.setTextSize(17);
        charging.setTextColor(ResUtils.skin_black);
        charging.setButtonDrawable(ResUtils.getCheckBoxBackground());
        charging.setPadding(_5dp, _5dp, _5dp, _5dp);
        charging.setChecked(FakeBatteryHook.get().isFakeBatteryCharging());
        ll.addView(charging, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 3 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button apply = new Button(self);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button dis = new Button(self);
        dis.setId(R_ID_DISABLE);
        dis.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(dis);
        dis.setText("停用");
        ll.addView(dis, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        showStatus();
        ActProxyMgr.setContentBackgroundDrawable(self, ResUtils.skin_background);
        invoke_virtual(self, "setTitle", "自定义电量", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
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
        apply = (Button) self.findViewById(R_ID_APPLY);
        disable = (Button) self.findViewById(R_ID_DISABLE);
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
        FakeBatteryHook bat = FakeBatteryHook.get();
        EditText pct;
        CheckBox charging;
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        int val;
        switch (v.getId()) {
            case R_ID_APPLY:
                pct = (EditText) self.findViewById(R_ID_PERCENT_VALUE);
                charging = (CheckBox) self.findViewById(R_ID_CHARGING);
                try {
                    val = Integer.parseInt(pct.getText().toString());
                } catch (NumberFormatException e) {
                    Utils.showToast(self, TOAST_TYPE_ERROR, "请输入电量", Toast.LENGTH_SHORT);
                    return;
                }
                if (val < 0 || val > 100) {
                    Utils.showToast(self, TOAST_TYPE_ERROR, "电量取值范围: [0,100]", Toast.LENGTH_SHORT);
                    return;
                }
                if (charging.isChecked()) val |= 128;
                bat.setFakeBatteryStatus(val);
                if (!bat.isEnabled()) {
                    cfg.putBoolean(qn_fake_bat_enable, true);
                    try {
                        cfg.save();
                        if (!bat.isInited()) bat.init();
                        SyncUtils.requestInitHook(bat.getId(), bat.getEffectiveProc());
                    } catch (Exception e) {
                        Utils.showToast(self, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                        log(e);
                    }
                }
                showStatus();
                break;
            case R_ID_DISABLE:
                cfg.putBoolean(qn_fake_bat_enable, false);
                try {
                    cfg.save();
                } catch (Exception e) {
                    Utils.showToast(self, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                    log(e);
                }
                showStatus();
        }
    }

    @Override
    public void doOnPostResume() throws Throwable {
    }

    @Override
    public void doOnPostPause() throws Throwable {
    }

    @Override
    public void doOnPostDestory() throws Throwable {
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) throws Throwable {
    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }
}