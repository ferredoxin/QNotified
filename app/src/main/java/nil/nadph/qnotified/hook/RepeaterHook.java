/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tencent.mobileqq.app.QQAppInterface;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.bridge.ChatActivityFacade;
import nil.nadph.qnotified.dialog.RepeaterIconSettingDialog;
import nil.nadph.qnotified.ui.LinearLayoutDelegate;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.ReflexUtil.*;
import static nil.nadph.qnotified.util.Utils.*;

public class RepeaterHook extends CommonDelayableHook {
    private static final RepeaterHook self = new RepeaterHook();

    private RepeaterHook() {
        super("bug_repeater");
    }

    public static RepeaterHook get() {
        return self;
    }

    @Override
    @SuppressLint({"WrongConstant", "ResourceType"})
    public boolean initOnce() {
        try {
            Method getView = null;
            Class listener2 = null;
            Class itemHolder = null;
            Class BaseChatItemLayout = null;
            Class ChatMessage = null;
            //begin: pic
            for (Method m : _PicItemBuilder().getDeclaredMethods()) {
                Class[] argt = m.getParameterTypes();
                if (m.getReturnType() == View.class && m.getName().equalsIgnoreCase("a")) {
                    if (argt.length > 4 && argt[2] == View.class) {
                        getView = m;
                        listener2 = argt[4];
                        itemHolder = argt[1];
                        ChatMessage = argt[0];
                        BaseChatItemLayout = argt[3];
                    }
                }
            }
            XposedBridge.hookMethod(getView, new XC_MethodHook(50) {
                @Override
                public void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    ViewGroup relativeLayout = (ViewGroup) param.getResult();
                    Context ctx = relativeLayout.getContext();
                    if (ctx.getClass().getName().contains("ChatHistoryActivity") ||
                            ctx.getClass().getName().contains("MultiForwardActivity"))
                        return;
                    final QQAppInterface app = getFirstNSFByType(param.thisObject, _QQAppInterface());
                    final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                    String uin = "" + Utils.getLongAccountUin();
                    if (relativeLayout.findViewById(101) == null) {
                        View childAt = relativeLayout.getChildAt(0);
                        ViewGroup viewGroup = (ViewGroup) childAt.getParent();
                        viewGroup.removeView(childAt);
                        int __id = childAt.getId();
                        LinearLayout linearLayout = new LinearLayout(ctx);
                        if (__id != -1) linearLayout.setId(__id);
                        linearLayout.setOrientation(0);
                        linearLayout.setGravity(17);
                        ImageView imageView = new ImageView(ctx);
                        imageView.setId(101);
                        imageView.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams.rightMargin = dip2px(ctx, (float) 10);
                        linearLayout.addView(imageView, layoutParams);
                        linearLayout.addView(childAt, childAt.getLayoutParams());
                        ImageView imageView2 = new ImageView(ctx);
                        imageView2.setId(102);
                        imageView2.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams2.leftMargin = dip2px(ctx, (float) 10);
                        linearLayout.addView(imageView2, layoutParams2);
                        viewGroup.addView(linearLayout, -2, -2);
                    }
                    ImageView imageView3 = relativeLayout.findViewById(101);
                    ImageView imageView4 = relativeLayout.findViewById(102);
                    if (iget_object_or_null(param.args[0], "senderuin").equals(uin)) {
                        imageView3.setVisibility(0);
                        imageView4.setVisibility(8);
                    } else {
                        imageView3.setVisibility(8);
                        imageView4.setVisibility(0);
                    }
                    View.OnClickListener r0 = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                ChatActivityFacade.repeatMessage(app, session, param.args[0]);
                            } catch (Throwable e) {
                                log(e);
                                Utils.showToast(HostInformationProviderKt.getHostInformationProvider().getApplicationContext(), TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
                            }
                        }
                    };
                    imageView3.setOnClickListener(r0);
                    imageView4.setOnClickListener(r0);
                }
            });
            //end: pic
            //begin: text
            if (HostInformationProviderKt.getHostInformationProvider().isTim()) {
                // TODO: 2020/5/17 Add MsgForText +1 for TIM
                XposedHelpers.findAndHookMethod(_TextItemBuilder(), "a", ChatMessage, itemHolder, View.class, BaseChatItemLayout, listener2,
                        new XC_MethodHook(51) {
                            @Override
                            public void afterHookedMethod(final MethodHookParam param) throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) return;
                                if (!isEnabled()) return;
                                View view;
                                View resultView = (View) param.getResult();
                                Context ctx = resultView.getContext();
                                if (ctx.getClass().getName().contains("ChatHistoryActivity")
                                        || ctx.getClass().getName().contains("MultiForwardActivity"))
                                    return;
                                final QQAppInterface app = getFirstNSFByType(param.thisObject, QQAppInterface.class);
                                final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                                String uin = "" + Utils.getLongAccountUin();
                                if (resultView.findViewById(101) == null) {
                                    LinearLayoutDelegate linearLayout = new LinearLayoutDelegate(ctx);
                                    linearLayout.setOrientation(0);
                                    linearLayout.setGravity(17);
                                    ImageView imageView = new ImageView(ctx);
                                    imageView.setId(101);
                                    imageView.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                                    layoutParams.rightMargin = dip2px(ctx, (float) 5);
                                    linearLayout.addView(imageView, layoutParams);
                                    ViewGroup p = (ViewGroup) resultView.getParent();
                                    if (p != null) p.removeView(resultView);
                                    ViewGroup.LayoutParams currlp = resultView.getLayoutParams();
                                    linearLayout.addView(resultView, -2, -2);
                                    linearLayout.setDelegate(resultView);
                                    ImageView imageView2 = new ImageView(ctx);
                                    imageView2.setId(102);
                                    imageView2.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                                    layoutParams2.leftMargin = dip2px(ctx, (float) 5);
                                    linearLayout.addView(imageView2, layoutParams2);
                                    linearLayout.setPadding(0, 0, 0, 0);
                                    param.setResult(linearLayout);
                                    view = linearLayout;
                                } else {
                                    view = resultView.findViewById(101);
                                }
                                ImageView imageView3 = view.findViewById(101);
                                @SuppressLint("ResourceType") ImageView imageView4 = view.findViewById(102);
                                if (iget_object_or_null(param.args[0], "senderuin").equals(uin)) {
                                    imageView3.setVisibility(0);
                                    imageView4.setVisibility(8);
                                } else {
                                    imageView3.setVisibility(8);
                                    imageView4.setVisibility(0);
                                }
                                View.OnClickListener r0 = new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            ChatActivityFacade.repeatMessage(app, session, param.args[0]);
                                        } catch (Throwable e) {
                                            log(e);
                                            Utils.showToast(HostInformationProviderKt.getHostInformationProvider().getApplicationContext(), TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
                                        }
                                    }
                                };
                                imageView3.setOnClickListener(r0);
                                imageView4.setOnClickListener(r0);
                            }
                        });
            } else {
                XposedHelpers.findAndHookMethod(_TextItemBuilder(), "a", ChatMessage, itemHolder, View.class, BaseChatItemLayout, listener2, new XC_MethodHook() {
                    @Override
                    public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        if (!isEnabled()) return;
                        View v = (View) methodHookParam.args[2];
                        if (v != null && (v.getContext().getClass().getName().contains("ChatHistoryActivity")
                                || v.getContext().getClass().getName().contains("MultiForwardActivity")))
                            return;
                        iput_object(methodHookParam.args[0], "isFlowMessage", true);
                        if (((int) iget_object_or_null(methodHookParam.args[0], "extraflag")) == 32768) {
                            iput_object(methodHookParam.args[0], "extraflag", 0);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        if (!isEnabled()) return;
                        ImageView imageView = iget_object_or_null(param.args[1], "b", ImageView.class);
                        ImageView imageView2 = iget_object_or_null(param.args[1], "c", ImageView.class);
                        ((Boolean) invoke_virtual(param.args[0], "isSend", boolean.class) ? imageView : imageView2).setVisibility(0);
                        Bitmap repeat = RepeaterIconSettingDialog.getRepeaterIcon();
                        imageView.setImageBitmap(repeat);
                        imageView2.setImageBitmap(repeat);
                        final QQAppInterface app = getFirstNSFByType(param.thisObject, QQAppInterface.class);
                        final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                        final Object msg = param.args[0];
                        View.OnClickListener r0 = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    ChatActivityFacade.repeatMessage(app, session, msg);
                                } catch (Throwable e) {
                                    log(e);
                                    Utils.showToast(HostInformationProviderKt.getHostInformationProvider().getApplicationContext(), TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
                                }
                            }
                        };
                        imageView.setOnClickListener(r0);
                        imageView2.setOnClickListener(r0);
                    }
                });
            }
            //end: text
            //begin: ptt
            XposedHelpers.findAndHookMethod(_PttItemBuilder(), "a", ChatMessage, itemHolder, View.class, BaseChatItemLayout, listener2,
                    new XC_MethodHook(51) {
                        @Override
                        public void afterHookedMethod(final MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            if (!isEnabled()) return;
                            ViewGroup convertView = (ViewGroup) param.getResult();
                            Context ctx = convertView.getContext();
                            if (ctx.getClass().getName().contains("ChatHistoryActivity")
                                    || ctx.getClass().getName().contains("MultiForwardActivity"))
                                return;
                            final QQAppInterface app = getFirstNSFByType(param.thisObject, QQAppInterface.class);
                            final Parcelable session = getFirstNSFByType(param.thisObject, _SessionInfo());
                            String uin = "" + Utils.getLongAccountUin();
                            if (convertView.findViewById(101) == null) {
                                LinearLayoutDelegate wrapperLayout = new LinearLayoutDelegate(ctx);
                                wrapperLayout.setDelegate(convertView);
                                //wrapperLayout.setId(Integer.parseInt((String) Hook.config.get("PttItem_id"), 16));
                                wrapperLayout.setOrientation(0);
                                wrapperLayout.setGravity(17);
                                ImageView leftIcon = new ImageView(ctx);
                                leftIcon.setId(101);
                                leftIcon.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                                layoutParams.rightMargin = dip2px(ctx, (float) 5);
                                wrapperLayout.addView(leftIcon, layoutParams);
                                wrapperLayout.addView(convertView, -2, -2);
                                ImageView rightIcon = new ImageView(ctx);
                                rightIcon.setId(102);
                                rightIcon.setImageBitmap(RepeaterIconSettingDialog.getRepeaterIcon());
                                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                                layoutParams2.leftMargin = dip2px(ctx, (float) 5);
                                wrapperLayout.addView(rightIcon, layoutParams2);
                                param.setResult(wrapperLayout);
                                convertView = wrapperLayout;
                            }
                            ImageView leftIcon = convertView.findViewById(101);
                            ImageView rightIcon = convertView.findViewById(102);
                            if (iget_object_or_null(param.args[0], "senderuin").equals(uin)) {
                                leftIcon.setVisibility(0);
                                rightIcon.setVisibility(8);
                            } else {
                                leftIcon.setVisibility(8);
                                rightIcon.setVisibility(0);
                            }
                            View.OnClickListener l = new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        ChatActivityFacade.repeatMessage(app, session, param.args[0]);
                                    } catch (Throwable e) {
                                        log(e);
                                        Utils.showToast(HostInformationProviderKt.getHostInformationProvider().getApplicationContext(), TOAST_TYPE_ERROR, e.toString(), Toast.LENGTH_SHORT);
                                    }
                                }
                            };
                            leftIcon.setOnClickListener(l);
                            rightIcon.setOnClickListener(l);
                        }
                    });
            //end: ptt
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
