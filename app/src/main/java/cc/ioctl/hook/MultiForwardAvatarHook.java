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

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.getFirstNSFByType;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_static_any;
import static nil.nadph.qnotified.util.Utils.getShort$Name;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.UiThread;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class MultiForwardAvatarHook extends CommonDelayableHook {

    public static final MultiForwardAvatarHook INSTANCE = new MultiForwardAvatarHook();
    private static Field mLeftCheckBoxVisible = null;

    private MultiForwardAvatarHook() {
        super("qn_multi_forward_avatar_profile",
            new DexDeobfStep(DexKit.C_AIO_UTILS)/*, new FindAvatarLongClickListener()*/);
    }

    /**
     * Target TIM or QQ<=7.6.0 Here we use DexKit!!!
     *
     * @param v the view in bubble
     * @return message or null
     */
    @Nullable
    //@Deprecated
    public static Object getChatMessageByView(View v) {
        Class cl_AIOUtils = DexKit.doFindClass(DexKit.C_AIO_UTILS);
        if (cl_AIOUtils == null) {
            return null;
        }
        try {
            return invoke_static_any(cl_AIOUtils, v, View.class,
                load("com.tencent.mobileqq.data.ChatMessage"));
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    @UiThread
    private static void createAndShowDialogForTroop(final Context __ctx, final Object msg) {
        if (msg == null) {
            loge("createAndShowDialogForTroop/E msg == null");
            return;
        }
        CustomDialog dialog = CustomDialog.createFailsafe(__ctx).setTitle(getShort$Name(msg))
            .setNeutralButton("资料卡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String senderuin = (String) iget_object_or_null(msg, "senderuin");
                        long uin = Long.parseLong(senderuin);
                        if (uin > 10000) {
                            MainHook.openProfileCard(__ctx, uin);
                        }
                    } catch (Exception e) {
                        log(e);
                    }
                }
            }).setPositiveButton("确认", null).setCancelable(true)
            .setNegativeButton("详情", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createAndShowDialogForDetail(__ctx, msg);
                }
            });
        Context ctx = dialog.getContext();
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        int p = Utils.dip2px(ctx, 10);
        ll.setPadding(p, p / 3, p, p / 3);
        String senderuin = (String) iget_object_or_null(msg, "senderuin");
        String frienduin = (String) iget_object_or_null(msg, "frienduin");
        ViewBuilder.newDialogClickableItemClickToCopy(ctx, "群号", frienduin, ll, true);
        ViewBuilder.newDialogClickableItemClickToCopy(ctx, "成员", senderuin, ll, true);
        TextView tv = new TextView(ctx);
        tv.setText("(长按可复制)");
        ll.addView(tv);
        dialog.setView(ll);
        dialog.show();
    }

    @UiThread
    private static void createAndShowDialogForPrivateMsg(final Context __ctx, final Object msg) {
        if (msg == null) {
            loge("createAndShowDialogForPrivateMsg/E msg == null");
            return;
        }
        CustomDialog dialog = CustomDialog.createFailsafe(__ctx).setTitle(getShort$Name(msg))
            .setNeutralButton("资料卡", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        String senderuin = (String) iget_object_or_null(msg, "senderuin");
                        long uin = Long.parseLong(senderuin);
                        if (uin > 10000) {
                            MainHook.openProfileCard(__ctx, uin);
                        }
                    } catch (Exception e) {
                        log(e);
                    }
                }
            }).setPositiveButton("确认", null).setCancelable(true)
            .setNegativeButton("详情", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    createAndShowDialogForDetail(__ctx, msg);
                }
            });
        Context ctx = dialog.getContext();
        LinearLayout ll = new LinearLayout(ctx);
        int p = Utils.dip2px(ctx, 10);
        ll.setPadding(p, p / 3, p, p / 3);
        ll.setOrientation(LinearLayout.VERTICAL);
        String senderuin = (String) iget_object_or_null(msg, "senderuin");
        ViewBuilder.newDialogClickableItemClickToCopy(ctx, "发送者", senderuin, ll, true);
        TextView tv = new TextView(ctx);
        tv.setText("(长按可复制)");
        ll.addView(tv);
        dialog.setView(ll);
        dialog.show();
    }

    @UiThread
    public static void createAndShowDialogForDetail(final Context ctx, final Object msg) {
        if (msg == null) {
            loge("createAndShowDialogForDetail/E msg == null");
            return;
        }
        CustomDialog.createFailsafe(ctx).setTitle(Utils.getShort$Name(msg))
            .setMessage(msg.toString())
            .setCancelable(true).setPositiveButton("确定", null).show();
    }

    public static boolean isLeftCheckBoxVisible() {
        Field a = null, b = null;
        try {
            if (mLeftCheckBoxVisible != null) {
                return mLeftCheckBoxVisible.getBoolean(null);
            } else {
                for (Field f : load("com/tencent/mobileqq/activity/aio/BaseChatItemLayout")
                    .getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers())
                        && f.getType().equals(boolean.class)) {
                        if ("a".equals(f.getName())) {
                            a = f;
                        }
                        if ("b".equals(f.getName())) {
                            b = f;
                        }
                    }
                }
                if (a != null) {
                    mLeftCheckBoxVisible = a;
                    return a.getBoolean(null);
                }
                if (b != null) {
                    mLeftCheckBoxVisible = b;
                    return b.getBoolean(null);
                }
                return false;
            }
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean initOnce() {
        try {
            findAndHookMethod(load("com/tencent/mobileqq/activity/aio/BaseBubbleBuilder"),
                "onClick", View.class, new XC_MethodHook(49) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        Context ctx = iget_object_or_null(param.thisObject, "a", Context.class);
                        if (ctx == null) {
                            ctx = getFirstNSFByType(param.thisObject, Context.class);
                        }
                        View view = (View) param.args[0];
                        if (ctx == null || isLeftCheckBoxVisible()) {
                            return;
                        }
                        String activityName = ctx.getClass().getName();
                        boolean needShow = false;
                        if (activityName
                            .equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                            if (view.getClass().getName()
                                .equals("com.tencent.mobileqq.vas.avatar.VasAvatar")) {
                                needShow = true;
                            } else if (view.getClass().equals(ImageView.class) ||
                                view.getClass()
                                    .equals(load("com.tencent.widget.CommonImageView"))) {
                                needShow = true;
                            }
                        }
                        if (!needShow) {
                            return;
                        }
                        Object msg = getChatMessageByView(view);
                        if (msg == null) {
                            return;
                        }
                        int istroop = (int) iget_object_or_null(msg, "istroop");
                        if (istroop == 1 || istroop == 3000) {
                            createAndShowDialogForTroop(ctx, msg);
                        } else if (istroop == 0) {
                            createAndShowDialogForPrivateMsg(ctx, msg);
                        } else {
                            createAndShowDialogForDetail(ctx, msg);
                        }
                    }
                });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
