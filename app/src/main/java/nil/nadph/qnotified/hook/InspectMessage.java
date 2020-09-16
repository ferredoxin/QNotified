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

import android.content.Context;
import android.content.DialogInterface;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.QQMessageFacade;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class InspectMessage extends BaseDelayableHook implements View.OnLongClickListener {
    private static final String qn_inspect_msg = "qn_inspect_msg";
    private static final InspectMessage self = new InspectMessage();
    private boolean inited = false;
    static Field f_panel;
    boolean bInspectMode = false;

    private InspectMessage() {
    }

    public static InspectMessage get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            findAndHookMethod(load("com/tencent/mobileqq/activity/aio/BaseBubbleBuilder"), "onClick", View.class, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!bInspectMode) return;
                    if (!isEnabled()) return;
                    Context ctx = iget_object_or_null(param.thisObject, "a", Context.class);
                    if (ctx == null) ctx = getFirstNSFByType(param.thisObject, Context.class);
                    View view = (View) param.args[0];
                    if (ctx == null || MultiForwardAvatarHook.isLeftCheckBoxVisible()) return;
                    String activityName = ctx.getClass().getName();
                    if (activityName.equals("com.tencent.mobileqq.activity.MultiForwardActivity")) {
                        return;
                    }
                    final Object msg = MultiForwardAvatarHook.getChatMessageByView(view);
                    if (msg == null) return;
                    //判断私聊或群聊  istroop = 0 为私聊 ，1 为群聊
                    //int istroop = (int) iget_object_or_null(msg, "istroop");
                    //取消istroop判断，在群里也可以撤回部分消息
                    CustomDialog dialog = CustomDialog.createFailsafe(ctx);
                    dialog.setTitle(Utils.getShort$Name(msg));
                    dialog.setMessage(msg.toString());
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("确认", null);
                    final Context finalCtx = ctx;
                    dialog.setNegativeButton("撤回", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                QQMessageFacade.revokeMessage(msg);
                            } catch (Throwable e) {
                                Utils.showToast(finalCtx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_LONG);
                            }
                        }
                    });
                    dialog.show();
                    param.setResult(null);
                }
            });
            //begin panel
            Method a = null, b = null, c = null, _emmm_ = null;
            for (Method m : load("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout").getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class) && Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers())
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
            if (m == null) m = _emmm_;
            XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
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
            findAndHookMethod(load("com.tencent.mobileqq.activity.aio.panel.PanelIconLinearLayout"), "setAllEnable", boolean.class, new XC_MethodHook(47) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    boolean z = (boolean) param.args[0];
                    ViewGroup panel = (ViewGroup) param.thisObject;
                    int cnt = panel.getChildCount();
                    if (cnt == 0) return;
                    View v = panel.getChildAt(cnt - 1);
                    v.setEnabled(true);
                    v.setClickable(z);
                    v.setLongClickable(true);
                }
            });
            //end tweak
            inited = true;
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
            Utils.showToastShort(ctx, "已开启检查消息");
        } else {
            Utils.showToastShort(ctx, "已关闭检查消息");
        }
        return true;
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
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_AIO_UTILS), new DexDeobfStep(DexKit.C_MessageCache), new DexDeobfStep(DexKit.C_MSG_REC_FAC)};
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_inspect_msg, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_inspect_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
