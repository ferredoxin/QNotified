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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.tencent.mobileqq.widget.BounceScrollView;
import nil.nadph.qnotified.chiral.Molecule;
import nil.nadph.qnotified.chiral.MoleculeView;
import nil.nadph.qnotified.chiral.PubChemStealer;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class Auth2Activity extends IphoneTitleBarActivityCompat implements View.OnClickListener, DialogInterface.OnClickListener, Runnable {

    private MoleculeView moleculeView;
    private TextView tvSelectedCount, newOne;
    private Button nextStep;
    private Molecule currMol;
    private AlertDialog makingMol = null;
    private int refreshId = 0;

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
        newOne = new TextView(this);
        newOne.setTextColor(ResUtils.skin_black);
        newOne.setTextSize(16);
        newOne.setGravity(Gravity.CENTER);
        newOne.setPadding(__10 * 2, 0, __10 * 2, __10 / 2);
        newOne.setText("看不清,换一个");
        newOne.setTextColor(ResUtils.skin_blue);
        newOne.setOnClickListener(this);
        hl.addView(newOne, ViewBuilder.newRelativeLayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                RelativeLayout.ALIGN_PARENT_RIGHT, -1));
        ll.addView(hl, ViewBuilder.newLinearLayoutParams(WRAP_CONTENT, WRAP_CONTENT,
                0, 0, 0, __10 * 2));

        nextStep = new Button(this);
        nextStep.setOnClickListener(this);
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
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SECURE);
        onClick(newOne);
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == moleculeView) {
            int count = moleculeView.getSelectedChiralCount();
            if (count == 0) {
                tvSelectedCount.setText("未选择");
            } else {
                tvSelectedCount.setText("已选择: " + count);
            }
        } else if (v == newOne) {
            if (makingMol == null) {
                refreshId++;
                makingMol = (AlertDialog) CustomDialog.createFailsafe(this).setCancelable(false).setTitle("请稍候").setMessage("正在加载")
                        .setNegativeButton("取消", this).show();
                new Thread(this).start();
            }
        } else if (v == nextStep) {
            if (moleculeView.getSelectedChiralCount() == 0) {
                showToast(Auth2Activity.this, TOAST_TYPE_INFO, "请选择手性碳原子", 0);
            } else {
                showToast(Auth2Activity.this, TOAST_TYPE_ERROR, "选择错误, 请重试", 0);
            }
        }
    }

    @Override
    public void run() {
        int curr = refreshId;
        final Molecule molecule = PubChemStealer.nextRandomMolecule();
        if (makingMol != null) {
            makingMol.dismiss();
            makingMol = null;
        } else {
            return;
        }
        if (curr != refreshId) return;
        if (molecule != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    moleculeView.setMolecule(molecule);
                    onClick(moleculeView);
                }
            });
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Utils.showToastShort(Auth2Activity.this, "加载失败");
                }
            });
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        makingMol = null;
        refreshId++;
    }
}
