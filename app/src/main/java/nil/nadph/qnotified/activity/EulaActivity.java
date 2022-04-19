/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.ioctl.hook.FakeBatteryHook;
import com.tencent.mobileqq.widget.BounceScrollView;
import java.io.IOException;
import me.singleneuron.qn_kernel.tlb.ActivityRouter;
import nil.nadph.qnotified.InjectDelayableHooks;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@SuppressLint("Registered")
public class EulaActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    public static final int CURRENT_EULA_VERSION = 9;
    private static final int R_ID_I_HAVE_READ = 0x300AFF91;
    private static final int R_ID_I_AGREE = 0x300AFF92;
    private static final int R_ID_I_DENY = 0x300AFF93;

    public static void appendEx2(SpannableStringBuilder sb, String text) {
        int start = sb.length();
        sb.append(text);
        sb.setSpan(new StyleSpan(Typeface.BOLD), start, sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(1.3f), start, sb.length(),
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.setId(R.id.rootBounceScrollView);
        ll.setId(R.id.rootMainLayout);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        this.setContentView(bounceScrollView);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle("EULA");
        LinearLayout.LayoutParams stdlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);

        if (LicenseStatus.hasEulaUpdated()) {
            TextView tv_updated = new TextView(this);
            tv_updated.setTextSize(22);
            tv_updated.setGravity(Gravity.CENTER);
            tv_updated.getPaint().setFakeBoldText(true);
            tv_updated.setTextColor(ResUtils.skin_red);
            tv_updated.setText("用户协议发生变更, 您需要同意接受下方《协议》及《隐私条款》才能继续使用本模块");
            ll.addView(tv_updated, stdlp);
        }
        TextView tv = new TextView(this);
        tv.setTextSize(28);
        tv.getPaint().setFakeBoldText(true);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ResUtils.skin_black);
        tv.setText("QNotified 最终用户许可协议\n与《隐私条款》");
        ll.addView(tv, stdlp);

        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            sb.append(Utils.getFileContent(ResUtils.openAsset("eula.txt")));
            sb.append("\n\n");
            sb.append(Utils.getFileContent(ResUtils.openAsset("privacy_license.txt")));
        } catch (IOException e) {
            sb.append(Log.getStackTraceString(e));
        }

        tv = new TextView(this);
        tv.setTextSize(16);
        tv.setTextColor(ResUtils.skin_black);
        tv.setText(sb);
        ll.addView(tv, stdlp);

        tv = new TextView(this);
        tv.setTextSize(23);
        tv.setGravity(Gravity.CENTER);
        tv.setTextColor(ResUtils.skin_black);
        tv.setText(
            "\n注意: 本软件是免费软件!\nQNotified自始至终都是免费且非商业使用，如果有你发现有人在违反AGPL和Eula，请拒绝并不遗余力地在一切平台举报投诉他！\n");
        ll.addView(tv, stdlp);

        int _5dp = Utils.dip2px(this, 5);

        if (!LicenseStatus.hasUserAcceptEula()) {
            CheckBox iHaveRead = new CheckBox(this);
            iHaveRead.setId(R_ID_I_HAVE_READ);
            iHaveRead.setText("我已阅读<<协议>>和<<隐私条款>>并自愿承担由使用本软件导致的一切后果");
            iHaveRead.setTextSize(17);
            iHaveRead.setTextColor(ResUtils.skin_black);
            iHaveRead.setButtonDrawable(ResUtils.getCheckBoxBackground());
            iHaveRead.setPadding(_5dp, _5dp, _5dp, _5dp);
            iHaveRead.setChecked(FakeBatteryHook.INSTANCE.isFakeBatteryCharging());
            ll.addView(iHaveRead,
                newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 3 * _5dp, _5dp, 2 * _5dp, _5dp));

            Button agree = new Button(this);
            agree.setId(R_ID_I_AGREE);
            agree.setOnClickListener(this);
            ResUtils.applyStyleCommonBtnBlue(agree);
            agree.setText("我同意并继续");
            ll.addView(agree,
                newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));

            Button deny = new Button(this);
            deny.setId(R_ID_I_DENY);
            deny.setOnClickListener(this);
            ResUtils.applyStyleCommonBtnBlue(deny);
            deny.setText("我拒绝");
            ll.addView(deny,
                newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
        } else {
            tv = new TextView(this);
            tv.setTextSize(17);
            tv.getPaint().setFakeBoldText(true);
            tv.setGravity(Gravity.CENTER);
            tv.setTextColor(ResUtils.skin_gray3);
            tv.setText("你已阅读并同意<<协议>>和<<隐私条款>>");
            ll.addView(tv, stdlp);
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        CheckBox iHaveRead = findViewById(R_ID_I_HAVE_READ);
        boolean read = iHaveRead.isChecked();
        switch (v.getId()) {
            case R_ID_I_AGREE:
                if (!read) {
                    Toasts.error(this, "请先勾选\"我已阅读<<协议>>\"");
                    return;
                } else {
                    LicenseStatus.setEulaStatus(CURRENT_EULA_VERSION);
                    InjectDelayableHooks.doInitDelayableHooksMP();
                    this.startActivity(
                        new Intent(this, ActivityRouter.INSTANCE.getActivityClass()));
                    finish();
                }
                break;
            case R_ID_I_DENY:
                try {
                    Uri uri = Uri.parse("package:nil.nadph.qnotified");
                    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Toasts.error(this, e + "", Toast.LENGTH_LONG);
                }
                Toasts.error(this, "请立即卸载QNotified", Toast.LENGTH_LONG);
                break;
        }
    }
}
