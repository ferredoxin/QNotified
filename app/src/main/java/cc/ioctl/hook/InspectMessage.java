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
import static nil.nadph.qnotified.util.Utils.log;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class InspectMessage extends CommonDelayableHook implements View.OnLongClickListener {

    public static final InspectMessage INSTANCE = new InspectMessage();
    static Field f_panel;
    boolean bInspectMode = false;

    private InspectMessage() {
        super("qn_inspect_msg", new DexDeobfStep(DexKit.C_AIO_UTILS));
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
                        if (!bInspectMode) {
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
                        if (ctx == null || MultiForwardAvatarHook.isLeftCheckBoxVisible()) {
                            return;
                        }
                        String activityName = ctx.getClass().getName();
                        if (activityName
                            .equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                            return;
                        }
                        final Object msg = MultiForwardAvatarHook.getChatMessageByView(view);
                        if (msg == null) {
                            return;
                        }
                        //取消istroop判断，在群里也可以撤回部分消息
                        CustomDialog dialog = CustomDialog.createFailsafe(ctx);
                        dialog.setTitle(Utils.getShort$Name(msg));
                        dialog.setMessage(msg.toString());
                        dialog.setCancelable(true);
                        dialog.setPositiveButton("确认", null);
                        dialog.show();
                        param.setResult(null);
                    }
                });
            //begin panel
            Method a = null, b = null, c = null, _emmm_ = null;
            for (Method m : load("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout")
                .getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class) && Modifier.isPublic(m.getModifiers())
                    && !Modifier.isStatic(m.getModifiers())
                    && m.getParameterTypes().length == 0) {
                    String name = m.getName();
                    if ("a".equals(name)) {
                        a = m;
                    } else if ("b".equals(name)) {
                        b = m;
                    } else if ("c".equals(name)) {
                        c = m;
                    } else if (m.getName().length() < 4) {
                        _emmm_ = m;
                    }
                }
            }
            Method m = c == null ? a : b;
            if (m == null) {
                m = _emmm_;
            }
            XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    ViewGroup panel = (ViewGroup) param.thisObject;
                    View v = panel.getChildAt(panel.getChildCount() - 1);
                    if (v instanceof ViewGroup) {
                        View v2;
                        for (int i = 0; i < ((ViewGroup) v).getChildCount(); i++) {
                            v2 = ((ViewGroup) v).getChildAt(i);
                            if (!(v2 instanceof ViewGroup)) {
                                v2.setOnLongClickListener(InspectMessage.this);
                            }
                        }
                    } else {
                        v.setOnLongClickListener(InspectMessage.this);
                    }
                }
            });
            //end panel
            //begin tweak
            findAndHookMethod(load("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout"),
                "setAllEnable", boolean.class, new XC_MethodHook(47) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        boolean z = (boolean) param.args[0];
                        ViewGroup panel = (ViewGroup) param.thisObject;
                        int cnt = panel.getChildCount();
                        if (cnt == 0) {
                            return;
                        }
                        View v = panel.getChildAt(cnt - 1);
                        v.setEnabled(true);
                        v.setClickable(z);
                        v.setLongClickable(true);
                    }
                });
            //end tweak
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        bInspectMode = !bInspectMode;
        Context ctx = v.getContext();
        if (bInspectMode) {
            Toasts.info(ctx, "已开启检查消息");
        } else {
            Toasts.info(ctx, "已关闭检查消息");
        }
        return true;
    }
}
