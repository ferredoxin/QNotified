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
package cc.ioctl.dialog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import com.rymmmmm.hook.OneTapTwentyLikes;
import com.rymmmmm.hook.RemoveSendGiftAd;
import com.rymmmmm.hook.ShowMsgCount;
import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.ui.DummyDrawable;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

public class RikkaDialog extends Dialog implements View.OnClickListener {

    private final Context mContext;
    GradientDrawable itemOnDrawable;
    GradientDrawable itemOffDrawable;
    GradientDrawable dialogBgDrawable;
    private ColorStateList textColor;
    private ColorStateList dialogBgColor;

    protected RikkaDialog(Context context) {
        super(context);
        mContext = context;
    }

    public static void showRikkaFuncDialog(@NonNull Context ctx) {
        RikkaDialog dialog = new RikkaDialog(ctx);
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        boolean nightMode = ResUtils.isInNightMode();
        try {
            if (!(mContext instanceof IphoneTitleBarActivityCompat)) {
                ResUtils.initTheme(mContext);
            }
            textColor = ResUtils.skin_black;
            dialogBgColor = ColorStateList.valueOf(ResUtils.skin_tips.getDefaultColor());
        } catch (Throwable e) {
            Utils.log(e);
        }
        if (textColor == null || dialogBgColor == null) {
            if (nightMode) {
                textColor = ColorStateList.valueOf(Color.WHITE);
                dialogBgColor = ColorStateList.valueOf(Color.BLACK);
            } else {
                textColor = ColorStateList.valueOf(Color.BLACK);
                dialogBgColor = ColorStateList.valueOf(Color.WHITE);
            }
        }
        int __5_dp = Utils.dip2px(mContext, 5);
        dialogBgDrawable = new GradientDrawable();
        dialogBgDrawable.setColor(dialogBgColor.getDefaultColor());
        float f = __5_dp * 2;
        dialogBgDrawable.setCornerRadii(new float[]{f, f, f, f, f, f, f, f});
        itemOffDrawable = new GradientDrawable();
        itemOffDrawable.setColor(0);
        itemOffDrawable.setCornerRadius(__5_dp);
        itemOffDrawable.setStroke(Utils.dip2px(mContext, 0.8f), 0xFF808080);
        itemOnDrawable = new GradientDrawable();
        itemOnDrawable.setColor(0);
        itemOnDrawable.setCornerRadius(__5_dp);
        itemOnDrawable.setStroke(Utils.dip2px(mContext, 2),
            RikkaColorPickDialog.getCurrentRikkaBorderColor());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window win = getWindow();
        win.setBackgroundDrawable(new DummyDrawable());
        ScrollView outer = new ScrollView(mContext);
        ViewCompat.setBackground(outer, dialogBgDrawable);
        LinearLayout ll = new LinearLayout(mContext);
        ll.setClickable(true);
        LinearLayout root = new LinearLayout(mContext);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RikkaDialog.this.dismiss();
            }
        });
        int i = __5_dp * 2;
        ll.setPadding(i, i, i, i);
        ll.setOrientation(LinearLayout.VERTICAL);
        RikkaConfigItem[] items = queryRikkaHooks();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        i = __5_dp / 2;
        lp.setMargins(i, i, i, i);
        i = __5_dp * 2;
        for (RikkaConfigItem it : items) {
            TextView tv = new TextView(mContext);
            tv.setText(it.getName());
            tv.setTag(it);
            tv.setTextColor(textColor);
            tv.setTextSize(16);
            tv.setPadding(i, i, i, i);
            tv.setOnClickListener(RikkaDialog.this);
            it.view = tv;
            it.invalidateStatus();
            ll.addView(tv, lp);
        }
        i = __5_dp * 3;
        outer.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams tmplp = new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        tmplp.setMargins(i, i, i, i);
        root.addView(outer, tmplp);
        setContentView(root);
        WindowManager.LayoutParams wlp = win.getAttributes();
        wlp.height = WRAP_CONTENT;
        wlp.width = MATCH_PARENT;
        wlp.gravity = Gravity.CENTER;
        wlp.format = PixelFormat.RGBA_8888;
        win.setAttributes(wlp);
    }

    @Override
    public void onClick(View v) {
        Object o = v.getTag();
        if (o instanceof RikkaConfigItem) {
            ((RikkaConfigItem) o).view = v;
            ((RikkaConfigItem) o).onClick(v);
        }
    }

    private RikkaConfigItem[] queryRikkaHooks() {
        return new RikkaConfigItem[]{
            RikkaConfigItem.create(this, "显示具体消息数量", ShowMsgCount.INSTANCE),
            new RikkaBaseApkFormatDialog(this),
            RikkaConfigItem.create(this, "回赞界面一键20赞", OneTapTwentyLikes.INSTANCE),
            new RikkaCustomMsgTimeFormatDialog(this),
            RikkaConfigItem.create(this, "免广告送免费礼物[仅限群聊送礼物]", RemoveSendGiftAd.INSTANCE),
            new RikkaCustomDeviceModelDialog(this),
            new RikkaCustomSplash(this),
            new RikkaColorPickDialog(this),
        };
    }

    public abstract static class RikkaConfigItem implements View.OnClickListener {

        final protected RikkaDialog rikkaDialog;
        public View view;

        public RikkaConfigItem(RikkaDialog d) {
            rikkaDialog = d;
        }

        public static RikkaConfigItem create(RikkaDialog dialog, final String name,
            final BaseDelayableHook hook) {
            return new RikkaConfigItem(dialog) {

                @Override
                public void onClick(final View v) {
                    hook.setEnabled(!hook.isEnabled());
                    if (hook.isEnabled() && !hook.isInited()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ViewBuilder.doSetupAndInit(v.getContext(), hook);
                                v.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invalidateStatus();
                                    }
                                });
                            }
                        }).start();
                    } else {
                        invalidateStatus();
                    }
                }

                @Override
                public boolean isEnabled() {
                    return hook.isEnabled();
                }

                @Override
                public String getName() {
                    return name;
                }
            };
        }

        public static RikkaConfigItem createDummy(RikkaDialog dialog, final String name) {
            return new RikkaConfigItem(dialog) {
                boolean on;

                @Override
                public void onClick(View v) {
                    on = !on;
                    invalidateStatus();
                }

                @Override
                public boolean isEnabled() {
                    return on;
                }

                @Override
                public String getName() {
                    return name;
                }
            };
        }

        public static RikkaConfigItem createStub(RikkaDialog dialog, final String name) {
            return new RikkaConfigItem(dialog) {

                @Override
                public void onClick(View v) {
                    Toasts.info(v.getContext(), "对不起,此功能尚在开发中");
                }

                @Override
                public boolean isEnabled() {
                    return false;
                }

                @Override
                public String getName() {
                    return name;
                }
            };
        }

        public abstract boolean isEnabled();

        public abstract String getName();

        public void invalidateStatus() {
            View v = view;
            if (v == null) {
                return;
            }
            ViewCompat.setBackground(v,
                isEnabled() ? rikkaDialog.itemOnDrawable : rikkaDialog.itemOffDrawable);
        }
    }


}
