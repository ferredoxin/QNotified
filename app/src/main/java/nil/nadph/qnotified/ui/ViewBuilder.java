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
package nil.nadph.qnotified.ui;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.dip2sp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import cc.ioctl.H;
import xyz.nextalone.base.MultiItemDelayableHook;
import xyz.nextalone.util.SystemServiceUtils;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.config.SwitchConfigItem;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.widget.FunctionButton;
import nil.nadph.qnotified.ui.widget.FunctionDummy;
import nil.nadph.qnotified.ui.widget.FunctionSwitch;
import nil.nadph.qnotified.util.NonUiThread;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;
import org.ferredoxin.ferredoxin_ui.base.UiSwitchItem;
import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference;

public class ViewBuilder {

    public static ViewGroup newListItemSwitch(Context ctx, CharSequence title,
        CharSequence desc, boolean on, boolean enabled, CompoundButton.OnCheckedChangeListener listener) {
        FunctionSwitch root = new FunctionSwitch(ctx);
        root.getTitle().setText(title);
        CompoundButton sw = root.getSwitch();
        sw.setChecked(on);
        sw.setEnabled(enabled);
        sw.setOnCheckedChangeListener(listener);
        if (!TextUtils.isEmpty(desc)) {
            root.getDesc().setText(desc);
        }
        return root;
    }

    public static ViewGroup newListItemSwitch(Context ctx, CharSequence title, CharSequence desc, boolean on, CompoundButton.OnCheckedChangeListener listener){
        return newListItemSwitch(ctx, title, desc, on, true, listener);
    }

    public static ViewGroup newListItemSwitchConfig(Context ctx, CharSequence title,
        CharSequence desc, final String key, boolean defVal) {
        boolean on = ConfigManager.getDefaultConfig().getBooleanOrDefault(key, defVal);
        ViewGroup root = newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> {
                try {
                    ConfigManager mgr = ConfigManager.getDefaultConfig();
                    mgr.putBoolean(key, isChecked);
                    mgr.save();
                } catch (Exception e) {
                    Utils.log(e);
                    Toasts.info(ctx, e.toString());
                }
            });
        root.setId(key.hashCode());
        return root;
    }


    public static ViewGroup newListItemSwitchConfigNext(Context ctx, CharSequence title,
        CharSequence desc, final String key, boolean defVal) {
        boolean on = ConfigManager.getDefaultConfig().getBooleanOrDefault(key, defVal);
        ViewGroup root = newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> {
                try {
                    ConfigManager mgr = ConfigManager.getDefaultConfig();
                    mgr.putBoolean(key, isChecked);
                    mgr.save();
                    Toasts.info(ctx,
                        "重启" + HostInfo.getHostInfo().getHostName() + "生效");
                } catch (Throwable e) {
                    Utils.log(e);
                    Toasts.info(ctx, e.toString());
                }
            });
        root.setId(key.hashCode());
        return root;
    }

    public static ViewGroup newListItemSwitchFriendConfigNext(Context ctx, CharSequence title,
        CharSequence desc, final String key, boolean defVal) {
        ConfigManager mgr = ExfriendManager.getCurrent().getConfig();
        boolean on = mgr.getBooleanOrDefault(key, defVal);
        ViewGroup root = newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> {
                try {

                    mgr.putBoolean(key, isChecked);
                    mgr.save();
                    Toasts.info(ctx, "设置成功");
                } catch (Throwable e) {
                    Utils.log(e);
                    Toasts.info(ctx, e.toString());
                }
            });
        root.setId(key.hashCode());
        return root;
    }

    public static ViewGroup newListItemSwitchConfigNext(Context ctx, CharSequence title,
        CharSequence desc, final SwitchConfigItem item) {
        boolean on = item.isEnabled();
        ViewGroup root = newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> {
                try {
                    item.setEnabled(isChecked);
                    Toasts.info(ctx,
                        "重启" + HostInfo.getHostInfo().getHostName() + "生效");
                } catch (Throwable e) {
                    Utils.log(e);
                    Toasts.info(ctx, e.toString());
                }
            });
        root.setId(title.hashCode());
        return root;
    }

    public static ViewGroup newListItemHookSwitchInit(final Context ctx, CharSequence title,
        CharSequence desc, final BaseDelayableHook hook) {
        boolean on = hook.isEnabled();
        return newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> {
                if (!hook.isInited() && isChecked) {
                    new Thread(() -> {
                        hook.setEnabled(true);
                        doSetupAndInit(ctx, hook);
                    }).start();
                } else {
                    hook.setEnabled(isChecked);
                }
            });
    }

    public static ViewGroup newListItemHookSwitchInit(final Context ctx, UiSwitchItem uiSwitchItem) {
        UiSwitchPreference preference = uiSwitchItem.getPreference();
        Boolean on = preference.getValue().getValue();
        on = on != null && on;
        ViewGroup root = newListItemSwitch(ctx, preference.getTitle(), preference.getSummary(), on, preference.getValid(),
            (buttonView, isChecked) -> preference.getValue().setValue(isChecked));
        root.setId(uiSwitchItem.getClass().getName().hashCode());
        return root;
    }

    public static ViewGroup newListItemConfigSwitchIfValid(final Context ctx,
        CharSequence title, CharSequence desc, final SwitchConfigItem item) {
        boolean on = item.isEnabled();
        FunctionSwitch root = (FunctionSwitch) newListItemSwitch(ctx, title, desc, on,
            (buttonView, isChecked) -> item.setEnabled(isChecked));
        root.getSwitch().setEnabled(item.isValid());
        root.setId(item.hashCode());
        return root;
    }

    @NonUiThread
    public static void doSetupAndInit(final Context ctx, BaseDelayableHook hook) {
        final CustomDialog[] pDialog = new CustomDialog[1];
        Throwable err = null;
        try {
            for (Step s : hook.getPreconditions()) {
                if (s.isDone()) {
                    continue;
                }
                final String name = s.getDescription();
                Utils.runOnUiThread(() -> {
                    if (pDialog[0] == null) {
                        pDialog[0] = CustomDialog.create(ctx);
                        pDialog[0].setCancelable(false);
                        pDialog[0].setTitle("请稍候");
                        pDialog[0].show();
                    }
                    pDialog[0].setMessage("QNotified正在初始化:\n" + name + "\n每个类一般不会超过一分钟");

                });
                s.step();
            }
        } catch (Throwable stepErr) {
            err = stepErr;
        }
        if (err == null) {
            if (hook.isTargetProc()) {
                boolean success = false;
                try {
                    success = hook.init();
                } catch (Throwable ex) {
                    err = ex;
                }
                if (!success) {
                    Utils.runOnUiThread(() -> Toasts.error(ctx, "初始化失败"));
                }
            }
            SyncUtils.requestInitHook(hook.getId(), hook.getEffectiveProc());
        }
        if (err != null) {
            Throwable finalErr = err;
            Utils.runOnUiThread(() -> CustomDialog.createFailsafe(ctx).setTitle("发生错误")
                .setMessage(finalErr.toString())
                .setCancelable(true).setPositiveButton(android.R.string.ok, null).show());
        }
        if (pDialog[0] != null) {
            Utils.runOnUiThread(() -> pDialog[0].dismiss());
        }
    }

    @NonUiThread
    public static void doSetupForPrecondition(final Context ctx, BaseDelayableHook hook) {
        final CustomDialog[] pDialog = new CustomDialog[1];
        Throwable error = null;
        try {
            for (Step i : hook.getPreconditions()) {
                if (i.isDone()) {
                    continue;
                }
                final String name = i.getDescription();
                Utils.runOnUiThread(() -> {
                    if (pDialog[0] == null) {
                        pDialog[0] = CustomDialog.create(ctx);
                        pDialog[0].setCancelable(false);
                        pDialog[0].setTitle("请稍候");
                        pDialog[0].show();
                    }
                    pDialog[0].setMessage("QNotified正在初始化:\n" + name + "\n每个类一般不会超过一分钟");
                });
                i.step();
            }
        } catch (Throwable e) {
            error = e;
        }
        if (error != null) {
            Throwable finalErr = error;
            Utils.runOnUiThread(() -> CustomDialog.createFailsafe(ctx).setTitle("发生错误")
                .setMessage(finalErr.toString())
                .setCancelable(true).setPositiveButton(android.R.string.ok, null).show());
        }
        if (pDialog[0] != null) {
            Utils.runOnUiThread(() -> pDialog[0].dismiss());
        }
    }

    public static ViewGroup newListItemSwitchStub(Context ctx, CharSequence title,
        CharSequence desc) {
        return newListItemSwitch(ctx, title, desc, false,
            (buttonView, isChecked) -> {
                buttonView.setChecked(false);
                Toasts.info(buttonView.getContext(), "对不起,此功能尚在开发中");
            });
    }

    public static ViewGroup newListItemDummy(Context ctx, CharSequence title,
        CharSequence desc, CharSequence
        value) {
        FunctionDummy root = new FunctionDummy(ctx);
        root.getTitle().setText(title);
        if (!TextUtils.isEmpty(desc)) {
            root.getDesc().setText(desc);
        }
        if (!TextUtils.isEmpty(value)) {
            root.getValue().setText(value);
        }
        return root;
    }

    public static ViewGroup newListItemButton(Context ctx, CharSequence title,
        CharSequence desc, CharSequence
        value, View.OnClickListener listener) {
        FunctionButton root = new FunctionButton(ctx);
        root.getTitle().setText(title);
        root.setOnClickListener(listener);
        if (!TextUtils.isEmpty(desc)) {
            root.getDesc().setText(desc);
        }
        if (!TextUtils.isEmpty(value)) {
            root.getValue().setText(value);
        }
        return root;
    }

    public static ViewGroup newListItemButtonIfValid(Context ctx, CharSequence title,
        CharSequence desc,
        CharSequence value, MultiItemDelayableHook hook) {
        View.OnClickListener listener;
        if (hook.isValid()) {
            listener = hook.listener();
        } else {
            listener = (v -> Toasts.error(v.getContext(), "此功能暂不支持当前版本" + H.getAppName()));
        }
        return newListItemButton(ctx, title, desc, value, listener);
    }

    public static ViewGroup newListItemButtonIfValid(Context ctx, CharSequence title,
        CharSequence desc,
        CharSequence value, BaseDelayableHook hook, Class<? extends Activity> activity) {
        View.OnClickListener listener;
        if (hook.isValid()) {
            listener = clickToProxyActAction(activity);
        } else {
            listener = (v -> Toasts.error(v.getContext(), "此功能暂不支持当前版本" + H.getAppName()));
        }
        return newListItemButton(ctx, title, desc, value, listener);
    }

    public static ViewGroup newListItemButtonIfValid(Context ctx, CharSequence title,
        CharSequence desc,
        CharSequence value, BaseDelayableHook hook, View.OnClickListener listener) {
        if (!hook.isValid()) {
            listener = (v -> Toasts.error(v.getContext(), "此功能暂不支持当前版本" + H.getAppName()));
        }
        return newListItemButton(ctx, title, desc, value, listener);
    }

    public static LinearLayout subtitle(Context ctx, CharSequence title) {
        return subtitle(ctx, title, 0);
    }

    public static LinearLayout subtitle(Context ctx, CharSequence title, int color) {
        return subtitle(ctx, title, color, false);
    }

    public static LinearLayout subtitle(Context ctx, CharSequence title, int color,
        boolean isSelectable) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(ctx);
        tv.setTextIsSelectable(isSelectable);
        tv.setText(title);
        tv.setTextSize(dip2sp(ctx, 13));
        if (color == 0) {
            tv.setTextColor(ResUtils.skin_gray3);
        } else {
            tv.setTextColor(color);
        }
        tv.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        int m = dip2px(ctx, 14);
        tv.setPadding(m, m / 5, m / 5, m / 5);
        ll.addView(tv);
        return ll;
    }

    public static LinearLayout largeSubtitle(Context ctx, CharSequence title) {
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setGravity(Gravity.CENTER_VERTICAL);
        TextView tv = new TextView(ctx);
        tv.setTextIsSelectable(false);
        tv.setText(title);
        tv.setTextSize(dip2sp(ctx, 13));
        tv.setTextColor(HostInfo.getHostInfo().getApplication().getResources()
            .getColor(R.color.colorPrimary));
        tv.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        int m = dip2px(ctx, 14);
        tv.setPadding(m, m, m / 5, m / 5);
        ll.addView(tv);
        return ll;
    }

    public static View.OnClickListener clickToProxyActAction(
        final @NonNull Class<? extends Activity> clz) {
        return v -> {
            Context ctx = v.getContext();
            ctx.startActivity(new Intent(ctx, clz));
        };
    }

    public static View.OnClickListener clickToUrl(final String url) {
        return v -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            v.getContext().startActivity(intent);
        };
    }

    public static View.OnClickListener clickToChat() {
        return v -> {
            Uri uri = Uri.parse(
                "mqqwpa://im/chat?chat_type=wpa&uin=1041703712&version=1&src_type=web&web_src=null");
            Intent intent = new Intent(v.getContext(),
                load("com/tencent/mobileqq/activity/JumpActivity"));
            intent.setData(uri);
            v.getContext().startActivity(intent);
        };
    }

    public static View.OnClickListener clickTheComing() {
        return v -> Toasts.info(v.getContext(), "对不起,此功能尚在开发中");
    }

    public static View.OnLongClickListener longClickToTest() {
        return v -> {
            Toasts.info(v.getContext(), "TEST");
            return false;
        };
    }

    public static LinearLayout.LayoutParams newLinearLayoutParams(int width, int height, int left,
        int top, int right, int bottom) {
        LinearLayout.LayoutParams ret = new LinearLayout.LayoutParams(width, height);
        ret.setMargins(left, top, right, bottom);
        return ret;
    }

    public static LinearLayout.LayoutParams newLinearLayoutParams(int width, int height,
        int gravity, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams ret = new LinearLayout.LayoutParams(width, height);
        ret.setMargins(left, top, right, bottom);
        ret.gravity = gravity;
        return ret;
    }

    public static LinearLayout.LayoutParams newLinearLayoutParams(int width, int height,
        int margins) {
        return newLinearLayoutParams(width, height, margins, margins, margins, margins);
    }

    public static RelativeLayout.LayoutParams newRelativeLayoutParamsM(int width, int height,
        int left, int top, int right, int bottom, int... verbArgv) {
        RelativeLayout.LayoutParams ret = new RelativeLayout.LayoutParams(width, height);
        ret.setMargins(left, top, right, bottom);
        for (int i = 0; i < verbArgv.length / 2; i++) {
            ret.addRule(verbArgv[i * 2], verbArgv[i * 2 + 1]);
        }
        return ret;
    }

    public static RelativeLayout.LayoutParams newRelativeLayoutParams(int width, int height,
        int... verbArgv) {
        RelativeLayout.LayoutParams ret = new RelativeLayout.LayoutParams(width, height);
        for (int i = 0; i < verbArgv.length / 2; i++) {
            ret.addRule(verbArgv[i * 2], verbArgv[i * 2 + 1]);
        }
        return ret;
    }

    public static void listView_setAdapter(View v, ListAdapter adapter) {
        try {
            Class<?> clazz = v.getClass();
            clazz.getMethod("setAdapter", ListAdapter.class).invoke(v, adapter);
        } catch (Exception e) {
            Utils.logi("tencent_ListView->setAdapter: " + e);
        }
    }

    public static LinearLayout newDialogClickableItemClickToCopy(final Context ctx, String title,
        String value, ViewGroup vg, boolean attach, View.OnClickListener l) {
        return newDialogClickableItem(ctx, title, value, l, v -> {
            Context c = v.getContext();
            String msg = ((TextView) v).getText().toString();
            if (msg.length() > 0) {
                SystemServiceUtils.copyToClipboard(ctx, msg);
                Toasts.info(c, "已复制文本");
            }
            return true;
        }, vg, attach);
    }

    public static LinearLayout newDialogClickableItem(final Context ctx, String title, String value,
        View.OnClickListener l,
        View.OnLongClickListener ll, ViewGroup vg, boolean attach) {
        LinearLayout root = (LinearLayout) LayoutInflater.from(ctx)
            .inflate(R.layout.dialog_clickable_item, vg, false);
        TextView t = root.findViewById(R.id.dialogClickableItemTitle);
        TextView v = root.findViewById(R.id.dialogClickableItemValue);
        t.setText(title);
        v.setText(value);
        if (l != null) {
            v.setOnClickListener(l);
        }
        if (ll != null) {
            v.setOnLongClickListener(ll);
        }
        if (l != null || ll != null) {
            ViewCompat.setBackground(v, ResUtils.getDialogClickableItemBackground());
        }
        if (attach) {
            vg.addView(root);
        }
        return root;
    }

    public static LinearLayout newDialogClickableItem(final Context ctx, String title, String value,
        View.OnLongClickListener ll, ViewGroup vg, boolean attach) {
        return newDialogClickableItem(ctx, title, value, null, ll, vg, attach);
    }

}
