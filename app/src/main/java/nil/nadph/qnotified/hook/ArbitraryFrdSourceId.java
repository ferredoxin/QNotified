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
package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.UiThread;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class ArbitraryFrdSourceId extends BaseDelayableHook {
    private static final ArbitraryFrdSourceId self = new ArbitraryFrdSourceId();
    private boolean inited = false;

    private ArbitraryFrdSourceId() {
    }

    public static ArbitraryFrdSourceId get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method AddFriendVerifyActivity_doOnCreate = null;
            for (Method m : Initiator.load("com.tencent.mobileqq.activity.AddFriendVerifyActivity").getDeclaredMethods()) {
                if (m.getName().equals("doOnCreate")) {
                    AddFriendVerifyActivity_doOnCreate = m;
                    break;
                }
            }
            XposedBridge.hookMethod(AddFriendVerifyActivity_doOnCreate, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity ctx = (Activity) param.thisObject;
                    initFunView(ctx);
                }
            });
            inited = true;
        } catch (Throwable e) {
            Utils.log(e);
        }
        return false;
    }

    @UiThread
    static ViewGroup[] findRlRootAndParent(Activity activity) {
        ResUtils.initTheme(activity);
        ViewGroup content = activity.findViewById(android.R.id.content);
        ViewGroup inner1 = (ViewGroup) content.getChildAt(0);
        for (int i = 0; i < inner1.getChildCount(); i++) {
            View v = inner1.getChildAt(i);
            if (v.getClass().getName().contains("BounceScrollView")) {
                ViewGroup bsv = (ViewGroup) v;
                return new ViewGroup[]{(ViewGroup) bsv.getChildAt(0), bsv};
            }
        }
        return null;
    }

    @UiThread
    static void initFunView(Activity ctx) {
        Intent intent = ctx.getIntent();
        ViewGroup[] tmp = findRlRootAndParent(ctx);
        RelativeLayout rl_root = (RelativeLayout) tmp[0];
        ViewGroup bsv = tmp[1];
        Bundle argv = intent.getExtras();
        assert argv != null : "Intent extra for AddFriendVerifyActivity should not be null";
        int __10_ = Utils.dip2px(ctx, 10);
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setOrientation(LinearLayout.VERTICAL);

        LinearLayout sourceAttrLayout = new LinearLayout(ctx);
        sourceAttrLayout.setOrientation(LinearLayout.VERTICAL);
        sourceAttrLayout.addView(ViewBuilder.subtitle(ctx, "来源参数"));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "SourceId", null, String.valueOf(argv.getInt("source_id", 3999))));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "SubSourceId", null, String.valueOf(argv.getInt("sub_source_id", 0))));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "Extra", null, String.valueOf(argv.getString("extra"))));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "Msg", null, String.valueOf(argv.getString("msg"))));
        sourceAttrLayout.setPadding(0, __10_, 0, __10_);

        ViewGroup.LayoutParams rl_root_lp = rl_root.getLayoutParams();
        bsv.removeAllViews();
        wrapper.addView(rl_root, MATCH_PARENT, WRAP_CONTENT);
        wrapper.addView(sourceAttrLayout, MATCH_PARENT, WRAP_CONTENT);

        bsv.addView(wrapper, rl_root_lp);
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
