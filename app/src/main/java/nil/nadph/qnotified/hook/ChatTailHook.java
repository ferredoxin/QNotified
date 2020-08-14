/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Looper;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tencent.mobileqq.app.QQAppInterface;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.ChatActivityFacade;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.InterceptLayout;
import nil.nadph.qnotified.ui.TouchEventToLongClickAdapter;
import nil.nadph.qnotified.util.*;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class ChatTailHook extends BaseDelayableHook {
    public static final String qn_chat_tail_enable = "qn_chat_tail_enable";
    private static final String ACTION_UPDATE_CHAT_TAIL = "nil.nadph.qnotified.ACTION_UPDATE_CHAT_TAIL";
    private static final ChatTailHook self = new ChatTailHook();
    private boolean inited = false;


    ChatTailHook() {
    }

    public static ChatTailHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            //Begin: send btn
            final Class<?> cl_BaseChatPie = _BaseChatPie();
            Method _BaseChatPie_init = null;
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
                    if (isEnabled()) {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        try {
                            Object chatPie = param.thisObject;
                            //Class cl_PatchedButton = load("com/tencent/widget/PatchedButton");
                            final ViewGroup viewGroup = (ViewGroup) invoke_virtual_any(chatPie, ViewGroup.class);
                            if (viewGroup == null) return;
                            Context ctx = viewGroup.getContext();
                            int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
                            View sendBtn = viewGroup.findViewById(fun_btn);
                            final QQAppInterface qqApp = getFirstNSFByType(param.thisObject, QQAppInterface.class);
                            final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                            sendBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (LicenseStatus.sDisableCommonHooks) return;
                                    Context ctx = v.getContext();
                                    EditText input = viewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));
                                    String text = input.getText().toString();
                                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                                    if (((TextView) v).length() != 0) {
                                        Field field = null;
                                        for (Field f : session.getClass().getDeclaredFields()) {
                                            if (f.getName().equalsIgnoreCase("a") && f.getType() == String.class) {
                                                field = f;
                                            }
                                        }
                                        String uin = "";
                                        try {
                                            uin = (String) field.get(session);
                                            String muted = "," + cfg.getString(ConfigItems.qn_chat_tail_troops + "_" + ExfriendManager.getCurrent().getUin()) + ",";
                                            if (muted.contains("," + uin + ",") || cfg.getBooleanOrFalse(ConfigItems.qn_chat_tail_global + "_" + ExfriendManager.getCurrent().getUin())) {
                                                text = text + ChatTailHook.get().getTailCapacity();
                                            } else {
                                                muted = "," + cfg.getString(ConfigItems.qn_chat_tail_friends + "_" + ExfriendManager.getCurrent().getUin()) + ",";
                                                if (muted.contains("," + uin + ",")) {
                                                    text = text + ChatTailHook.get().getTailCapacity();
                                                }
                                            }
                                        } catch (IllegalAccessException e) {
                                        }
                                        ChatActivityFacade.sendMessage(qqApp, ctx, session, text);
                                        input.setText("");
                                    }
                                }
                            });
                        } catch (Throwable e) {
                            log(e);
                        }
                    }
                }
            });
            //End: send btn
            inited = true;
            return true;
        } catch (Throwable throwable) {
            log(throwable);
            return false;
        }
    }


    public void setTail(String tail) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putString(ConfigItems.qn_chat_tail + "_" + ExfriendManager.getCurrent().getUin(), tail);
            cfg.save();
            Intent intent = new Intent(ACTION_UPDATE_CHAT_TAIL);
            SyncUtils.sendGenericBroadcast(intent);
        } catch (IOException e) {
            log(e);
        }
    }

    public String getTailStatus() {
        return ConfigManager.getDefaultConfig().getStringOrDefault(ConfigItems.qn_chat_tail + "_" + ExfriendManager.getCurrent().getUin(), "");
    }

    public String getTailCapacity() {
        return getTailStatus().replace("\\n", "\n");
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_chat_tail_enable + "_" + ExfriendManager.getCurrent().getUin(), enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_chat_tail_enable + "_" + ExfriendManager.getCurrent().getUin());
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}
