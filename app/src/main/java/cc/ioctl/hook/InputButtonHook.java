/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.getFirstNSFByType;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import me.singleneuron.hook.CopyCardMsg;
import me.singleneuron.qn_kernel.decorator.BaseInputButtonDecorator;
import mqq.app.AppRuntime;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.TouchEventToLongClickAdapter;
import nil.nadph.qnotified.ui.widget.InterceptLayout;
import nil.nadph.qnotified.util.CliOper;
import nil.nadph.qnotified.util.CustomMenu;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;
import xyz.nextalone.util.SystemServiceUtils;

@FunctionEntry
public class InputButtonHook extends CommonDelayableHook {

    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    public static final InputButtonHook INSTANCE = new InputButtonHook();

    private InputButtonHook() {
        super("__NOT_USED__", new DexDeobfStep(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER),
            new DexDeobfStep(DexKit.C_FACADE),
            new DexDeobfStep(DexKit.C_TEST_STRUCT_MSG),
            new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__INIT));
    }

    @Override
    public boolean initOnce() {
        try {
            //Begin: send btn
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__INIT),
                new XC_MethodHook(40) {
                    @Override
                    public void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (LicenseStatus.isBlacklisted()) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        try {
                            Object chatPie = param.thisObject;
                            //Class cl_PatchedButton = load("com/tencent/widget/PatchedButton");
                            ViewGroup __aioRootView = null;
                            for (Method m : Initiator._BaseChatPie().getDeclaredMethods()) {
                                if (m.getReturnType() == ViewGroup.class
                                    && m.getParameterTypes().length == 0) {
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
                            int fun_btn = ctx.getResources()
                                .getIdentifier("fun_btn", "id", ctx.getPackageName());
                            View sendBtn = aioRootView.findViewById(fun_btn);
                            final AppRuntime qqApp = getFirstNSFByType(param.thisObject,
                                Initiator._QQAppInterface());
                            final Parcelable session = getFirstNSFByType(param.thisObject,
                                _SessionInfo());
                            if (!sendBtn.getParent().getClass().getName()
                                .equals(InterceptLayout.class.getName())) {
                                InterceptLayout layout = InterceptLayout.setupRudely(sendBtn);
                                layout.setTouchInterceptor(new TouchEventToLongClickAdapter() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        ViewGroup vg = (ViewGroup) v;
                                        if (event.getAction() == MotionEvent.ACTION_DOWN &&
                                            vg.getChildCount() != 0 && vg.getChildAt(0)
                                            .isEnabled()) {
                                            return false;
                                        }
                                        return super.onTouch(v, event);
                                    }

                                    @Override
                                    public boolean onLongClick(View v) {
                                        try {
                                            ViewGroup vg = (ViewGroup) v;
                                            Context ctx = v.getContext();
                                            if (vg.getChildCount() != 0 && !vg.getChildAt(0)
                                                .isEnabled()) {
                                                EditText input = aioRootView.findViewById(
                                                    ctx.getResources().getIdentifier("input", "id",
                                                        ctx.getPackageName()));
                                                String text = input.getText().toString();
                                                if (text.length() == 0) {
                                                    Toasts.error(ctx, "请先输入卡片代码");
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
                            sendBtn.setOnLongClickListener(v -> {
                                Context ctx1 = v.getContext();
                                EditText input = aioRootView.findViewById(ctx1.getResources()
                                    .getIdentifier("input", "id", ctx1.getPackageName()));
                                String text = input.getText().toString();
                                if (((TextView) v).length()
                                    == 0) { //|| !CardMsgHook.INSTANCE.isEnabled()
                                    return false;
                                }
                                for (BaseInputButtonDecorator decorator : decorators) {
                                    if (decorator
                                        .decorate(text, session, input, sendBtn, ctx1, qqApp)) {
                                        return true;
                                    }
                                }
                                return true;
                            });
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                });
            //End: send btn
            return true;
        } catch (Throwable throwable) {
            log(throwable);
            return false;
        }
    }

    private static final BaseInputButtonDecorator[] decorators = {CardMsgHook.INSTANCE,
        ChatTailHook.INSTANCE};

    @Override
    public boolean isEnabled() {
        for (BaseInputButtonDecorator decorator : decorators) {
            if (decorator.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (enabled) {
            init();
        }
    }

    public static class GetMenuItemCallBack extends XC_MethodHook {

        public GetMenuItemCallBack() {
            super(60);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            if (LicenseStatus.sDisableCommonHooks) {
                return;
            }
            if (!CopyCardMsg.INSTANCE.isEnabled()) {
                return;
            }
            if (LicenseStatus.isBlacklisted()) {
                return;
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
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            if (!CopyCardMsg.INSTANCE.isEnabled()) {
                return;
            }
            if (LicenseStatus.isBlacklisted()) {
                return;
            }
            int id = (int) param.args[0];
            Activity ctx = (Activity) param.args[1];
            Object chatMessage = param.args[2];
            if (id == R_ID_COPY_CODE) {
                param.setResult(null);
                try {
                    if (load("com.tencent.mobileqq.data.MessageForStructing")
                        .isAssignableFrom(chatMessage.getClass())) {
                        String text = (String) invoke_virtual(
                            iget_object_or_null(chatMessage, "structingMsg"), "getXml",
                            new Object[0]);
                        SystemServiceUtils.copyToClipboard(ctx, text);
                        Toasts.info(ctx, "复制成功");
                        CliOper.copyCardMsg(text);
                    } else if (load("com.tencent.mobileqq.data.MessageForArkApp")
                        .isAssignableFrom(chatMessage.getClass())) {
                        String text = (String) invoke_virtual(
                            iget_object_or_null(chatMessage, "ark_app_message"), "toAppXml",
                            new Object[0]);
                        SystemServiceUtils.copyToClipboard(ctx, text);
                        Toasts.info(ctx, "复制成功");
                        CliOper.copyCardMsg(text);
                    }
                } catch (Throwable e) {
                    log(e);
                }
            }
        }
    }
}
