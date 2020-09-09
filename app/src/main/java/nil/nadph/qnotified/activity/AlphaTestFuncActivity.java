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
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.mobileqq.widget.BounceScrollView;

import me.kyuubiran.dialog.RevokeMsgDialog;
import me.singleneuron.util.KotlinUtilsKt;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.dip2px;

@Deprecated
@SuppressLint("Registered")
public class AlphaTestFuncActivity extends IphoneTitleBarActivityCompat {



    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
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
        if (!LicenseStatus.isAsserted()) {
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
                        AlphaTestFuncActivity.this.finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            View v = subtitle(this, "Alpha内测功能 请勿截图此页面");
            v.setOnClickListener(v1 -> RevokeMsgDialog.INSTANCE.onShow(AlphaTestFuncActivity.this));
            ll.addView(v);

        }
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;

        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("Alpha内测功能");
        KotlinUtilsKt.showEulaDialog(this);
        return true;
    }

    @Override
    public void doOnResume() {
        super.doOnResume();

    }
}
