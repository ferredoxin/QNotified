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
import android.content.DialogInterface;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.QQMessageFacade;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.*;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
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
     * Here we use DexKit!!!
     *
     * @param v the view in bubble
     * @return message or null
     */
    @Nullable
    @Deprecated
    public static Object getChatMessageByView(View v) {
        Class cl_AIOUtils = DexKit.doFindClass(DexKit.C_AIO_UTILS);
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
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    Context ctx = iget_object_or_null(param.thisObject, "a", Context.class);
                    if (ctx == null) ctx = getFirstNSFByType(param.thisObject, Context.class);
                    View view = (View) param.args[0];
                    if (ctx == null || isLeftCheckBoxVisible()) return;
                    String activityName = ctx.getClass().getName();
                    if (activityName.equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                        if (view.getClass().getName().equals("com.tencent.mobileqq.vas.avatar.VasAvatar")) {
                            String uinstr = iget_object_or_null(view, "a", String.class);
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
                    } /*else if (activityName.endsWith(".SplashActivity") || activityName.endsWith(".ChatActivity")) {
                        final Object msg = getChatMessageByView(view);
                        if (msg == null) return;
                        Object chatpie = null;
                        try {
                            Object fmgr = invoke_virtual(ctx, "getSupportFragmentManager");
                            Object fragment = invoke_virtual(fmgr, "findFragmentByTag", "com.tencent.mobileqq.activity.ChatFragment", String.class);
                            chatpie = invoke_virtual(fragment, "a", _BaseChatPie());
                        } catch (Exception e) {
                            log(e);
                        }
                        if (chatpie != null) {
                            final Object finalChatpie = chatpie;
                            CustomDialog.createFailsafe(ctx).setTitle(Utils.getShort$Name(msg)).setMessage(chatpie.getClass().getName() + "\n" + msg.toString())
                                    .setCancelable(true).setPositiveButton("确定", null).setNegativeButton("撤回", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    QQMessageFacade.revokeMessage(msg);
                                }
                            }).show();
                            param.setResult(true);
                        }
                    }*/
                }
            });
            Class<?> listener = FindAvatarLongClickListener.getLongClickListenerClass();
            Method onLongClick = listener.getMethod("onLongClick", View.class);
            XposedBridge.hookMethod(onLongClick, new XC_MethodHook(48) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    Object builder = null;
                    for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                        if (Modifier.isFinal(f.getModifiers()) && !Modifier.isStatic(f.getModifiers())) {
                            f.setAccessible(true);
                            builder = f.get(param.thisObject);
                            break;
                        }
                    }
                    Context ctx = iget_object_or_null(builder, "a", Context.class);
                    if (ctx == null) ctx = getFirstNSFByType(builder, Context.class);
                    View view = (View) param.args[0];
                    if (ctx == null || isLeftCheckBoxVisible()) return;
                    String activityName = ctx.getClass().getName();
                    if (activityName.equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                        Object msg = getChatMessageByView(view);
                        if (msg == null) return;
                        CustomDialog.createFailsafe(ctx).setTitle(Utils.getShort$Name(msg)).setMessage(msg.toString())
                                .setCancelable(true).setPositiveButton("确定", null).show();
                    }
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_AIO_UTILS), new FindAvatarLongClickListener()};
    }

    private static final String cache_avatar_long_click_listener_class = "cache_avatar_long_click_listener_class";
    private static final String cache_avatar_long_click_listener_version_code = "cache_avatar_long_click_listener_version_code";

    private static class FindAvatarLongClickListener extends Step {

        public static Class<?> getLongClickListenerClass() {
            String klass = null;
            ConfigManager cache = ConfigManager.getCache();
            int lastVersion = cache.getIntOrDefault(cache_avatar_long_click_listener_version_code, 0);
            int version = getHostInfo(getApplication()).versionCode;
            if (version == lastVersion) {
                String name = cache.getString(cache_avatar_long_click_listener_class);
                if (name != null && name.length() > 0) {
                    klass = name;
                }
            }
            Class<?> c = Initiator.load(klass);
            if (c != null) return c;
            Class<?> decl = Initiator.load("com/tencent/mobileqq/activity/aio/BaseBubbleBuilder");
            if (decl == null) return null;
            String fname = null;
            for (Field f : decl.getDeclaredFields()) {
                if (f.getType().equals(View.OnLongClickListener.class)) {
                    fname = f.getName();
                    break;
                }
            }
            if (fname == null) {
                log("getLongClickListenerClass: field name is null");
                return null;
            }
            DexMethodDescriptor _init_ = null;
            byte[] dex = DexKit.getClassDeclaringDex("Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;", new int[]{7, 11, 6});
            for (DexMethodDescriptor m : DexFlow.getDeclaredDexMethods(dex, "Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;")) {
                if ("<init>".equals(m.name)) {
                    _init_ = m;
                    break;
                }
            }
            DexFieldDescriptor f = new DexFieldDescriptor("Lcom/tencent/mobileqq/activity/aio/BaseBubbleBuilder;",
                    fname, DexMethodDescriptor.getTypeSig(View.OnLongClickListener.class));
            try {
                klass = DexFlow.guessNewInstanceType(dex, _init_, f);
            } catch (Exception e) {
                log(e);
                return null;
            }
            if (klass != null && klass.startsWith("L")) {
                klass = klass.replace('/', '.').substring(1, klass.length() - 1);
                cache.putString(cache_avatar_long_click_listener_class, klass);
                cache.putInt(cache_avatar_long_click_listener_version_code, version);
                try {
                    cache.save();
                } catch (IOException e) {
                    log(e);
                }
                return Initiator.load(klass);
            }
            return null;
        }

        @Override
        public boolean step() {
            return getLongClickListenerClass() != null;
        }

        @Override
        public boolean isDone() {
            try {
                ConfigManager cache = ConfigManager.getCache();
                int lastVersion = cache.getIntOrDefault(cache_avatar_long_click_listener_version_code, 0);
                if (getHostInfo(getApplication()).versionCode != lastVersion) {
                    return false;
                }
                String name = cache.getString(cache_avatar_long_click_listener_class);
                return name != null && name.length() > 0;
            } catch (Exception e) {
                log(e);
                return false;
            }
        }

        @Override
        public int getPriority() {
            return 20;
        }

        @Override
        public String getDescription() {
            return "定位com/tencent/mobileqq/activity/aio/BaseBubbleBuilder$3";
        }
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
