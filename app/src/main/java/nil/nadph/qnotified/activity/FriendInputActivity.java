/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;

import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.dip2sp;

@SuppressLint("Registered")
public class FriendInputActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;

    TextView tvStatus;

    private boolean mMsfResponsive = false;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(FriendInputActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(FriendInputActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(FriendInputActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(FriendInputActivity.this, "在这里设置生效的好友"));
        RelativeLayout _s;
        LinearLayout _t;
        ll.addView(_t = subtitle(FriendInputActivity.this, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(FriendInputActivity.this, "用,分开(英文符号)"));
        int _5dp = dip2px(FriendInputActivity.this, 5);
        EditText pct = new EditText(FriendInputActivity.this);
        pct.setId(R_ID_PERCENT_VALUE);
        pct.setInputType(TYPE_CLASS_TEXT);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(FriendInputActivity.this, 18));
        pct.setBackgroundDrawable(null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        pct.setBackgroundDrawable(new HighContrastBorder());
        pct.setHint("输入生效好友号码，使用,分开");
        pct.setText(ConfigManager.getDefaultConfig().getString(ConfigItems.qn_chat_tail_friends));
        pct.setSelection(pct.getText().length());
        ll.addView(pct, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button apply = new Button(FriendInputActivity.this);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        showStatus();
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("设置生效好友");
        return true;
    }

    private void showStatus() {
        Button apply;
        apply = FriendInputActivity.this.findViewById(R_ID_APPLY);
        apply.setText("确认");
    }

    @Override
    public void onClick(View v) {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        switch (v.getId()) {
            case R_ID_APPLY:
                if (mMsfResponsive) {
                    doUpdateFriendCfg();
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
                            doUpdateFriendCfg();
                        }

                        @Override
                        public void onEnumResult(SyncUtils.EnumRequestHolder holder) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = holder.result.size() > 0;
                            waitDialog.dismiss();
                            if (mMsfResponsive) {
                                doUpdateFriendCfg();
                            } else {
                                CustomDialog.create(FriendInputActivity.this).setTitle("操作失败")
                                        .setCancelable(true).setPositiveButton("确认", null)
                                        .setMessage("发生错误:\n" + getApplication().getPackageName() + ":MSF 进程响应超时\n" +
                                                "如果您的QQ刚刚启动,您可以在十几秒后再试一次\n" +
                                                "如果您是太极(含无极)用户,请确认您的太极版本至少为 湛泸-6.0.2(1907) ,如低于此版本,请尽快升级").show();
                            }
                        }
                    });
                }

        }
    }

    private void doUpdateFriendCfg() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        EditText pct;
        pct = FriendInputActivity.this.findViewById(R_ID_PERCENT_VALUE);
        String val = pct.getText().toString().replace("，", ",").replace("\n", "").replace(" ", "");
        cfg.putString(ConfigItems.qn_chat_tail_friends, val);
    }

}
