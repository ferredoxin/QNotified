package nil.nadph.qnotified.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import nil.nadph.qnotified.util.QThemeKit;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.QQViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

public class DonateActivity implements ActivityAdapter {
    private Activity self;

    public DonateActivity(Activity activity) {
        self = activity;
    }

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
        bounceScrollView.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
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
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;

        ll.addView(subtitle(self, ""));
        ll.addView(subtitle(self, "首先我想说一句,谢谢,心意收到"));
        ll.addView(subtitle(self, ""));
        ll.addView(subtitle(self, "但是目前还存在一个较为严重的问题:"));
        ll.addView(subtitle(self, "  虽然QNotified是一个完全免费的插件,但仍因有不少人对本软件进行二次贩卖(如:以群发器的名义贩卖),不少人上当受骗"));
        ll.addView(subtitle(self, "从用户反馈来看,这种贩卖情况并非个例"));
        ll.addView(subtitle(self, "这违背了我的本意: **我希望任何个人都能免费的使用本软件**"));
        ll.addView(subtitle(self, "从根本上说,如果任何需要本软件的人都能免费地获取本软件,那么倒卖这种的情况就不会发生"));
        ll.addView(subtitle(self, "所以"));
        ll.addView(subtitle(self, "每多一个人免费地分发本软件,可能因贩卖上当的人就少一个"));
        ll.addView(subtitle(self, "譬如说,可以在各大玩机论坛社区以资源分享的方式分发免费软件(包括但不限于本模块,尽量别设置回复可见)"));
        ll.addView(subtitle(self, "当然以上只是其中一种方法"));
        ll.addView(subtitle(self, "本软件首发地为 https://github.com/cinit/QNotified (求star/issue/pull request)"));
        ll.addView(subtitle(self, "最后,谢谢你的支持"));
        ll.addView(subtitle(self, "by 千古华亭鸽自飞 (咕咕咕)"));

        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        //sdlv.setBackgroundColor(0xFFAA0000)
        invoke_virtual(self, "setTitle", "打赏", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        //TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
        //log("Title:"+invoke_virtual(self,"getTextTitle"));
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
