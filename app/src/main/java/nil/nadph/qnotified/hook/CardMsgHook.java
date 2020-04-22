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


import android.app.Activity;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.InterceptLayout;
import nil.nadph.qnotified.ui.TouchEventToLongClickAdapter;
import nil.nadph.qnotified.util.CustomMenu;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.*;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;


public class CardMsgHook extends BaseDelayableHook {
    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    public static final String qn_send_card_msg = "qn_send_card_msg";
    private static final CardMsgHook self = new CardMsgHook();
    private boolean inited = false;

    private CardMsgHook() {
    }

    public static CardMsgHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            //Begin: send btn
            String _BaseChatPie_init_name = "e";
            try {
                Application ctx = Utils.getApplication();
                if (getHostInfo(ctx).versionName.indexOf(0) == '7') {
                    _BaseChatPie_init_name = "d";
                }
            } catch (Throwable e) {
                //Should not happen
                log(e);
            }
            final Class cl_BaseChatPie = load("com.tencent.mobileqq.activity.BaseChatPie");
            Method _BaseChatPie_init = null;
            for (Method method : cl_BaseChatPie.getDeclaredMethods()) {
                if (method.getParameterTypes().length != 0
                        || !method.getReturnType().equals(void.class)) continue;
                if (method.getName().equals(_BaseChatPie_init_name)) {
                    _BaseChatPie_init = method;
                    break;
                }
            }
            XposedBridge.hookMethod(_BaseChatPie_init, new XC_MethodHook(40) {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        Object chatPie = param.thisObject;
                        //Class cl_PatchedButton = load("com/tencent/widget/PatchedButton");
                        final ViewGroup viewGroup = (ViewGroup) invoke_virtual(chatPie, "a", ViewGroup.class);
                        if (viewGroup == null) return;
                        Context ctx = viewGroup.getContext();
                        int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
                        View sendBtn = viewGroup.findViewById(fun_btn);
                        final Object qqApp = iget_object_or_null(param.thisObject, "a", _QQAppInterface());
                        final Object session = iget_object_or_null(param.thisObject, "a", _SessionInfo());
                        if (!sendBtn.getParent().getClass().getName().equals(InterceptLayout.class.getName())) {
                            InterceptLayout layout = InterceptLayout.setupRudely(sendBtn);
                            layout.setTouchInterceptor(new TouchEventToLongClickAdapter() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    ViewGroup vg = (ViewGroup) v;
                                    Context ctx = v.getContext();
                                    if (event.getAction() == MotionEvent.ACTION_DOWN &&
                                            vg.getChildCount() != 0 && vg.getChildAt(0).isEnabled()) {
                                        return false;
                                    }
                                    return super.onTouch(v, event);
                                }

                                @Override
                                public boolean onLongClick(View v) {
                                    try {
                                        ViewGroup vg = (ViewGroup) v;
                                        Context ctx = v.getContext();
                                        if (vg.getChildCount() != 0 && !vg.getChildAt(0).isEnabled()) {
                                            EditText input = (EditText) viewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
                                            String text = input.getText().toString();
                                            if (text.length() == 0) {
                                                showToast(ctx, TOAST_TYPE_ERROR, "请先输入卡片代码", Toast.LENGTH_SHORT);
                                            }
                                            return true;
                                        }
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                    return false;
                                }
                            }.setLongPressTimeoutFactor(1.5f));
                        }
                        sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                Context ctx = v.getContext();
                                EditText input = (EditText) viewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
                                String text = input.getText().toString();
                                if (((TextView) v).length() == 0) {
                                    return false;
                                } else {
                                    if (text.contains("<?xml")) {
                                        try {
                                            Object structMsg = invoke_static(DexKit.doFindClass(DexKit.C_TEST_STRUCT_MSG), "a", text, String.class, load("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                                            if (structMsg != null) {
                                                XposedHelpers.callStaticMethod(DexKit.doFindClass(DexKit.C_FACADE), "a", qqApp, session, structMsg);
                                                input.setText("");
                                                return true;
                                            } else {
                                                Utils.showToast(ctx, TOAST_TYPE_ERROR, "XML语法错误(代码有误)", Toast.LENGTH_SHORT);
                                                return true;
                                            }
                                        } catch (Throwable e) {
                                            if (e instanceof InvocationTargetException) e = e.getCause();
                                            log(e);
                                            Utils.showToast(ctx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_SHORT);
                                        }
                                    } else if (text.contains("{\"")) {
                                        try {
                                            Object arkMsg = load("com.tencent.mobileqq.data.ArkAppMessage").newInstance();
                                            if ((boolean) invoke_virtual(arkMsg, "fromAppXml", text, String.class)) {
                                                XposedHelpers.callStaticMethod(DexKit.doFindClass(DexKit.C_FACADE), "a", qqApp, session, arkMsg);
                                                //invoke_static(DexKit.doFindClass(DexKit.C_FACADE), "a", qqApp, session, arkMsg, load("com.tencent.mobileqq.app.QQAppInterface"), _SessionInfo(), arkMsg.getClass());
                                                input.setText("");
                                                return true;
                                            } else {
                                                Utils.showToast(ctx, TOAST_TYPE_ERROR, "JSON语法错误(代码有误)", Toast.LENGTH_SHORT);
                                                return true;
                                            }
                                        } catch (Throwable e) {
                                            if (e instanceof InvocationTargetException) e = e.getCause();
                                            log(e);
                                            Utils.showToast(ctx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_SHORT);
                                        }
                                    }
                                }
                                return true;
                            }
                        });
                    } catch (Throwable e) {
                        log(e);
                    }
                }
            });
            //End: send btn
            //Begin: ArkApp
            Class cl_ArkAppItemBuilder = DexKit.doFindClass(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER);
            findAndHookMethod(cl_ArkAppItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
            for (Method m : cl_ArkAppItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
            //End: ArkApp
            //Begin: StructMsg
            Class cl_StructingMsgItemBuilder = load("com/tencent/mobileqq/activity/aio/item/StructingMsgItemBuilder");
            findAndHookMethod(cl_StructingMsgItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
            for (Method m : cl_StructingMsgItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
            //End: StructMsg
//            for (Method m : load("com.tencent.mobileqq.structmsg.StructMsgForGeneralShare").getMethods()) {
//                if (m.getName().equals("getView")) {
//                    XposedBridge.hookMethod(m, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            View v = (View) param.getResult();
//                            View.OnLongClickListener l = getBubbleLongClickListener((Activity) param.args[0]);
//                            if (v != null && l != null) {
//                                //v.setOnLongClickListener(l);
//                            }
//                        }
//                    });
//                    break;
//                }
//            }
            inited = true;
            return true;
        } catch (Throwable throwable) {
            log(throwable);
            return false;
        }
    }

    public static class GetMenuItemCallBack extends XC_MethodHook {
        public GetMenuItemCallBack() {
            super(60);
        }

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            try {
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                if (!cfg.getBooleanOrFalse(qn_send_card_msg)) return;
            } catch (Exception ignored) {
            }
            Object arr = param.getResult();
            Class<?> clQQCustomMenuItem = arr.getClass().getComponentType();
            Object item_copy = CustomMenu.createItem(clQQCustomMenuItem, R_ID_COPY_CODE, "复制代码");
            Object ret = Array.newInstance(clQQCustomMenuItem, Array.getLength(arr) + 1);
            Array.set(ret, 0, Array.get(arr, 0));
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(arr, 1, ret, 2, Array.getLength(arr) - 1);
            Array.set(ret, 1, item_copy);
            param.setResult(ret);
        }
    }

    public static class MenuItemClickCallback extends XC_MethodHook {
        public MenuItemClickCallback() {
            super(60);
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            int id = (int) param.args[0];
            Activity ctx = (Activity) param.args[1];
            Object chatMessage = param.args[2];
            if (id == R_ID_COPY_CODE) {
                param.setResult(null);
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(chatMessage.getClass())) {
                        clipboardManager.setText((String) invoke_virtual(iget_object_or_null(chatMessage, "structingMsg"), "getXml", new Object[0]));
                        showToast(ctx, TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                    } else if (load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(chatMessage.getClass())) {
                        clipboardManager.setText((String) invoke_virtual(iget_object_or_null(chatMessage, "ark_app_message"), "toAppXml", new Object[0]));
                        showToast(ctx, TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                    }
                } catch (Throwable e) {
                    log(e);
                }
            }
        }
    }

    private Field fChatAdapter = null;
    private Field fBubbleOnLongClickListener = null;

    public View.OnLongClickListener getBubbleLongClickListener(Activity activity) {
        Object fmgr;
        try {
            fmgr = invoke_virtual(activity, "getSupportFragmentManager");
            Object fragment = invoke_virtual(fmgr, "findFragmentByTag", "com.tencent.mobileqq.activity.ChatFragment", String.class);
            Object chatpie = invoke_virtual(fragment, "a", load("com.tencent.mobileqq.activity.BaseChatPie"));
            if (fChatAdapter == null) {
                for (Field f : _BaseChatPie().getDeclaredFields()) {
                    if (f.getName().equals("a") && Modifier.isPublic(f.getModifiers())) {
                        Class type = f.getType();
                        if (BaseAdapter.class.isAssignableFrom(type)) {
                            fChatAdapter = f;
                            fChatAdapter.setAccessible(true);
                        }
                    }
                }
            }
            BaseAdapter chatAdapter1 = (BaseAdapter) fChatAdapter.get(chatpie);
            if (fBubbleOnLongClickListener == null) {
                for (Field f : fChatAdapter.getType().getDeclaredFields()) {
                    if (View.OnLongClickListener.class.isAssignableFrom(f.getType())) {
                        fBubbleOnLongClickListener = f;
                        fBubbleOnLongClickListener.setAccessible(true);
                    }
                }
            }
            return (View.OnLongClickListener) fBubbleOnLongClickListener.get(chatAdapter1);
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER, DexKit.C_FACADE, DexKit.C_TEST_STRUCT_MSG};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_send_card_msg, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_send_card_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
