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

public class AboutActivity implements ActivityAdapter {
    private Activity self;

    public AboutActivity(Activity activity) {
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
		
		ll.addView(subtitle(self, "QNotified"));
		ll.addView(subtitle(self, "本模块无毒无害, 免费开源, 旨在\n 1.接手部分停更模块的部分功能\n 2.提供被删好友通知\n 3.移除部分臃肿功能, 增加部分实用功能"));
		
		ll.addView(subtitle(self, "此模块目前承认的APP发布渠道为 Github 上本项目的 Releases 和 Xposed Installer 里的模块下载 ,也可从https://github.com/cinit/QNotified 获取源码自行编译, 如果您是在其他渠道下载的话请自己注意安全.\n Copyright (C) 2019 cinit@github"));
		
		ll.addView(subtitle(self, "支持的(类)Xposed内核:"));
        ll.addView(subtitle(self, "原生Xposed, Epic(太极), SandHook, BugHook(应用转生)"));
		ll.addView(subtitle(self, " * YAHFA 可能存在一定兼容性问题(如卡Q,黑屏等等),遇到问题请反馈(说明发生的情况和其它必要信息,如QQ版本,模块版本,使用的框架环境,安卓版本等),谢谢"));
		
		ll.addView(subtitle(self, "声明:"));
		ll.addView(subtitle(self, "此软件是捐赠软件 个人可以免费使用 请勿以任何方式商用本软件 如果喜欢我的作品请打赏支持我维护和开发! 任何形式或渠道包括预装手机售卖此软件​都是非法贩卖, 别上当受骗！欢迎举报贩卖者! "));
		
		ll.addView(subtitle(self, "特别声明:"));
		ll.addView(subtitle(self, "QNotified模块属于个人作品! 没有售后! 没有客服! 您可以与我反馈和讨论问题, 但请文明交流尊重彼此!"));
		
        ll.addView(subtitle(self, "免责声明"));
        ll.addView(subtitle(self, "This program is distributed in the hope that it will be useful, " +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE."));
		
		ll.addView(subtitle(self, "请尊重我的的劳动成果 请勿用于商业用途 严禁盗版贩卖"));
        ll.addView(subtitle(self, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader() + "\nContext.getClassLoader()\n" + self.getClassLoader() + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()));
        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        //sdlv.setBackgroundColor(0xFFAA0000)
        invoke_virtual(self, "setTitle", "关于", CharSequence.class);
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
