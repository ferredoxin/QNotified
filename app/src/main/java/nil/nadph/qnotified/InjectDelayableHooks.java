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
package nil.nadph.qnotified;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.hook.SettingEntryHook;
import nil.nadph.qnotified.ui.ProportionDrawable;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.SimpleBgDrawable;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import java.util.ArrayList;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class InjectDelayableHooks {

    private static boolean inited = false;

    public static boolean step(Object director) {
        if (inited) return true;
        inited = true;
        Activity activity = (Activity) iget_object_or_null(director, "a", load("mqq/app/AppActivity"));
        if (activity == null) activity = (Activity) Utils.getFirstNSFByType(director, load("mqq/app/AppActivity"));
        final Activity ctx = activity;
        boolean needDeobf = false;
        BaseDelayableHook[] hooks = BaseDelayableHook.queryDelayableHooks();
        for (BaseDelayableHook h : hooks) {
            if (h.isEnabled() && !h.checkPreconditions()) {
                needDeobf = true;
                break;
            }
        }
        final LinearLayout[] overlay = new LinearLayout[1];
        final LinearLayout[] main = new LinearLayout[1];
        final ProportionDrawable[] prog = new ProportionDrawable[1];
        final TextView[] text = new TextView[1];
        if (needDeobf) {
            try {
                if (ctx != null) ResUtils.initTheme(ctx);
            } catch (Throwable e) {
                log(e);
            }
            final ArrayList<Integer> todos = new ArrayList<>();
            for (BaseDelayableHook h : hooks) {
                if (!h.isEnabled()) continue;
                for (int i : h.getPreconditions()) {
                    if (!DexKit.checkFor(i)) todos.add(i);
                }
            }
            for (int idx = 0; idx < todos.size(); idx++) {
                final String name = DexKit.c(todos.get(idx)).replace("/", ".");
                final int j = idx;
                if (SyncUtils.isMainProcess() && ctx != null)
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (overlay[0] == null) {
                                overlay[0] = new LinearLayout(ctx);
                                overlay[0].setOrientation(LinearLayout.VERTICAL);
                                overlay[0].setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
                                overlay[0].setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                                main[0] = new LinearLayout(ctx);
                                overlay[0].addView(main[0]);
                                main[0].setOrientation(LinearLayout.VERTICAL);
                                main[0].setGravity(Gravity.CENTER);
                                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                                llp.bottomMargin = dip2px(ctx, 55);
                                main[0].setLayoutParams(llp);
                                LinearLayout lprop = new LinearLayout(ctx);
                                lprop.setBackgroundDrawable(new SimpleBgDrawable(0, 0xA0808080, 2));
                                final View _v = new View(ctx);
                                prog[0] = new ProportionDrawable(0xA0202020, 0x40FFFFFF, Gravity.LEFT, 0);
                                _v.setBackgroundDrawable(prog[0]);
                                int __3_ = dip2px(ctx, 3);
                                LinearLayout.LayoutParams _tmp_lllp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(ctx, 4));
                                _tmp_lllp.setMargins(__3_, __3_, __3_, __3_);
                                lprop.addView(_v, _tmp_lllp);
                                LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                                int __5_ = dip2px(ctx, 5);
                                plp.setMargins(__5_ * 2, 0, __5_ * 2, __5_);
                                main[0].addView(lprop, plp);
                                text[0] = new TextView(ctx);
                                text[0].setTextSize(16);
                                text[0].setGravity(Gravity.CENTER_HORIZONTAL);
                                text[0].setTextColor(0xFF000000);
                                text[0].setShadowLayer(__5_ * 2, -0, -0, 0xFFFFFFFF);
                                LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                                main[0].addView(text[0], tlp);
                                ((ViewGroup) ctx.getWindow().getDecorView()).addView(overlay[0]);
                            }
                            text[0].setText("QNotified正在定位被混淆类:\n" + name + "\n每个类一般不会超过一分钟");
                            prog[0].setProportion(1.0f * j / todos.size());
                        }
                    });
                DexKit.prepareFor(todos.get(idx));
            }
        }
        if (LicenseStatus.hasUserAgreeEula()) {
            for (BaseDelayableHook h : hooks) {
                try {
                    if (h.isEnabled() && h.isTargetProc() && h.checkPreconditions()) h.init();
                } catch (Throwable e) {
                    log(e);
                }
            }
        } else {
            SettingEntryHook.get().init();
        }
        if (ctx != null && main[0] != null) ctx.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) ctx.getWindow().getDecorView()).removeView(overlay[0]);
            }
        });
        return true;
    }

    public static void doInitDelayableHooksMP() {
        for (BaseDelayableHook h : BaseDelayableHook.queryDelayableHooks()) {
            try {
                if (h.isEnabled() && h.isTargetProc() && h.checkPreconditions()) {
                    SyncUtils.requestInitHook(h.getId(), h.getEffectiveProc());
                    h.init();
                }
            } catch (Throwable e) {
                log(e);
            }
        }
    }

}
