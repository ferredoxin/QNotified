package nil.nadph.qnotified.dialog;

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

import androidx.core.view.ViewCompat;

import nil.nadph.qnotified.activity.IphoneTitleBarActivityCompat;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.hook.rikka.OneTapTwentyLikes;
import nil.nadph.qnotified.hook.rikka.RemoveSendGiftAd;
import nil.nadph.qnotified.hook.rikka.ShowMsgCount;
import nil.nadph.qnotified.ui.DummyDrawable;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

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
        itemOnDrawable.setStroke(Utils.dip2px(mContext, 2), RikkaColorPickDialog.getCurrentRikkaBorderColor());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window win = getWindow();
        win.setBackgroundDrawable(new DummyDrawable());
        ScrollView outer = new ScrollView(mContext);
        //outer.setBackgroundDrawable(dialogBgDrawable);
        ViewCompat.setBackground(outer,dialogBgDrawable);
        //outer.setVerticalScrollbarTrackDrawable(null);
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
                RikkaConfigItem.create(this, "显示具体消息数量", ShowMsgCount.get()),
                new RikkaBaseApkFormatDialog(this),
//                RikkaConfigItem.create(this, "屏蔽抖动窗口", DisableShakeWindow.get()),
                RikkaConfigItem.create(this, "回赞界面一键20赞", OneTapTwentyLikes.get()),
                new RikkaCustomMsgTimeFormatDialog(this),
                RikkaConfigItem.create(this, "免广告送免费礼物[仅限群聊送礼物]", RemoveSendGiftAd.get()),
                new RikkaCustomDeviceModelDialog(this),
                new RikkaCustomSplash(this),
                new RikkaColorPickDialog(this),
        };
    }

    public abstract static class RikkaConfigItem implements View.OnClickListener {
        final protected RikkaDialog rikkaDialog;

        public RikkaConfigItem(RikkaDialog d) {
            rikkaDialog = d;
        }

        public View view;

        public abstract boolean isEnabled();

        public abstract String getName();

        public void invalidateStatus() {
            View v = view;
            if (v == null) return;
            //v.setBackgroundDrawable(isEnabled() ? rikkaDialog.itemOnDrawable : rikkaDialog.itemOffDrawable);
            ViewCompat.setBackground(v, isEnabled() ? rikkaDialog.itemOnDrawable : rikkaDialog.itemOffDrawable);
        }

        public static RikkaConfigItem create(RikkaDialog dialog, final String name, final BaseDelayableHook hook) {
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
                    Utils.showToastShort(v.getContext(), "对不起,此功能尚在开发中");
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
    }


}
