package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;

import java.io.IOException;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class DonateActivity extends IphoneTitleBarActivityCompat {

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

        ll.addView(subtitle(this, "如果你希望支持作者, 保持更新的动力, 可请使用以下并不存在的方式捐赠, 完成后打开 [我已捐赠] 即可"));
        ll.addView(subtitle(this, "免费开发不易, 需要花费很多个人精力, 且回报甚微, 甚至有人盗卖, 感谢理解"));
        RelativeLayout playout = newListItemSwitchConfig(this, "我已捐赠", null, qn_donated_choice, false);
        ((CompoundButton) playout.findViewById(R_ID_SWITCH)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (isRecursion()) return;
                if (isChecked) {
                    if (!ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_donated_choice)) {
                        try {
                            CustomDialog dialog = CustomDialog.create(DonateActivity.this);
                            dialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isRecursion()) return;
                                    try {
                                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                                        cfg.putBoolean(qn_donated_choice, true);
                                        cfg.save();
                                        buttonView.setChecked(true);
                                        showToast(DonateActivity.this, TOAST_TYPE_SUCCESS, "感谢您的支持", Toast.LENGTH_SHORT);
                                    } catch (Throwable e) {
                                        log(e);
                                        showToast(DonateActivity.this, TOAST_TYPE_ERROR, "出了点问题", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (isRecursion()) return;
                                    buttonView.setChecked(false);
                                }
                            });
                            dialog.setCancelable(true);
                            dialog.setMessage("Are you sure that you have donated?");
                            dialog.setTitle("THINK TWICE");
                            dialog.show();
                            buttonView.setChecked(ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_donated_choice));
                        } catch (Exception e) {
                            ConfigManager cfg = ConfigManager.getDefaultConfig();
                            cfg.putBoolean(qn_donated_choice, true);
                            try {
                                cfg.save();
                                showToast(DonateActivity.this, TOAST_TYPE_SUCCESS, "感谢您的支持", Toast.LENGTH_SHORT);
                            } catch (IOException ex) {
                                log(ex);
                            }
                        }
                    }
                } else {
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    if (cfg.getBooleanOrFalse(qn_donated_choice)) {
                        try {
                            showToast(DonateActivity.this, TOAST_TYPE_ERROR, "YOU CANNOT UNDO THAT!", Toast.LENGTH_LONG);
                        } catch (Throwable e) {
                            log(e);
                        }
                        buttonView.setChecked(true);
                    }
                }
            }
        });
        ll.addView(playout);

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
        ll.addView(subtitle(this, "本软件首发地为 https://github.com/cinit/QNotified (求star/issue/pull request)"));
        ll.addView(subtitle(this, "最后,谢谢你的支持"));
        ll.addView(subtitle(this, "by 千古华亭鸽自飞 (咕咕咕)"));

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

}
