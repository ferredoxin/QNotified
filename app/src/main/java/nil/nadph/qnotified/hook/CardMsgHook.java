package nil.nadph.qnotified.hook;


import android.app.Activity;
import android.app.Application;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;


public class CardMsgHook extends BaseDelayableHook {
    private CardMsgHook() {
    }

    public static final int R_ID_COPY_CODE = 0x00EE77CC;

    private static final CardMsgHook self = new CardMsgHook();

    public static CardMsgHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            String _____ = "e";
            try {
                Application ctx = Utils.getApplication();
                if (getHostInfo(ctx).versionName.indexOf(0) == '7') {
                    _____ = "d";
                }
            } catch (Throwable e) {
                //Should not happen
                log(e);
            }
            final Class cl_BaseChatPie = load("com.tencent.mobileqq.activity.BaseChatPie");
            for (Method method : cl_BaseChatPie.getDeclaredMethods()) {
                if (method.getParameterTypes().length != 0
                        || !method.getReturnType().equals(void.class)) continue;
                if (method.getName().equals(_____)) {
                    XposedBridge.hookMethod(method, new XC_MethodHook(40) {
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
                                final Object qqApp = iget_object_or_null(param.thisObject, "a", load("com.tencent.mobileqq.app.QQAppInterface"));
                                final Object session = iget_object_or_null(param.thisObject, "a", _SessionInfo());
                                sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        Context ctx = v.getContext();
                                        EditText input = (EditText) viewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
                                        String text = input.getText().toString();
                                        try {
                                            Object arkMsg = load("com.tencent.mobileqq.data.ArkAppMessage").newInstance();
                                            if ((boolean) invoke_virtual(arkMsg, "fromAppXml", text, String.class)) {
                                                invoke_static(DexKit.doFindClass(DexKit.C_FACADE), "a", qqApp, session, arkMsg, load("com.tencent.mobileqq.app.QQAppInterface"), _SessionInfo(), arkMsg.getClass());
                                                input.setText("");
                                                return true;
                                            }
                                        } catch (Throwable e) {
                                            try {
                                                Utils.showToast(ctx, TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
                                            } catch (Throwable ignored) {
                                            }
                                            log(e);
                                        }

//                                Object callStaticMethod = MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("TestStructMsg")), this.val$loader.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), "a", editable);
//                                if (callStaticMethod != null) {
//                                    MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("MessageManager")), "a", this.val$qqAppInterface, this.val$session, callStaticMethod);
//                                }

                                        return true;
                                    }
                                });
                            } catch (Throwable e) {
                                log(e);
                            }
                        }
                    });
                    break;
                }
            }

            Class cl_ArkAppItemBuilder = DexKit.doFindClass(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER);
            findAndHookMethod(cl_ArkAppItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new XC_MethodHook(60) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
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
            });
            for (Method m : cl_ArkAppItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new XC_MethodHook(60) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                ConfigManager cfg = ConfigManager.getDefault();
                                if (!cfg.getBooleanOrFalse(qn_send_card_msg)) return;
                            } catch (Exception ignored) {
                            }
                            Object arr = param.getResult();
                            Object QQCustomMenuItem = Array.get(arr, 0).getClass().newInstance();
                            iput_object(QQCustomMenuItem, "a", int.class, R_ID_COPY_CODE);
                            iput_object(QQCustomMenuItem, "a", String.class, "复制代码");
                            Object ret = Array.newInstance(QQCustomMenuItem.getClass(), Array.getLength(arr) + 1);
                            Array.set(ret, 0, Array.get(arr, 0));
                            System.arraycopy(arr, 1, ret, 2, Array.getLength(arr) - 1);
                            Array.set(ret, 1, QQCustomMenuItem);
                            param.setResult(ret);
                        }
                    });
                    break;
                }
            }
            inited = true;
            return true;
        } catch (Throwable throwable) {
            log(throwable);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER,DexKit.C_FACADE};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qn_send_card_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
