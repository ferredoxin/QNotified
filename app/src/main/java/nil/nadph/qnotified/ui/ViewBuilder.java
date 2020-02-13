package nil.nadph.qnotified.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.NonUiThread;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.dip2sp;

public class ViewBuilder {

    public static final int R_ID_TITLE = 0x300AFF11;
    public static final int R_ID_DESCRIPTION = 0x300AFF12;
    public static final int R_ID_SWITCH = 0x300AFF13;
    public static final int R_ID_VALUE = 0x300AFF14;
    public static final int R_ID_ARROW = 0x300AFF15;

    private static final int CONSTANT_LIST_ITEM_HEIGHT_DP = 48;

    public static RelativeLayout newListItemSwitch(Context ctx, CharSequence title, CharSequence desc, boolean on, CompoundButton.OnCheckedChangeListener listener) {
        RelativeLayout root = new RelativeLayout(ctx);
        root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, dip2px(ctx, CONSTANT_LIST_ITEM_HEIGHT_DP)));
        root.setBackgroundDrawable(ResUtils.getListItemBackground());
        TextView tv = new TextView(ctx);
        tv.setText(title);
        tv.setId(R_ID_TITLE);
        tv.setTextColor(ResUtils.skin_black);
        tv.setTextSize(dip2sp(ctx, 18));
        CompoundButton sw = switch_new(ctx);
        sw.setChecked(on);
        sw.setId(R_ID_SWITCH);
        sw.setOnCheckedChangeListener(listener);
        RelativeLayout.LayoutParams lp_sw = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int m = dip2px(ctx, 14);
        lp_sw.setMargins(m, m, m, 0);
        lp_sw.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_sw.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (desc == null) {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.CENTER_VERTICAL);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_SWITCH);
            root.addView(tv, lp_t);
        } else {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m / 2, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_SWITCH);
            TextView des = new TextView(ctx);
            des.setText(desc);
            des.setId(R_ID_DESCRIPTION);
            des.setTextColor(ResUtils.skin_gray3);
            des.setTextSize(dip2sp(ctx, 13));
            RelativeLayout.LayoutParams lp_d = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            //m=(int)dip2px(ctx,6);
            lp_d.setMargins(m, 0, 0, 0);
            lp_d.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_d.addRule(RelativeLayout.BELOW, R_ID_TITLE);
            lp_d.addRule(RelativeLayout.LEFT_OF, R_ID_SWITCH);
            root.addView(des, lp_d);
            root.addView(tv, lp_t);
        }
        root.addView(sw, lp_sw);
        return root;
    }

    public static RelativeLayout newListItemSwitchConfig(Context ctx, CharSequence title, CharSequence desc, final String key, boolean defVal) {
        boolean on = ConfigManager.getDefaultConfig().getBooleanOrDefault(key, defVal);
        RelativeLayout root = newListItemSwitch(ctx, title, desc, on, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    ConfigManager mgr = ConfigManager.getDefaultConfig();
                    mgr.getAllConfig().put(key, isChecked);
                    mgr.save();
                } catch (Exception e) {
                    Utils.log(e);
                    Utils.showToastShort(buttonView.getContext(), e.toString());
                }
            }
        });
        return root;
    }


    public static RelativeLayout newListItemSwitchConfigNext(Context ctx, CharSequence title, CharSequence desc, final String key, boolean defVal) throws IOException {
        boolean on = ConfigManager.getDefaultConfig().getBooleanOrDefault(key, defVal);
        RelativeLayout root = newListItemSwitch(ctx, title, desc, on, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    ConfigManager mgr = ConfigManager.getDefaultConfig();
                    mgr.getAllConfig().put(key, isChecked);
                    mgr.save();
                    Utils.showToastShort(buttonView.getContext(), "重启QQ生效");
                } catch (Throwable e) {
                    Utils.log(e);
                    Utils.showToastShort(buttonView.getContext(), e.toString());
                }
            }
        });
        return root;
    }


    public static RelativeLayout newListItemSwitchConfigInit(final Context ctx, CharSequence title, CharSequence desc, final String key, boolean defVal, final BaseDelayableHook hook) throws IOException {
        boolean on = ConfigManager.getDefaultConfig().getBooleanOrDefault(key, defVal);
        RelativeLayout root = newListItemSwitch(ctx, title, desc, on, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
                if (!hook.isInited() && isChecked) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            doSetupAndInit(ctx, hook);
                            try {
                                ConfigManager mgr = ConfigManager.getDefaultConfig();
                                mgr.getAllConfig().put(key, true);
                                mgr.save();
                            } catch (Throwable e) {
                                Utils.log(e);
                                Utils.showToastShort(buttonView.getContext(), e.toString());
                            }
                        }
                    }).start();
                } else {
                    try {
                        ConfigManager mgr = ConfigManager.getDefaultConfig();
                        mgr.getAllConfig().put(key, isChecked);
                        mgr.save();
                    } catch (Throwable e) {
                        try {
                            Utils.showToastShort(buttonView.getContext(), e.toString());
                        } catch (Throwable ignored) {
                        }
                        Utils.log(e);
                    }
                }
            }
        });
        return root;
    }

    private static void doSetupAndInit(final Context ctx, BaseDelayableHook hook) {
        final CustomDialog[] pDialog = new CustomDialog[1];
        for (int i : hook.getPreconditions()) {
            if (DexKit.tryLoadOrNull(i) != null) continue;
            final String name = DexKit.c(i).replace("/", ".");
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pDialog[0] == null) {
                        pDialog[0] = CustomDialog.create(ctx);
                        pDialog[0].setCancelable(false);
                        pDialog[0].setTitle("请稍候");
                        pDialog[0].show();
                    }
                    pDialog[0].setMessage("QNotified正在定位被混淆类:\n" + name + "\n每个类一般不会超过一分钟");

                }
            });
            DexKit.doFindClass(i);
        }
        if (hook.isTargetProc()) hook.init();
        SyncUtils.requestInitHook(hook.getId(), hook.getEffectiveProc());
        if (pDialog[0] != null) {
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog[0].dismiss();
                }
            });
        }
    }

    @NonUiThread
    public static void doSetupForPrecondition(final Context ctx, BaseDelayableHook hook) {
        final CustomDialog[] pDialog = new CustomDialog[1];
        for (int i : hook.getPreconditions()) {
            if (DexKit.tryLoadOrNull(i) != null) continue;
            final String name = DexKit.c(i).replace("/", ".");
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (pDialog[0] == null) {
                        pDialog[0] = CustomDialog.create(ctx);
                        pDialog[0].setCancelable(false);
                        pDialog[0].setTitle("请稍候");
                        pDialog[0].show();
                    }
                    pDialog[0].setMessage("QNotified正在定位被混淆类:\n" + name + "\n每个类一般不会超过一分钟");
                }
            });
            DexKit.doFindClass(i);
        }
        if (pDialog[0] != null) {
            Utils.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    pDialog[0].dismiss();
                }
            });
        }
    }

    public static RelativeLayout newListItemSwitchConfigStub(Context ctx, CharSequence title, CharSequence desc,
                                                             final String key, boolean defVal) {
        RelativeLayout root = newListItemSwitch(ctx, title, desc, false, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setChecked(false);
                Utils.showToastShort(buttonView.getContext(), "对不起,此功能尚在开发中");
            }
        });
        return root;
    }


    public static RelativeLayout newListItemDummy(Context ctx, CharSequence title, CharSequence desc, CharSequence
            value) {
        RelativeLayout root = new RelativeLayout(ctx);
        root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, dip2px(ctx, CONSTANT_LIST_ITEM_HEIGHT_DP)));
        root.setBackgroundDrawable(ResUtils.getListItemBackground());
        TextView tv = new TextView(ctx);
        tv.setText(title);
        tv.setId(R_ID_TITLE);
        tv.setTextColor(ResUtils.skin_black);
        tv.setTextSize(dip2sp(ctx, 18));
        TextView st = new TextView(ctx);
        st.setId(R_ID_VALUE);
        st.setText(value);
        st.setTextColor(ResUtils.skin_gray3);
        st.setTextSize(dip2sp(ctx, 15));
        RelativeLayout.LayoutParams lp_sw = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int m = dip2px(ctx, 14);
        lp_sw.setMargins(m, m, m, m);
        lp_sw.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_sw.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (desc == null) {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.CENTER_VERTICAL);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            root.addView(tv, lp_t);
        } else {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m / 2, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            TextView des = new TextView(ctx);
            des.setText(desc);
            des.setId(R_ID_DESCRIPTION);
            des.setTextColor(ResUtils.skin_gray3);
            des.setTextSize(dip2sp(ctx, 13));
            RelativeLayout.LayoutParams lp_d = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            //m=(int)dip2px(ctx,6);
            lp_d.setMargins(m, 0, 0, 0);
            lp_d.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_d.addRule(RelativeLayout.BELOW, R_ID_TITLE);
            lp_d.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            root.addView(des, lp_d);
            root.addView(tv, lp_t);
        }
        root.addView(st, lp_sw);
        return root;
    }

    public static RelativeLayout newListItemButton(Context ctx, CharSequence title, CharSequence desc, CharSequence
            value, View.OnClickListener listener) {
        RelativeLayout root = new RelativeLayout(ctx);
        root.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, dip2px(ctx, CONSTANT_LIST_ITEM_HEIGHT_DP)));
        root.setBackgroundDrawable(ResUtils.getListItemBackground());
        TextView tv = new TextView(ctx);
        tv.setText(title);
        tv.setId(R_ID_TITLE);
        tv.setTextColor(ResUtils.skin_black);
        tv.setTextSize(dip2sp(ctx, 18));
        ImageView img = new ImageView(ctx);
        img.setImageDrawable(ResUtils.skin_icon_arrow_right_normal);
        img.setId(R_ID_ARROW);
        RelativeLayout.LayoutParams lp_im = new RelativeLayout.LayoutParams(dip2px(ctx, 9), dip2px(ctx, 15));
        int m = dip2px(ctx, 14);
        lp_im.setMargins(0, m, m, 0);
        lp_im.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_im.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        if (desc == null) {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.CENTER_VERTICAL);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            root.addView(tv, lp_t);
        } else {
            RelativeLayout.LayoutParams lp_t = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            m = dip2px(ctx, 14);
            lp_t.setMargins(m, m / 2, 0, 0);
            lp_t.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_t.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            TextView des = new TextView(ctx);
            des.setText(desc);
            des.setId(R_ID_DESCRIPTION);
            des.setTextColor(ResUtils.skin_gray3);
            des.setTextSize(dip2sp(ctx, 13));
            RelativeLayout.LayoutParams lp_d = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            //m=(int)dip2px(ctx,6);
            lp_d.setMargins(m, 0, 0, 0);
            lp_d.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lp_d.addRule(RelativeLayout.BELOW, R_ID_TITLE);
            lp_d.addRule(RelativeLayout.LEFT_OF, R_ID_VALUE);
            root.addView(des, lp_d);
            root.addView(tv, lp_t);
        }
        root.addView(img, lp_im);
        TextView st = new TextView(ctx);
        st.setId(R_ID_VALUE);
        if (value != null) st.setText(value);
        st.setTextColor(ResUtils.skin_gray3);
        st.setTextSize(dip2sp(ctx, 15));
        RelativeLayout.LayoutParams lp_st = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        m = dip2px(ctx, 14);
        lp_st.setMargins(m / 4, m, m / 4, 0);
        lp_st.addRule(RelativeLayout.CENTER_VERTICAL);
        lp_st.addRule(RelativeLayout.LEFT_OF, R_ID_ARROW);
        root.addView(st, lp_st);
        root.setClickable(true);
        root.setOnClickListener(listener);
        return root;
    }


    public static LinearLayout subtitle(Context ctx, CharSequence title) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(ctx);
        tv.setText(title);
        tv.setTextSize(dip2sp(ctx, 13));
        tv.setTextColor(ResUtils.skin_gray3);
        tv.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        int m = dip2px(ctx, 14);
        tv.setPadding(m, m / 5, m / 5, m / 5);
        ll.addView(tv);
		/*View v=new View(ctx);
		int th=dip2px(ctx,3);
		v.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,th*2));
		v.setBackground(new DivDrawable(DivDrawable.TYPE_HORIZONTAL,th));
		ll.addView(v);*/
        return ll;
    }

    public static LinearLayout subtitle(Context ctx, CharSequence title, int color) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(ctx);
        tv.setText(title);
        tv.setTextSize(dip2sp(ctx, 13));
        tv.setTextColor(color);
        tv.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        int m = dip2px(ctx, 14);
        tv.setPadding(m, m / 5, m / 5, m / 5);
        ll.addView(tv);
		/*View v=new View(ctx);
		int th=dip2px(ctx,3);
		v.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT,th*2));
		v.setBackground(new DivDrawable(DivDrawable.TYPE_HORIZONTAL,th));
		ll.addView(v);*/
        return ll;
    }

    public static View.OnClickListener clickToProxyActAction(final int action) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainHook.startProxyActivity(v.getContext(), action);
            }
        };
    }

    public static View.OnClickListener clickToUrl(final String url) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                v.getContext().startActivity(intent);
            }
        };
    }

    public static View.OnClickListener clickToChat() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin=1041703712&version=1&src_type=web&web_src=null");
                Intent intent = new Intent(v.getContext(), load("com/tencent/mobileqq/activity/JumpActivity"));
                intent.setData(uri);
                v.getContext().startActivity(intent);
            }
        };
    }

    public static View.OnClickListener clickTheComing() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showToastShort(v.getContext(), "对不起,此功能尚在开发中");
            }
        };
    }

    public static LinearLayout.LayoutParams newLinearLayoutParams(int width, int height, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams ret = new LinearLayout.LayoutParams(width, height);
        ret.setMargins(left, top, right, bottom);
        return ret;
    }

    public static LinearLayout.LayoutParams newLinearLayoutParams(int width, int height, int margins) {
        return newLinearLayoutParams(width, height, margins, margins, margins, margins);
    }

    public static void listView_setAdapter(View v, ListAdapter adapter) {
        try {
            Class<?> clazz = v.getClass();
            clazz.getMethod("setAdapter", ListAdapter.class).invoke(v, adapter);
        } catch (Exception e) {
            Utils.log("tencent_ListView->setAdapter: " + e.toString());
        }
    }

    public static CompoundButton switch_new(Context ctx) {
        try {
            Class<?> clazz = load("com/tencent/widget/Switch");
            return (CompoundButton) clazz.getConstructor(Context.class).newInstance(ctx);
        } catch (Exception e) {
            Utils.log("Switch->new: " + e.toString());
        }
        return null;
    }
}
