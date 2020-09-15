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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.mobileqq.widget.BounceScrollView;

import java.net.URLEncoder;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class DonateActivity extends IphoneTitleBarActivityCompat {

    public static final String qn_donated_choice = "qn_donated_choice";

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
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

        ll.addView(subtitle(this, "QNotified是开源软件,完全免费,无需任何授权/卡密/加群即可使用全部功能,没有卡密或者授权这类的东西,请勿上当受骗!!!"));
        ll.addView(subtitle(this, "如果你希望支持作者, 保持更新的动力, 可请使用以下方式捐赠, 完成后手动打开 [我已捐赠] 即可"));
        ll.addView(subtitle(this, "免费开发不易, 需要花费很多个人精力, 且回报甚微, 甚至被人盗卖, 感谢理解"));
        RelativeLayout iHaveDonated = newListItemSwitchConfig(this, "我已捐赠", null, qn_donated_choice, false);
        ((CompoundButton) iHaveDonated.findViewById(R_ID_SWITCH)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                try {
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    cfg.putBoolean(qn_donated_choice, isChecked);
                    cfg.save();
                    if (isChecked) {
                        showToast(DonateActivity.this, TOAST_TYPE_SUCCESS, "感谢您的支持", Toast.LENGTH_SHORT);
                    }
                } catch (Throwable e) {
                    log(e);
                    showToast(DonateActivity.this, TOAST_TYPE_ERROR, "出了点问题:" + e, Toast.LENGTH_SHORT);
                }
            }
        });
        ll.addView(iHaveDonated);
        ll.addView(subtitle(this, "感谢您的支持!"));
        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "目前还存在一个较为严重的问题:"));
        ll.addView(subtitle(this, "  虽然QNotified是一个完全免费的插件,但仍因有不少人对本软件进行二次贩卖(如:以群发器的名义贩卖),不少人上当受骗"));
        ll.addView(subtitle(this, "从用户反馈来看,这种贩卖情况并非个例"));
        ll.addView(subtitle(this, "这违背了我的本意: **我希望任何个人都能免费的使用本软件**"));
        ll.addView(subtitle(this, "从根本上说,如果任何需要本软件的人都能免费地获取本软件,那么倒卖这种的情况就不会发生"));
        ll.addView(subtitle(this, "所以"));
        ll.addView(subtitle(this, "每多一个人免费地分发本软件,可能因贩卖上当的人就少一个"));
        ll.addView(subtitle(this, "譬如说,可以在各大玩机论坛社区以资源分享的方式分发免费软件(包括但不限于本模块,尽量别设置回复可见)"));
        ll.addView(subtitle(this, "当然以上只是其中一种方法"));
        ll.addView(subtitle(this, "本软件首发地为 https://github.com/ferredoxin/QNotified (求star/issue/pull request)"));
        ll.addView(subtitle(this, "最后,谢谢你的支持"));
        ll.addView(subtitle(this, "by"));
        ll.addView(newListItemButton(this, "更新频道", null, "@QNotified", clickToUrl("https://t.me/QNotified")));
        ll.addView(newListItemButton(this, "交流群", null, "@QNotifiedChat", clickToUrl("https://t.me/QNotifiedChat")));
        ll.addView(newListItemButton(this, "Telegram", "点击私信", "Auride", clickToUrl("https://t.me/Auride")));
        ll.addView(newListItemButton(this, "Mail", null, "xenonhydride@gmail.com", null));
        ll.addView(subtitle(this, "扶贫方式"));
        if (isNiceUser()) {
            ll.addView(newListItemButton(this, "支付宝", null, null, clickToAlipay()));
            ll.addView(newListItemButton(this, "微信支付", null, null, clickToFxxkWxpay()));
        }
        ll.addView(newListItemButton(this, "Bitcoin", null, null, clickToBtc()));

        ll.addView(subtitle(this, "FAQ1:"));
        ll.addView(subtitle(this, "Q: 捐赠后能解锁隐藏功能吗?"));
        ll.addView(subtitle(this, "A: 不能. 所有功能全部都是可以白嫖的"));
        ll.addView(subtitle(this, "FAQ2:"));
        ll.addView(subtitle(this, "Q: 我捐赠过,但是" + Utils.getHostAppName() + "数据被清除后没了怎么办"));
        ll.addView(subtitle(this, "A: 直接打开 我已捐赠 即可"));
        ll.addView(subtitle(this, "FAQ3:"));
        ll.addView(subtitle(this, "Q: 已知 我已捐赠 这个Switch开和关没有任何区别,那这个开关意义何在"));
        ll.addView(subtitle(this, "A: 和B站上up主的明示投币一个道理"));
        ll.addView(subtitle(this, "FAQ4:"));
        ll.addView(subtitle(this, "Q: 为什么不加个付费验证,然后收费?"));
        ll.addView(subtitle(this, "A: 开源软件搞什么付费验证"));

        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        //sdlv.setBackgroundColor(0xFFAA0000)
        setTitle("捐赠");
        setContentBackgroundDrawable(ResUtils.skin_background);
        //TextView rightBtn=(TextView)invoke_virtual(this,"getRightTextView");
        //log("Title:"+invoke_virtual(this,"getTextTitle"));
        return true;
    }


    private View.OnClickListener clickToAlipay() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.create(DonateActivity.this).setTitle("提示")
                        .setMessage("捐赠是自愿行为!\n如果你希望支持作者, 保持更新的动力, 您可以捐赠3-5元.\n本软件是开源软件, 全部功能都无需付费即可使用!\n\"QNotified\"请求打开支付宝\n捐赠完成后请手动打开上方 我已捐赠 开关.").setNegativeButton("取消", null)
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (!hasAlipay()) {
                                    showToastShort(DonateActivity.this, "拉起支付宝失败");
                                } else {
                                    jumpToAlipay();
                                }
                            }
                        }).setCancelable(true).create().show();
            }
        };
    }

    private View.OnClickListener clickToFxxkWxpay() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.createFailsafe(DonateActivity.this).setTitle("为什么不支持微信支付")
                        .setMessage("微信支付收款0.01很容易导致账户被冻结, 故不提供微信支付.")
                        .setPositiveButton("确认", null).setCancelable(true).create().show();
            }
        };
    }

    private View.OnClickListener clickToBtc() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog.createFailsafe(DonateActivity.this).setTitle("BTC")
                        .setMessage("即将开放...")
                        .setPositiveButton("确认", null).setCancelable(true).create().show();
            }
        };
    }

    private void jumpToAlipay() {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(payUrl()));
        startActivity(intent);
    }

    private boolean hasAlipay() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.eg.android.AlipayGphone", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static String payUrl() {
        String urlCode;
        try {
            urlCode = URLEncoder.encode("https://qr.alipay.com/fkx06007ngjbx8qoonajyff", "utf-8");
            final String alipayqr = "alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + urlCode;
            return alipayqr + "%3F_s%3Dweb-other&_t=" + System.currentTimeMillis();
        } catch (Exception e) {
            log(e);
            //should not happen
            return null;
        }
    }
}
