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
import android.widget.*;

import com.tencent.mobileqq.widget.BounceScrollView;

import java.io.IOException;

import nil.nadph.qnotified.InjectDelayableHooks;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.hook.FakeBatteryHook;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;

@SuppressLint("Registered")
public class EulaActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {
    public static final int CURRENT_EULA_VERSION = 5;
    private static final int R_ID_I_HAVE_READ = 0x300AFF91;
    private static final int R_ID_I_AGREE = 0x300AFF92;
    private static final int R_ID_I_DENY = 0x300AFF93;

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
        sb.append("请务必仔细阅读和理解QNotified最终用户许可协议（“本《协议》”）中规定的所有权利和限制。在使用前，您需要仔细阅读并决定接受或不接受本《协议》的条款。除非或直至您接受本《协议》的条款，否则本“软件”不得在您的任何终端上安装或使用。\n" +
                "您一旦使用本“软件”，即表示您同意接受本《协议》各项条款的约束。如您不同意本《协议》中的条款，您则应当立即卸载本“软件”。\n" +
                "本“软件”权利只许可使用，而不出售。\n");
        appendEx2(sb, "一．本“软件”作者将本“软件”的非专有的使用权授予您。 您可以：\n");
        sb.append("  1.在一台个人所有终端上安装、使用、显示、运行（“运行”）本“软件” 的一份副本。本“软件”为个人版软件，仅供个人所有终端使用，不得用于法人或其他组织（包括但不限于政府机关、公司、企事业单位、其他组织等；无论该组织是否为经济性组织；无论该组织的使用是否构成商业目的使用）所有终端。如若个人所有终端长期固定为法人或其他组织服务，则将被视为“法人或其他组织所有终端”，无法享受本授权。任何超出上述授权范围的使用均被视为非法复制的盗版行为，本“软件”作者保留权利要求相关责任人承担相应的法律责任包括但不限于民事责任、行政责任、刑事责任。\n" +
                "  2.为了防止副本损坏而制作备份复制品。这些备份复制品不得通过任何方式提供给他人使用，并在您丧失该合法副本的所有权时，负责将备份复制品销毁。\n" +
                "  3.为了把本“软件”用于实际的终端应用环境或者改进其功能、性能而进行必要的修改；但是，除本《协议》另有约定外，未经本“软件”作者许可，不得向任何第三方提供修改后的软件。\n" +
                "  4.对本“软件”进行反向工程、反向编译或反汇编；或进行其他获得本“软件”原代码的访问或行为(尽管您可以在github获取本软件全部源代码)。\n");
        appendEx2(sb, "二．您保证：\n");
        sb.append("  1.不得出售、贩卖、发行、出租、或以其他方式传播本“软件”以获利。\n" +
                "  2.不得以任何方式商用本“软件”, 包括但不限于使用本“软件”进行群发,代发并获取利润, 使用卡片消息引流, 或出售、二次贩卖、发行、出租本“软件”。\n" +
                "  3.在本“软件”的所有副本上包含所有的版权标识。\n");
        appendEx2(sb, "三．权利的保留：\n");
        sb.append("  未明示授予的一切权利均为本“软件”作者所有。\n");
        appendEx2(sb, "四．本“软件”的著作权：\n");
        sb.append("  1.您不得去掉本“软件”上的任何版权标识，并应在其所有复制品上依照其现有表述方式标注其版权属于本“软件”作者。\n" +
                "  2.本“软件”（包括但不限于本“软件”中所含的任何图像、照片、动画、录像、录音、音乐、文字和附加程序）、随附的印刷材料及本“软件”任何副本的著作权，均由本“软件”作者拥有。\n" +
                "  3.您不可以从本“软件”中去掉其版权声明；并保证为本“软件”的复制品（全部或部分）复制版权声明。\n");
        appendEx2(sb, "五．免责声明：\n");
        sb.append("  1.用户在下载并使用本“软件”时均被视为已经仔细阅读本条款并完全同意。凡以任何方式激活本软件，或直接、间接使用本“软件”，均被视为自愿接受相关声明和用户服务协议的约束。\n" +
                "  2.本“软件”仅供用户作测试交流或娱乐使用，不得用于非法用途，一切自行使用于其它用途的用户，本“软件”概不承担任何法律责任。\n" +
                "  3.用户使用本“软件”过程中，如果侵犯了第三方的知识产权或其他权利，责任由使用者本人承担，本“软件”对此不承担责任。\n" +
                "  4.用户明确并同意其使用本“软件”所存在的风险将完全由其本人承担；因其使用软件而产生的一切后果也由其本人承担，本“软件”对此不承担任何责任。\n" +
                "  5.除本“软件”注明之服务条款外，其它因不当使用本“软件”而导致的任何意外、疏忽、合约毁坏、诽谤、版权或其他知识产权侵犯及其所造成的任何损失，本“软件”概不负责，亦不承担任何法律责任。\n" +
                "  6.对于因不可抗力或因黑客攻击、通讯线路中断等本“软件”不能控制的原因造成的网络服务中断或其他缺陷，导致用户不能正常使用软件，本“软件”不承担任何责任。\n" +
                "  7.本声明未涉及的问题请参见国家有关法律法规，当本声明与国家有关法律法规冲突时，以国家法律法规为准。\n" +
                "  8.如您不接受以上声明协议，请停止使用并立即卸载删除本软件。\n");
        appendEx2(sb, "六．终解释权归本“软件”作者所有\n\n");
        try {
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
        tv.setText("\n注意: 本软件是免费软件!\nQNotified自始至终都是免费且非商业使用，如果有你发现有人商用本软件并牟取利润(群发,代发,引流,出售,贩卖等)，请拒绝并不遗余力地在一切平台举报投诉他！\n");
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
            iHaveRead.setChecked(FakeBatteryHook.get().isFakeBatteryCharging());
            ll.addView(iHaveRead, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 3 * _5dp, _5dp, 2 * _5dp, _5dp));

            Button agree = new Button(this);
            agree.setId(R_ID_I_AGREE);
            agree.setOnClickListener(this);
            ResUtils.applyStyleCommonBtnBlue(agree);
            agree.setText("我同意并继续");
            ll.addView(agree, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));

            Button deny = new Button(this);
            deny.setId(R_ID_I_DENY);
            deny.setOnClickListener(this);
            ResUtils.applyStyleCommonBtnBlue(deny);
            deny.setText("我拒绝");
            ll.addView(deny, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, 2 * _5dp, _5dp, 2 * _5dp, _5dp));
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
                    Utils.showToast(this, Utils.TOAST_TYPE_ERROR, "请先勾选\"我已阅读<<协议>>\"", Toast.LENGTH_SHORT);
                    return;
                } else {
                    LicenseStatus.setEulaStatus(CURRENT_EULA_VERSION);
                    InjectDelayableHooks.doInitDelayableHooksMP();
                    MainHook.startProxyActivity(this, SettingsActivity.class);
                    finish();
                }
                break;
            case R_ID_I_DENY:
                try {
                    Uri uri = Uri.parse("package:nil.nadph.qnotified");
                    Intent intent = new Intent(Intent.ACTION_DELETE, uri);
                    startActivity(intent);
                } catch (Exception e) {
                    Utils.showToast(this, Utils.TOAST_TYPE_ERROR, e + "", Toast.LENGTH_LONG);
                }
                Utils.showToast(this, Utils.TOAST_TYPE_ERROR, "请立即卸载QNotified", Toast.LENGTH_LONG);
                break;
        }
    }

    public static void appendEx2(SpannableStringBuilder sb, String text) {
        int start = sb.length();
        sb.append(text);
        sb.setSpan(new StyleSpan(Typeface.BOLD), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.setSpan(new RelativeSizeSpan(1.3f), start, sb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
