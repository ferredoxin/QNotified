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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.mobileqq.app.QQAppInterface;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.hook.CopyCardMsg;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.activity.ChatTailActivity;
import nil.nadph.qnotified.dialog.RikkaCustomMsgTimeFormatDialog;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.InterceptLayout;
import nil.nadph.qnotified.ui.TouchEventToLongClickAdapter;
import nil.nadph.qnotified.util.*;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;


public class InputButtonHook extends BaseDelayableHook {
    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    private static final InputButtonHook self = new InputButtonHook();
    private boolean inited = false;

    private InputButtonHook() {
    }

    public static InputButtonHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            //Begin: send btn
//            for (Method method : cl_BaseChatPie.getDeclaredMethods()) {
//                if (method.getParameterTypes().length != 0
//                        || !method.getReturnType().equals(void.class)) continue;
//                if (method.getName().equals(_BaseChatPie_init_name)) {
//                    _BaseChatPie_init = method;
//                    break;
//                }
//            }
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__INIT), new XC_MethodHook(40) {
                @Override
                public void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    try {
                        Object chatPie = param.thisObject;
                        //Class cl_PatchedButton = load("com/tencent/widget/PatchedButton");
                        ViewGroup __aioRootView = null;
                        for (Method m : Initiator._BaseChatPie().getDeclaredMethods()) {
                            if (m.getReturnType() == ViewGroup.class && m.getParameterTypes().length == 0) {
                                __aioRootView = (ViewGroup) m.invoke(chatPie);
                                break;
                            }
                        }
                        if (__aioRootView == null) {
                            Utils.logw("AIO root view not found");
                            return;
                        }
                        ViewGroup aioRootView = __aioRootView;
                        Context ctx = aioRootView.getContext();
                        int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
                        View sendBtn = aioRootView.findViewById(fun_btn);
                        final QQAppInterface qqApp = getFirstNSFByType(param.thisObject, QQAppInterface.class);
                        final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                        if (!sendBtn.getParent().getClass().getName().equals(InterceptLayout.class.getName())) {
                            InterceptLayout layout = InterceptLayout.setupRudely(sendBtn);
                            layout.setTouchInterceptor(new TouchEventToLongClickAdapter() {
                                @Override
                                public boolean onTouch(View v, MotionEvent event) {
                                    if (LicenseStatus.hasBlackFlags() || !CardMsgHook.get().isEnabled())
                                        return false;
                                    ViewGroup vg = (ViewGroup) v;
                                    if (event.getAction() == MotionEvent.ACTION_DOWN &&
                                            vg.getChildCount() != 0 && vg.getChildAt(0).isEnabled()) {
                                        return false;
                                    }
                                    return super.onTouch(v, event);
                                }

                                @Override
                                public boolean onLongClick(View v) {
                                    try {
                                        if (LicenseStatus.sDisableCommonHooks) return false;
                                        if (LicenseStatus.hasBlackFlags() || !CardMsgHook.get().isEnabled())
                                            return false;
                                        ViewGroup vg = (ViewGroup) v;
                                        Context ctx = v.getContext();
                                        if (vg.getChildCount() != 0 && !vg.getChildAt(0).isEnabled()) {
                                            EditText input = aioRootView.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
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
                                if (LicenseStatus.sDisableCommonHooks) return false;
                                Context ctx = v.getContext();
                                EditText input = aioRootView.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
                                String text = input.getText().toString();
                                if (((TextView) v).length() == 0 || LicenseStatus.hasBlackFlags() || !CardMsgHook.get().isEnabled()) {
                                    return false;
                                } else if (text.contains("<?xml") || text.contains("{\"")) {
                                    new Thread(() -> {
                                        if (text.contains("<?xml")) {
                                            try {
                                                if (CardMsgHook.ntSendCardMsg(qqApp, session, text)) {
                                                    Utils.runOnUiThread(() -> input.setText(""));
                                                    CliOper.sendCardMsg(Utils.getLongAccountUin(), text);
                                                } else {
                                                    Utils.showToast(ctx, TOAST_TYPE_ERROR, "XML语法错误(代码有误)", Toast.LENGTH_SHORT);
                                                }
                                            } catch (Throwable e) {
                                                if (e instanceof InvocationTargetException) {
                                                    e = e.getCause();
                                                }
                                                log(e);
                                                Utils.showToast(ctx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_SHORT);
                                            }
                                        } else if (text.contains("{\"")) {
                                            try {
                                                // Object arkMsg = load("com.tencent.mobileqq.data.ArkAppMessage").newInstance();
                                                if (CardMsgHook.ntSendCardMsg(qqApp, session, text)) {
                                                    Utils.runOnUiThread(() -> input.setText(""));
                                                    CliOper.sendCardMsg(Utils.getLongAccountUin(), text);
                                                } else {
                                                    Utils.showToast(ctx, TOAST_TYPE_ERROR, "JSON语法错误(代码有误)", Toast.LENGTH_SHORT);
                                                }
                                            } catch (Throwable e) {
                                                if (e instanceof InvocationTargetException) {
                                                    e = e.getCause();
                                                }
                                                log(e);
                                                Utils.showToast(ctx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_SHORT);
                                            }
                                        }
                                    }).start();
                                } else {
                                    if (LicenseStatus.hasBlackFlags()) return false;
                                    if (!ChatTailHook.get().isEnabled()) return false;
                                    if (!Utils.isNullOrEmpty(ChatTailHook.get().getTailCapacity())) {
                                        int battery = FakeBatteryHook.get().isEnabled() ? FakeBatteryHook.get().getFakeBatteryStatus() < 1 ? ChatTailActivity.getBattery() : FakeBatteryHook.get().getFakeBatteryCapacity() : ChatTailActivity.getBattery();
                                        String tc = ChatTailHook.get().getTailCapacity().
                                                replace(ChatTailActivity.delimiter, input.getText())
                                                .replace("#model#", Build.MODEL)
                                                .replace("#brand#", Build.BRAND)
                                                .replace("#battery#", battery + "")
                                                .replace("#power#", ChatTailActivity.getPower())
                                                .replace("#time#", new SimpleDateFormat(RikkaCustomMsgTimeFormatDialog.getTimeFormat()).format(new Date()));
                                        input.setText(tc);
                                        sendBtn.callOnClick();
                                    } else {
                                        Utils.showToast(ctx, TOAST_TYPE_ERROR, "你还没有设置小尾巴", Toast.LENGTH_SHORT);
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
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (LicenseStatus.sDisableCommonHooks) return;
            if (LicenseStatus.hasBlackFlags() || !CopyCardMsg.INSTANCE.isEnabled()) return;
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
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (LicenseStatus.hasBlackFlags() || !CopyCardMsg.INSTANCE.isEnabled()) return;
            int id = (int) param.args[0];
            Activity ctx = (Activity) param.args[1];
            Object chatMessage = param.args[2];
            if (id == R_ID_COPY_CODE) {
                param.setResult(null);
                try {
                    ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (load("com.tencent.mobileqq.data.MessageForStructing").isAssignableFrom(chatMessage.getClass())) {
                        String text = (String) invoke_virtual(iget_object_or_null(chatMessage, "structingMsg"), "getXml", new Object[0]);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
                        showToast(ctx, TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                        CliOper.copyCardMsg(text);
                    } else if (load("com.tencent.mobileqq.data.MessageForArkApp").isAssignableFrom(chatMessage.getClass())) {
                        String text = (String) invoke_virtual(iget_object_or_null(chatMessage, "ark_app_message"), "toAppXml", new Object[0]);
                        clipboardManager.setPrimaryClip(ClipData.newPlainText(null, text));
                        showToast(ctx, TOAST_TYPE_INFO, "复制成功", Toast.LENGTH_SHORT);
                        CliOper.copyCardMsg(text);
                    }
                } catch (Throwable e) {
                    log(e);
                }
            }
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER), new DexDeobfStep(DexKit.C_FACADE),
                new DexDeobfStep(DexKit.C_TEST_STRUCT_MSG), new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__INIT)};
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
