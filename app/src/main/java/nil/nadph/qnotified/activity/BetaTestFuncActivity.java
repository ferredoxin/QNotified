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
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mobileqq.widget.BounceScrollView;

import nil.nadph.qnotified.hook.ChatTailHook;
import nil.nadph.qnotified.hook.MutePokePacket;
import nil.nadph.qnotified.hook.PttForwardHook;
import nil.nadph.qnotified.hook.kyuubiran.*;
import nil.nadph.qnotified.hook.rikka.RemoveMiniProgramAd;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.ui.ViewBuilder.clickToProxyActAction;
import static nil.nadph.qnotified.util.ActProxyMgr.ACTION_CHAT_TAIL_CONFIG_ACTIVITY;
import static nil.nadph.qnotified.util.Utils.dip2px;

@SuppressLint("Registered")
public class BetaTestFuncActivity extends IphoneTitleBarActivityCompat {

    TextView __tv_chat_tail_status;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        RelativeLayout _t;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
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
        if (!LicenseStatus.getAuth2Status()) {
            TextView tv = new TextView(this);
            tv.setText("你是怎么进来的???????????????????");
            tv.setTextColor(ResUtils.skin_red);
            tv.setTextSize(30);
            ll.addView(tv, MATCH_PARENT, WRAP_CONTENT);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        BetaTestFuncActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            ll.addView(subtitle(this, "Beta测试功能 仅用于测试稳定性[可能会存在BUG 包括但不限于功能不生效、QQ出现卡顿乃至QQ闪退 请酌情开启]"));
            ll.addView(newListItemSwitchConfig(this, "保存语音", "需要打开语音转发才能使用本功能", PttForwardHook.qn_enable_ptt_save, false));
            ll.addView(_t = newListItemButton(this, "自定义聊天小尾巴", "回车发送不生效", "N/A", clickToProxyActAction(ACTION_CHAT_TAIL_CONFIG_ACTIVITY)));
            __tv_chat_tail_status = _t.findViewById(R_ID_VALUE);
            ll.addView(newListItemHookSwitchInit(this, "屏蔽戳一戳", "OvO", MutePokePacket.get()));
//            ll.addView(newListItemHookSwitchInit(this, "昵称/群名字打码", "娱乐功能", AutomaticMosaicName.INSTANCE));
//            ll.addView(newListItemHookSwitchInit(this, "侧滑精简", "芜湖~重启生效", TestQQMe.INSTANCE));
//            ll.addView(newListItemHookSwitchInit(this, "群应用爬", "不会真有人用群应用吧 不会吧不会吧", RemoveGroupApp.INSTANCE));
        }
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;

        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        String text = ChatTailHook.get().isEnabled() ? ChatTailHook.get().getTailCapacity().replace("\n", "") : "[未启用]";
        if (text.length() > 3) {
            // 避免过长影响美观
            text = "..." + text.substring(text.length() - 3);
        }
        __tv_chat_tail_status.setText(text);
    }
}
