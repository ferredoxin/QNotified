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
package cc.ioctl.hook;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.UiThread;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class ArbitraryFrdSourceId extends CommonDelayableHook {

    public static final ArbitraryFrdSourceId INSTANCE = new ArbitraryFrdSourceId();

    private ArbitraryFrdSourceId() {
        super("__NOT_USED__");
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
        Bundle argv = intent.getExtras();
        assert argv != null : "Intent extra for AddFriendVerifyActivity should not be null";
        int uinType = argv.getInt("k_uin_type", 0);
        if (uinType == 4) {
            //Pointless for group entry
            return;
        }
        ViewGroup[] tmp = findRlRootAndParent(ctx);
        RelativeLayout rl_root = (RelativeLayout) tmp[0];
        ViewGroup bsv = tmp[1];
        int __10_ = Utils.dip2px(ctx, 10);
        LinearLayout wrapper = new LinearLayout(ctx);
        wrapper.setOrientation(LinearLayout.VERTICAL);

        LinearLayout sourceAttrLayout = new LinearLayout(ctx);
        sourceAttrLayout.setOrientation(LinearLayout.VERTICAL);
        sourceAttrLayout.addView(ViewBuilder.subtitle(ctx, "来源参数"));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "SourceId", null,
            String.valueOf(argv.getInt("source_id", 3999))));
        sourceAttrLayout.addView(ViewBuilder.newListItemDummy(ctx, "SubSourceId", null,
            String.valueOf(argv.getInt("sub_source_id", 0))));
        sourceAttrLayout.addView(ViewBuilder
            .newListItemDummy(ctx, "Extra", null, String.valueOf(argv.getString("extra"))));
        sourceAttrLayout.addView(
            ViewBuilder.newListItemDummy(ctx, "Msg", null, String.valueOf(argv.getString("msg"))));
        sourceAttrLayout.setPadding(0, __10_, 0, __10_);

        ViewGroup.LayoutParams rl_root_lp = rl_root.getLayoutParams();
        bsv.removeAllViews();
        wrapper.addView(rl_root, MATCH_PARENT, WRAP_CONTENT);
        wrapper.addView(sourceAttrLayout, MATCH_PARENT, WRAP_CONTENT);

        bsv.addView(wrapper, rl_root_lp);
    }

    @Override
    public boolean initOnce() {
        try {
            Method AddFriendVerifyActivity_doOnCreate = null;
            for (Method m : Initiator.load("com.tencent.mobileqq.activity.AddFriendVerifyActivity")
                .getDeclaredMethods()) {
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
        } catch (Throwable e) {
            Utils.log(e);
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }
}
