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
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.ChatTailHook;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.hook.JumpController;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_CLASS_TEXT;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.ui.ViewBuilder.clickToProxyActAction;
import static nil.nadph.qnotified.util.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class ChatTailActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private static final int R_ID_APPLY = 0x300AFF81;
    private static final int R_ID_DISABLE = 0x300AFF82;
    private static final int R_ID_PERCENT_VALUE = 0x300AFF83;
    public static final String delimiter = "@";

    TextView tvStatus;

    private boolean mMsfResponsive = false;
    private TextView __tv_chat_tail_groups, __tv_chat_tail_friends;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(ChatTailActivity.this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(ChatTailActivity.this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(ChatTailActivity.this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(subtitle(ChatTailActivity.this, "在这里设置然后每次聊天自动添加"));
        ChatTailHook ct = ChatTailHook.get();
        boolean enabled = ct.isEnabled();
        RelativeLayout _s;
        LinearLayout _t;
        ll.addView(_t = subtitle(ChatTailActivity.this, ""));
        tvStatus = (TextView) _t.getChildAt(0);
        ll.addView(subtitle(ChatTailActivity.this, "默认不换行，换行符号请输入\\n"));
        ll.addView(subtitle(ChatTailActivity.this, ChatTailActivity.delimiter + "将会被替换为消息"));
        ll.addView(subtitle(ChatTailActivity.this, "回车发送不生效"));

        ll.addView(_s = newListItemButton(this, "选择生效的群", "未选择的群将不展示小尾巴", "N/A", clickToProxyActAction(ACTION_CHAT_TAIL_TROOPS_ACTIVITY)));
        __tv_chat_tail_groups = _s.findViewById(R_ID_VALUE);
        ll.addView(_s = newListItemButton(this, "选择生效的好友", "未选择的好友将不展示小尾巴", "N/A", clickToProxyActAction(ACTION_CHAT_TAIL_FRIENDS_ACTIVITY)));
        __tv_chat_tail_friends = _s.findViewById(R_ID_VALUE);

        ll.addView(subtitle(ChatTailActivity.this, "设置小尾巴(" + ChatTailActivity.delimiter + " 将会被替换为消息"));
        int _5dp = dip2px(ChatTailActivity.this, 5);
        EditText pct = new EditText(ChatTailActivity.this);
        pct.setId(R_ID_PERCENT_VALUE);
        pct.setInputType(TYPE_CLASS_TEXT);
        pct.setTextColor(ResUtils.skin_black);
        pct.setTextSize(dip2sp(ChatTailActivity.this, 18));
        pct.setBackgroundDrawable(null);
        pct.setGravity(Gravity.CENTER);
        pct.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        pct.setBackgroundDrawable(new HighContrastBorder());
        pct.setHint(ChatTailActivity.delimiter + "之前为前缀，" + ChatTailActivity.delimiter + "之后为后缀");
        pct.setText(ct.getTailCapacity().replace("\n","\\n"));
        pct.setSelection(pct.getText().length());
        ll.addView(pct, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        ll.addView(newListItemSwitchFriendConfigNext(this, "全局开关", "开启将无视生效范围(无需重启QQ)", ConfigItems.qn_chat_tail_global, false));
        Button apply = new Button(ChatTailActivity.this);
        apply.setId(R_ID_APPLY);
        apply.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(apply);
        ll.addView(apply, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        Button dis = new Button(ChatTailActivity.this);
        dis.setId(R_ID_DISABLE);
        dis.setOnClickListener(this);
        ResUtils.applyStyleCommonBtnBlue(dis);
        dis.setText("停用");
        ll.addView(dis, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        showStatus();
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("聊天小尾巴");
        return true;
    }

    @Override
    public void doOnResume() {
        super.doOnResume();
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        String str = cfg.getString(ConfigItems.qn_chat_tail_troops);
        int n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_chat_tail_groups.setText(n + "个群");
        str = cfg.getString(ConfigItems.qn_chat_tail_friends);
        n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_chat_tail_friends.setText(n + "个好友");
    }

    private void showStatus() {
        ChatTailHook ct = ChatTailHook.get();
        boolean enabled = ct.isEnabled();
        String desc = "当前状态: ";
        if (enabled) {
            desc += "已开启: " + ct.getTailCapacity().replace("\n", "");
        } else {
            desc += "禁用";
        }
        tvStatus.setText(desc);
        Button apply, disable;
        apply = ChatTailActivity.this.findViewById(R_ID_APPLY);
        disable = ChatTailActivity.this.findViewById(R_ID_DISABLE);
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
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        switch (v.getId()) {
            case R_ID_APPLY:
                //if (mMsfResponsive) {
                doUpdateTailCfg();
               /* } else {
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
                            doUpdateTailCfg();
                        }

                        @Override
                        public void onEnumResult(SyncUtils.EnumRequestHolder holder) {
                            if (mFinished) return;
                            mFinished = true;
                            mMsfResponsive = holder.result.size() > 0;
                            waitDialog.dismiss();
                            if (mMsfResponsive) {
                                doUpdateTailCfg();
                            } else {
                                CustomDialog.create(ChatTailActivity.this).setTitle("操作失败")
                                        .setCancelable(true).setPositiveButton("确认", null)
                                        .setMessage("发生错误:\n" + getApplication().getPackageName() + ":MSF 进程响应超时\n" +
                                                "如果您的QQ刚刚启动,您可以在十几秒后再试一次\n" +
                                                "如果您是太极(含无极)用户,请确认您的太极版本至少为 湛泸-6.0.2(1907) ,如低于此版本,请尽快升级").show();
                            }
                        }
                    });
                }

                */
                break;
            case R_ID_DISABLE:
                cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, false);
                try {
                    cfg.save();
                } catch (Exception e) {
                    Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                    log(e);
                }
                showStatus();
        }
    }

    private void doUpdateTailCfg() {
        ChatTailHook ct = ChatTailHook.get();
        ConfigManager cfg = ExfriendManager.getCurrent().getConfig();
        EditText pct;
        pct = ChatTailActivity.this.findViewById(R_ID_PERCENT_VALUE);
        String val = pct.getText().toString();
        if (Utils.isNullOrEmpty(val)) {
            Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "请输入小尾巴", Toast.LENGTH_SHORT);
            return;
        }
        if (!val.contains(ChatTailActivity.delimiter)) {
            Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "请在小尾巴中加入" + ChatTailActivity.delimiter + "", Toast.LENGTH_SHORT);
            return;
        }
        ct.setTail(val);
        if (!ct.isEnabled()) {
            cfg.putBoolean(ChatTailHook.qn_chat_tail_enable, true);
            try {
                cfg.save();
                //  boolean success = true;
                // if (!ct.isInited()) success = ct.init();
                //SyncUtils.requestInitHook(ct.getId(), ct.getEffectiveProc());
                //  if (!success)
                //   Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "初始化错误: 可能是版本不支持", Toast.LENGTH_SHORT);
            } catch (Exception e) {
                Utils.showToast(ChatTailActivity.this, TOAST_TYPE_ERROR, "错误:" + e.toString(), Toast.LENGTH_LONG);
                log(e);
            }
        }
        showStatus();
    }

}
