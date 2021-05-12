/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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

package cc.ioctl.activity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.util.Utils.dip2px;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import androidx.annotation.Nullable;
import com.tencent.mobileqq.widget.BounceScrollView;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import kotlin.text.Charsets;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Natives;
import nil.nadph.qnotified.util.Utils;

@SuppressLint("Registered")
public class SecurityTestActivity extends IphoneTitleBarActivityCompat {

    @Override
    public boolean doOnCreate(@Nullable Bundle bundle) {
        super.doOnCreate(bundle);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = null;
        bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);

        ll.addView(newListItemButton(this, "Hook libc.so!open", null, null,
            v -> {
                try {
                    long libc = Natives.dlopen("libnatives.so", Natives.RTLD_NOLOAD);
                    if (libc == 0) {
                        throw new RuntimeException("dlopen libnatives.so failed");
                    }
                    long abort = Natives.dlsym(libc, "hook_libc_open");
                    if (abort == 0) {
                        String msg = Natives.dlerror();
                        if (msg != null) {
                            throw new RuntimeException(msg);
                        } else {
                            throw new RuntimeException("dlsym failed");
                        }
                    }
                    long ret = Natives.call(abort);
                    CustomDialog.createFailsafe(SecurityTestActivity.this)
                        .setTitle("Result").setMessage(String.format("0x%x", ret)).ok().show();
                } catch (Throwable e) {
                    CustomDialog.createFailsafe(SecurityTestActivity.this)
                        .setTitle(Utils.getShort$Name(e)).setMessage(e.getMessage()).ok().show();
                }
            }));
        ll.addView(newListItemButton(this, "测试 /proc/self/maps", null, null,
            v -> {
                try {
                    String result = checkProcSelfMaps();
                    CustomDialog.createFailsafe(SecurityTestActivity.this)
                        .setTitle(result.trim().length() == 0 ? "测试通过" : "测试未通过")
                        .setMessage(withSmallMonospaceSpan(result)).ok().show();
                } catch (Exception e) {
                    CustomDialog.createFailsafe(SecurityTestActivity.this)
                        .setTitle(Utils.getShort$Name(e)).setMessage(e.getMessage()).ok().show();
                }
            }));

        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        this.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle(Utils.getShort$Name(getClass()));
        return true;
    }

    private static String checkProcSelfMaps() throws IOException {
        StringBuilder ret = new StringBuilder();
        FileInputStream ins = new FileInputStream("/proc/self/maps");
        InputStreamReader reader = new InputStreamReader(ins, Charsets.UTF_8);
        BufferedReader bufReader = new BufferedReader(reader);
        String line;
        while ((line = bufReader.readLine()) != null) {
            if (line.contains("nil.nadph.qnotified") || line.contains("libnatives.so")
                || line.contains("/qn_mmkv/")) {
                ret.append(line).append('\n');
            }
        }
        bufReader.close();
        reader.close();
        ins.close();
        return ret.toString();
    }

    CharSequence withSmallMonospaceSpan(String text) {
        SpannableString result = new SpannableString(text);
        result.setSpan(new TextAppearanceSpan("monospace", 0, Utils.dip2px(this, 8), null, null),
            0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return result;
    }
}
