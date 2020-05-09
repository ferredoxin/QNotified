/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.chiral.MdlMolParser;
import nil.nadph.qnotified.chiral.Molecule;
import nil.nadph.qnotified.chiral.MoleculeView;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class Auth2Activity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    private MoleculeView moleculeView;
    private TextView tvSelectedCount;
    private Button nextStep;
    private Molecule currMol;

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(this, 48));
        LinearLayout.LayoutParams lp_mw = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

        TextView tv = new TextView(this);
        tv.setTextSize(20);
        tv.setTextColor(ResUtils.skin_black);
        tv.setText("QNotified 高级验证\n为防止本软件被不合理使用, 您需要完成以下验证以激活本软件部分功能.");
        ll.addView(tv, lp_mw);
        TextView tv2 = new TextView(this);
        tv2.setTextSize(18);
        tv2.setTextColor(ResUtils.skin_black);
        tv2.setText("请点击(或长按)以选出下方有机物中的所有手性碳原子, 然后点击下一步. 如果您觉得下方分子过于复杂, 您可以尝试点击 看不清,换一个 以重新生成有机物.");
        ll.addView(tv2, lp_mw);

        nextMol();

        moleculeView = new MoleculeView(this);
        moleculeView.setTextColor(ResUtils.skin_black.getDefaultColor());
        moleculeView.setMolecule(currMol);
        moleculeView.setOnClickListener(this);
        ll.addView(moleculeView, ViewBuilder.newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT,
                dip2px(this, 5), dip2px(this, 20), dip2px(this, 5), 0));
        tvSelectedCount = new TextView(this);
        tvSelectedCount.setTextColor(ResUtils.skin_black);
        tvSelectedCount.setTextSize(16);
        tvSelectedCount.setGravity(Gravity.CENTER);
        tvSelectedCount.setText("未选择");
        ll.addView(tvSelectedCount, lp_mw);

        int __10 = dip2px(this, 10);
        RelativeLayout hl = new RelativeLayout(this);
        TextView reset = new TextView(this);
        reset.setTextColor(ResUtils.skin_black);
        reset.setTextSize(16);
        reset.setGravity(Gravity.CENTER);
        reset.setPadding(__10 * 2, 0, __10 * 2, __10 / 2);
        reset.setText("重置");
        reset.setTextColor(ResUtils.skin_blue);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moleculeView.unselectedAllChiral();
                Auth2Activity.this.onClick(moleculeView);
            }
        });
        hl.addView(reset, ViewBuilder.newRelativeLayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                RelativeLayout.ALIGN_PARENT_LEFT, -1));
        TextView newOne = new TextView(this);
        newOne.setTextColor(ResUtils.skin_black);
        newOne.setTextSize(16);
        newOne.setGravity(Gravity.CENTER);
        newOne.setPadding(__10 * 2, 0, __10 * 2, __10 / 2);
        newOne.setText("看不清,换一个");
        newOne.setTextColor(ResUtils.skin_blue);
        newOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastShort(Auth2Activity.this, "对不起,就这一个");
            }
        });
        hl.addView(newOne, ViewBuilder.newRelativeLayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                RelativeLayout.ALIGN_PARENT_RIGHT, -1));
        ll.addView(hl, ViewBuilder.newLinearLayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                0, 0, 0, __10 * 2));

        nextStep = new Button(this);
        nextStep.setEnabled(false);
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(Auth2Activity.this, TOAST_TYPE_ERROR, "选择错误,请重试", 0);
            }
        });
        ResUtils.applyStyleCommonBtnBlue(nextStep);
        nextStep.setText("下一步");
        ll.addView(nextStep, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, __10, __10 / 2, __10, __10 / 2));

        this.setContentView(bounceScrollView);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("高级验证");
        TextView rightBtn = (TextView) getRightTextView();
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText("取消");
        rightBtn.setEnabled(true);
        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth2Activity.this.finish();
            }
        });
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == moleculeView) {
            int count = moleculeView.getSelectedChiralCount();
            if (count == 0) {
                tvSelectedCount.setText("未选择");
                nextStep.setEnabled(false);
            } else {
                tvSelectedCount.setText("已选择: " + count);
                nextStep.setEnabled(true);
            }
        }
    }

    private void nextMol() {
        try {
            String molstr = new String(ResUtils.readAll(ResUtils.openAsset("9280425.mol")));
            currMol = MdlMolParser.parseString(molstr);
        } catch (Exception e) {
            Utils.log(e);
        }
    }
}
