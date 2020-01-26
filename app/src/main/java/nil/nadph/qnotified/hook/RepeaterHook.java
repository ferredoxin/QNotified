package nil.nadph.qnotified.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class RepeaterHook extends BaseDelayableHook {
    private static final RepeaterHook self = new RepeaterHook();
    private boolean inited = false;

    private RepeaterHook() {
    }

    public static RepeaterHook get() {
        return self;
    }

    @Override
    @SuppressLint({"WrongConstant", "ResourceType"})
    public boolean init() {
        if (inited) return true;
        try {
            Method getView = null;
            Class listener2 = null;
            Class itemHolder = null;
            Class BaseChatItemLayout = null;
            Class ChatMessage = null;
            for (Method m : _PicItemBuilder().getDeclaredMethods()) {
                if (!m.getReturnType().equals(View.class)) continue;
                if (!m.getName().equals("a")) continue;
                Class[] argt = m.getParameterTypes();
                if (argt.length != 5) continue;
                if (!argt[2].equals(View.class)) continue;
                if (argt[4].getInterfaces().length != 2) continue;
                getView = m;
                listener2 = argt[4];
                itemHolder = argt[1];
                ChatMessage = argt[0];
                BaseChatItemLayout = argt[3];
            }
            XposedBridge.hookMethod(getView, new XC_MethodHook(50) {
                @Override
                public void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    ViewGroup relativeLayout = (ViewGroup) param.getResult();
                    final Object app = iget_object_or_null(param.thisObject, "a", load("com.tencent.mobileqq.app.QQAppInterface"));
                    final Object session = iget_object_or_null(param.thisObject, "a", _SessionInfo());
                    String uin = "" + Utils.getLongAccountUin();
                    Context ctx = relativeLayout.getContext();
                    if (relativeLayout.findViewById(101) == null) {
                        View childAt = relativeLayout.getChildAt(0);
                        ViewGroup viewGroup = (ViewGroup) childAt.getParent();
                        viewGroup.removeView(childAt);
                        int __id = childAt.getId();
                        LinearLayout linearLayout = new LinearLayout(ctx);
                        //linearLayout.setId(Integer.parseInt((String) Hook.config.get("Item_id"), 16));
                        if (__id != -1) linearLayout.setId(__id);
                        linearLayout.setOrientation(0);
                        linearLayout.setGravity(17);
                        ImageView imageView = new ImageView(ctx);
                        imageView.setId(101);
                        imageView.setImageDrawable(ResUtils.loadDrawableFromAsset("repeat.png", ctx));
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams.rightMargin = dip2px(ctx, (float) 10);
                        linearLayout.addView(imageView, layoutParams);
                        linearLayout.addView(childAt, childAt.getLayoutParams());
                        ImageView imageView2 = new ImageView(ctx);
                        imageView2.setId(102);
                        //imageView2.setImageResource(Integer.parseInt((String) Hook.config.get("+1_icon"), 16));
                        imageView2.setImageDrawable(ResUtils.loadDrawableFromAsset("repeat.png", ctx));
                        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                        layoutParams2.leftMargin = dip2px(ctx, (float) 10);
                        linearLayout.addView(imageView2, layoutParams2);
                        viewGroup.addView(linearLayout, -2, -2);
                    }
                    ImageView imageView3 = (ImageView) relativeLayout.findViewById(101);
                    ImageView imageView4 = (ImageView) relativeLayout.findViewById(102);
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
                                Class[] argt = null;
                                Method m = null;
                                for (Method mi : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
                                    if (!mi.getName().equals("a")) continue;
                                    argt = mi.getParameterTypes();
                                    if (argt.length < 3) continue;
                                    if (argt[0].equals(load("com/tencent/mobileqq/app/QQAppInterface")) && argt[1].equals(_SessionInfo())
                                            && argt[2].isAssignableFrom(param.args[0].getClass())) {
                                        m = mi;
                                        break;
                                    }
                                }
                                if (argt.length == 3) m.invoke(null, app, session, param.args[0]);
                                else m.invoke(null, app, session, param.args[0], 0);
                            } catch (Throwable e) {
                                log(e);
                            }
                        }
                    };
                    imageView3.setOnClickListener(r0);
                    imageView4.setOnClickListener(r0);
//                    View.OnLongClickListener r4 = new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View view) {
//                            try {
//                                Class loadClass = this.val$loader.loadClass((String) Hook.config.get("DialogUtils"));
//                                String str = "a";
//                                Class[] clsArr = new Class[2];
//                                clsArr[0] = Class.forName("android.app.Activity");
//                                clsArr[1] = Integer.TYPE;
//                                Dialog dialog = (Dialog) MethodUtils.callStaticMethod(loadClass, str, (Class<?>[]) clsArr, this.val$context, new Integer(2131501443));
//                                ((View) ((View) FieldUtils.getField(dialog, "lBtn")).getParent()).setVisibility(8);
//                                String str2 = "setItems";
//                                Class[] clsArr2 = new Class[2];
//                                clsArr2[0] = Class.forName("[Ljava.lang.String;");
//                                clsArr2[1] = Class.forName("android.content.DialogInterface$OnClickListener");
//                                MethodUtils.callMethod((Object) dialog, str2, (Class<?>[]) clsArr2, new String[]{"秀图", "幻影", "抖动", "生日", "爱你", "征友"}, new DialogInterface.OnClickListener(this, this.val$param, this.val$qqAppInterface, this.val$loader) {
//                                    @Override
//                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                        try {
//                                            Object obj = this.val$param.args[0];
//                                            String str = (String) MethodUtils.callMethod(this.val$qqAppInterface, "getCurrentAccountUin", new Object[0]);
//                                            String str2 = (String) FieldUtils.getField(obj, "frienduin", Class.forName("java.lang.String"));
//                                            String str3 = (String) FieldUtils.getField(obj, "senderuin", Class.forName("java.lang.String"));
//                                            int intValue = ((Integer) FieldUtils.getField(obj, "istroop", Integer.TYPE)).intValue();
//                                            long longValue = ((Long) FieldUtils.getField(obj, "time", Long.TYPE)).longValue();
//                                            Object callConstructor = ConstructorUtils.callConstructor(this.val$loader.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"), new Object[0]);
//                                            FieldUtils.setField(callConstructor, "a", str2, Class.forName("java.lang.String"));
//                                            FieldUtils.setField(callConstructor, "a", new Integer(intValue), Integer.TYPE);
//                                            String str4 = "";
//                                            if (intValue == 1) {
//                                                String str5 = "@%s ";
//                                                Object[] objArr = new Object[1];
//                                                Class loadClass = this.val$loader.loadClass((String) Hook.config.get("NickManager"));
//                                                String str6 = "a";
//                                                Object[] objArr2 = new Object[5];
//                                                objArr2[0] = this.val$qqAppInterface;
//                                                objArr2[1] = str3;
//                                                objArr2[2] = str2;
//                                                Integer num = new Integer(intValue == 1 ? 1 : 2);
//                                                objArr2[3] = num;
//                                                Integer num2 = new Integer(0);
//                                                objArr2[4] = num2;
//                                                objArr[0] = (String) MethodUtils.callStaticMethod(loadClass, str6, objArr2);
//                                                String.format(str5, objArr);
//                                            }
//                                            byte[] bArr = (byte[]) FieldUtils.getField(obj, "msgData");
//                                            Object callConstructor2 = ConstructorUtils.callConstructor(this.val$loader.loadClass("localpb.richMsg.RichMsg$PicRec"), new Object[0]);
//                                            MethodUtils.callMethod(callConstructor2, "mergeFrom", bArr);
//                                            Object callConstructor3 = ConstructorUtils.callConstructor(this.val$loader.loadClass("tencent.im.msg.hummer.resv3.CustomFaceExtPb$ResvAttr"), new Object[0]);
//                                            Object field = FieldUtils.getField(callConstructor3, "msg_image_show");
//                                            Object field2 = FieldUtils.getField(field, "int32_effect_id");
//                                            String str7 = "set";
//                                            Integer num3 = new Integer(40000 + i);
//                                            MethodUtils.callMethod(field2, str7, num3);
//                                            String str8 = "setHasFlag";
//                                            Boolean bool = new Boolean(true);
//                                            MethodUtils.callMethod(field, str8, bool);
//                                            MethodUtils.callMethod(FieldUtils.getField(callConstructor2, "bytes_pb_reserved"), "set", ConstructorUtils.callConstructor(this.val$loader.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"), (byte[]) MethodUtils.callMethod(callConstructor3, "toByteArray", new Object[0])));
//                                            byte[] bArr2 = (byte[]) MethodUtils.callMethod(callConstructor2, "toByteArray", new Object[0]);
//                                            Object callConstructor4 = ConstructorUtils.callConstructor(this.val$loader.loadClass("com.tencent.mobileqq.data.MessageForTroopEffectPic"), new Object[0]);
//                                            String str9 = "init";
//                                            Object[] objArr3 = new Object[8];
//                                            objArr3[0] = str;
//                                            if (intValue == 0) {
//                                                str2 = str3;
//                                            }
//                                            objArr3[1] = str2;
//                                            objArr3[2] = str3;
//                                            objArr3[3] = "QQ复读机";
//                                            objArr3[4] = new Long(longValue);
//                                            objArr3[5] = new Integer(-5015);
//                                            objArr3[6] = new Integer(intValue);
//                                            objArr3[7] = new Long(longValue);
//                                            MethodUtils.callMethod(callConstructor4, str9, objArr3);
//                                            FieldUtils.setField(callConstructor4, "msgUid", new Long(((Long) FieldUtils.getField(obj, "msgUid")).longValue() + ((long) new Random().nextInt())));
//                                            FieldUtils.setField(callConstructor4, "shmsgseq", new Long(((Long) FieldUtils.getField(obj, "shmsgseq")).longValue()));
//                                            FieldUtils.setField(callConstructor4, "isread", new Boolean(true));
//                                            FieldUtils.setField(callConstructor4, "msgData", bArr2);
//                                            MethodUtils.callMethod(callConstructor4, "doParse", new Object[0]);
//                                            FieldUtils.setField(callConstructor4, "msgtype", new Integer(-5015));
//                                            MethodUtils.callStaticMethod(this.val$loader.loadClass((String) Hook.config.get("MessageManager")), "a", this.val$qqAppInterface, callConstructor, callConstructor4);
//                                        } catch (Throwable th) {
//                                            log(th);
//                                        }
//                                    }
//                                });
//                                dialog.create();
//                                dialog.show();
//                            } catch (Throwable th) {
//                                XposedBridge.log(th);
//                            }
//                            return true;
//                        }
//                    };
//                    imageView3.setOnLongClickListener(r4);
//                    imageView4.setOnLongClickListener(r4);
                }
            });

            XposedHelpers.findAndHookMethod(_TextItemBuilder(), "a", ChatMessage, itemHolder, View.class, BaseChatItemLayout, listener2, new XC_MethodHook() {
                @Override
                public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                    if (!isEnabled()) return;
                    iput_object(methodHookParam.args[0], "isFlowMessage", true);
                    if (((int) iget_object_or_null(methodHookParam.args[0], "extraflag")) == 32768) {
                        iput_object(methodHookParam.args[0], "extraflag", 0);
                    }
                }
            });

            XposedHelpers.findAndHookMethod(_PttItemBuilder(), "a", ChatMessage, itemHolder, View.class, BaseChatItemLayout, listener2,
                    new XC_MethodHook(51) {
                        @Override
                        public void afterHookedMethod(final MethodHookParam param) throws Throwable {
                            if (!isEnabled()) return;
                            View view;
                            ViewGroup relativeLayout = (ViewGroup) param.getResult();
                            final Object app = iget_object_or_null(param.thisObject, "a", load("com.tencent.mobileqq.app.QQAppInterface"));
                            final Object session = iget_object_or_null(param.thisObject, "a", _SessionInfo());
                            String uin = "" + Utils.getLongAccountUin();
                            Context ctx = relativeLayout.getContext();
                            if (relativeLayout.findViewById(101) == null) {
                                LinearLayout linearLayout = new LinearLayout(ctx);
                                //linearLayout.setId(Integer.parseInt((String) Hook.config.get("PttItem_id"), 16));
                                linearLayout.setOrientation(0);
                                linearLayout.setGravity(17);
                                ImageView imageView = new ImageView(ctx);
                                imageView.setId(101);
                                imageView.setImageDrawable(ResUtils.loadDrawableFromAsset("repeat.png", ctx));
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                                layoutParams.rightMargin = dip2px(ctx, (float) 10);
                                linearLayout.addView(imageView, layoutParams);
                                linearLayout.addView(relativeLayout, -2, -2);
                                ImageView imageView2 = new ImageView(ctx);
                                imageView2.setId(102);
                                imageView2.setImageDrawable(ResUtils.loadDrawableFromAsset("repeat.png", ctx));
                                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-2, -2);
                                layoutParams2.leftMargin = dip2px(ctx, (float) 10);
                                linearLayout.addView(imageView2, layoutParams2);
                                param.setResult(linearLayout);
                                view = linearLayout;
                            } else {
                                view = relativeLayout.findViewById(101);
                            }
                            ImageView imageView3 = (ImageView) view.findViewById(101);
                            @SuppressLint("ResourceType") ImageView imageView4 = (ImageView) view.findViewById(102);
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
                                        Class[] argt = null;
                                        Method m = null;
                                        for (Method mi : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
                                            if (!mi.getName().equals("a")) continue;
                                            argt = mi.getParameterTypes();
                                            if (argt.length < 3) continue;
                                            if (argt[0].equals(load("com/tencent/mobileqq/app/QQAppInterface")) && argt[1].equals(_SessionInfo())
                                                    && argt[2].isAssignableFrom(param.args[0].getClass())) {
                                                m = mi;
                                                break;
                                            }
                                        }
                                        if (argt.length == 3) m.invoke(null, app, session, param.args[0]);
                                        else m.invoke(null, app, session, param.args[0], 0);
                                    } catch (Throwable e) {
                                        log(e);
                                    }
                                }
                            };
                            imageView3.setOnClickListener(r0);
                            imageView4.setOnClickListener(r0);
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
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int[] getPreconditions() {
        return new int[0];
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(bug_repeater);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
