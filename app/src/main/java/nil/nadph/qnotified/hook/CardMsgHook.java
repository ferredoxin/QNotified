package nil.nadph.qnotified.hook;


import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
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

        Method[] methods = load("com.tencent.mobileqq.activity.BaseChatPie").getMethods();
        for (Method method : methods) {
            if (method.getName().equals("e") && ((method.getParameterTypes().length == 0) && method.getReturnType().equals(void.class))) {
                XposedBridge.hookMethod(method, new XC_MethodHook() {
                    @Override
                    public void afterHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        Object field = FieldUtils.getField(methodHookParam.thisObject, "a", load("com.tencent.mobileqq.app.QQAppInterface"));
                        Object field2 = FieldUtils.getField(methodHookParam.thisObject, "a", load("com.tencent.mobileqq.activity.aio.SessionInfo"));
                        ViewGroup viewGroup = (ViewGroup) FieldUtils.getField(methodHookParam.thisObject, "d", Class.forName("android.view.ViewGroup"));
                        if (viewGroup != null) {
                            ((Button) viewGroup.findViewById(Hook.getResId(viewGroup.getContext(), "id", "fun_btn"))).setOnLongClickListener(new View.OnLongClickListener(this, (EditText) viewGroup.findViewById(Hook.getResId(viewGroup.getContext(), "id", "input")), this.val$loader, field, field2) {
                                @Override
                                public boolean onLongClick(View view) {
                                    try {
                                        String editable = this.val$edit.getText().toString();
                                        Object callStaticMethod = MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("TestStructMsg")), this.val$loader.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), "a", editable);
                                        if (callStaticMethod != null) {
                                            MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("MessageManager")), "a", this.val$qqAppInterface, this.val$session, callStaticMethod);
                                        }
                                    } catch (Throwable th) {
                                        XposedBridge.log(th);
                                    }
                                    try {
                                        String editable2 = this.val$edit.getText().toString();
                                        Object callConstructor = ConstructorUtils.callConstructor(this.val$loader.loadClass("com.tencent.mobileqq.data.ArkAppMessage"), new Object[0]);
                                        if (((Boolean) MethodUtils.callMethod(callConstructor, "fromAppXml", editable2)).booleanValue()) {
                                            MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("MessageManager")), "a", this.val$qqAppInterface, this.val$session, callConstructor);
                                        }
                                    } catch (Throwable th2) {
                                        XposedBridge.log(th2);
                                    }
                                    this.val$edit.setText("");
                                    return false;
                                }
                            });
                        }

                    }
                });
                break;
            }
        }


        Class cl_ArkAppItemBuilder = DexKit.doFindClass(DexKit.C_ARK_APP_ITEM_BUILDER);
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
            if (ps.length == 1 && ps[0].equals(View.class))
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
        }
        inited = true;
        return true;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ARK_APP_ITEM_BUILDER};
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
