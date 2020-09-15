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
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.mobileqq.widget.BounceScrollView;

import nil.nadph.qnotified.ui.ResUtils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

@SuppressLint("Registered")
public class AboutActivity extends IphoneTitleBarActivityCompat {

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

        ll.addView(subtitle(this, "QNotified"));
        ll.addView(subtitle(this, "本模块无毒无害, 免费开源, 旨在\n 1.接手部分停更模块的部分功能\n 2.提供被删好友通知\n 3.移除部分臃肿功能, 增加部分实用功能"));

        ll.addView(subtitle(this, "注意: 6.5.5以下版本的QQ已不再受支持"));

        ll.addView(subtitle(this, "此模块目前承认的APP发布渠道为 Github 上本项目的 Releases 和 Xposed Installer 里的模块下载 ,也可从https://github.com/ferredoxin/QNotified 获取源码自行编译, 如果您是在其他渠道下载的话请自己注意安全.\n Copyright (C) 2019-2020 cinit@github"));

        ll.addView(subtitle(this, "支持的(类)Xposed内核:"));
        ll.addView(subtitle(this, "原生Xposed, Epic(太极), SandHook, YAHFA ,BugHook(应用转生), etc"));

        ll.addView(subtitle(this, "声明:"));
        ll.addView(subtitle(this, "此软件是捐赠软件 个人可以免费使用 请勿以任何方式商用本软件 如果喜欢我的作品请打赏支持我维护和开发! 任何形式或渠道包括预装手机售卖此软件​都是非法贩卖, 别上当受骗！欢迎举报贩卖者! ", Color.RED));

        ll.addView(subtitle(this, "特别声明:"));
        ll.addView(subtitle(this, "QNotified模块属于个人作品! 没有售后! 没有客服! 您可以与我反馈和讨论问题, 但请文明交流尊重彼此!"));

        ll.addView(subtitle(this, "免责声明: 一切后果自负(包括但不限于群发导致的冻结封号)", Color.RED));
        ll.addView(subtitle(this, "用户协议: The GNU General Public License v3.0"));
        ll.addView(subtitle(this, "This program is distributed in the hope that it will be useful, " +
                "but WITHOUT ANY WARRANTY; without even the implied warranty of " +
                "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE."));

        ll.addView(subtitle(this, "请尊重我的劳动成果 请勿用于商业用途 严禁盗版贩卖", Color.RED));
        ll.addView(subtitle(this, "遇到 免费软件(包括但不限于本软件) 倒卖者请直接举报, 谢谢您的配合!"));
        //bounceScrollView.setFocusable(true);
        //bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("关于");
        return true;
    }

}
