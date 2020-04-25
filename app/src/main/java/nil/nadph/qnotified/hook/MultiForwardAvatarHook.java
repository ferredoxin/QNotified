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
package nil.nadph.qnotified.hook;

import android.content.Context;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class MultiForwardAvatarHook extends BaseDelayableHook {

    public static final String qn_multi_forward_avatar_profile = "qn_multi_forward_avatar_profile";
    private static final MultiForwardAvatarHook self = new MultiForwardAvatarHook();
    private boolean inited = false;
    private Field mLeftCheckBoxVisible = null;

    private MultiForwardAvatarHook() {
    }

    public static MultiForwardAvatarHook get() {
        return self;
    }

    /**
     * Target TIM or QQ<=7.6.0
     * Here we use a simple workaround, not use DexKit
     *
     * @param v the view in bubble
     * @return message or null
     */
    @Nullable
    @Deprecated
    public static Object getChatMessageByView(View v) {
        Class cl_AIOUtils = load("com/tencent/mobileqq/activity/aio/AIOUtils");
        if (cl_AIOUtils == null) return null;
        try {
            return invoke_static_any(cl_AIOUtils, v, View.class, load("com.tencent.mobileqq.data.ChatMessage"));
        } catch (NoSuchMethodException e) {
            return null;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            findAndHookMethod(load("com/tencent/mobileqq/activity/aio/BaseBubbleBuilder"), "onClick", View.class, new XC_MethodHook(49) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    Context ctx = (Context) iget_object_or_null(param.thisObject, "a", Context.class);
                    if (ctx == null) ctx = getFirstNSFByType(param.thisObject, Context.class);
                    View view = (View) param.args[0];
                    if (ctx == null || isLeftCheckBoxVisible()) return;
                    if (ctx.getClass().getName().equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                        if (view.getClass().getName().equals("com.tencent.mobileqq.vas.avatar.VasAvatar")) {
                            String uinstr = (String) iget_object_or_null(view, "a", String.class);
                            try {
                                long uin = Long.parseLong(uinstr);
                                if (uin > 10000) {
                                    MainHook.openProfileCard(ctx, uin);
                                }
                            } catch (Exception e) {
                                log(e);
                            }
                        } else if (view.getClass().equals(ImageView.class) ||
                                view.getClass().equals(load("com.tencent.widget.CommonImageView"))) {
                            Object msg = getChatMessageByView(view);
                            if (msg == null) return;
                            String senderuin = (String) iget_object_or_null(msg, "senderuin");
                            try {
                                long uin = Long.parseLong(senderuin);
                                if (uin > 10000) {
                                    MainHook.openProfileCard(ctx, uin);
                                }
                            } catch (Exception e) {
                                log(e);
                            }
                        }
                    }
                }
            });
            Field
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int[] getPreconditions() {
        return new int[0];
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
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_multi_forward_avatar_profile, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrDefault(qn_multi_forward_avatar_profile, true);
        } catch (Exception e) {
            log(e);
            return true;
        }
    }

    public boolean isLeftCheckBoxVisible() {
        Field a = null, b = null;
        try {
            if (mLeftCheckBoxVisible != null) {
                return mLeftCheckBoxVisible.getBoolean(null);
            } else {
                for (Field f : load("com/tencent/mobileqq/activity/aio/BaseChatItemLayout").getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && f.getType().equals(boolean.class)) {
                        if ("a".equals(f.getName())) a = f;
                        if ("b".equals(f.getName())) b = f;
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
}
