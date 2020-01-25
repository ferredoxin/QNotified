package nil.nadph.qnotified.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.FaceImpl;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.ResUtils;
import nil.nadph.qnotified.util.Utils;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;


public class PttForwardHook extends BaseDelayableHook {

    public static final int R_ID_PTT_FORWARD = 0x00EE77CB;


    private PttForwardHook() {
    }

    private static final PttForwardHook self = new PttForwardHook();

    public static PttForwardHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz_ForwardBaseOption = load("com/tencent/mobileqq/forward/ForwardBaseOption");
            if (clz_ForwardBaseOption == null) {
                Class clz_DirectForwardActivity = load("com/tencent/mobileqq/activity/DirectForwardActivity");
                for (Field f : clz_DirectForwardActivity.getDeclaredFields()) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    Class clz = f.getType();
                    if (Modifier.isAbstract(clz.getModifiers()) && !clz.getName().contains("android")) {
                        clz_ForwardBaseOption = clz;
                        break;
                    }
                }
            }
            Method buildConfirmDialog = null;
            for (Method m : clz_ForwardBaseOption.getDeclaredMethods()) {
                if (!m.getReturnType().equals(void.class)) continue;
                if (!Modifier.isFinal(m.getModifiers())) continue;
                if (m.getParameterTypes().length != 0) continue;
                buildConfirmDialog = m;
                break;
            }
            XposedBridge.hookMethod(buildConfirmDialog, new XC_MethodHook(51) {
                @SuppressLint("SetTextI18n")
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    Bundle data = (Bundle) Utils.iget_object_or_null(param.thisObject, "a", Bundle.class);
                    if (!data.containsKey("ptt_forward_path")) return;
                    param.setResult(null);
                    final String path = data.getString("ptt_forward_path");
                    final Activity ctx = (Activity) Utils.iget_object_or_null(param.thisObject, "a", Activity.class);
                    if (path == null || !new File(path).exists()) {
                        Utils.showToast(ctx, TOAST_TYPE_ERROR, "InternalError: Invalid ptt file!", Toast.LENGTH_SHORT);
                        return;
                    }
                    ResUtils.initTheme(ctx);
                    boolean multi;
                    final ArrayList<Utils.ContactDescriptor> mTargets = new ArrayList<>();
                    boolean unsupport = false;
                    if (data.containsKey("forward_multi_target")) {
                        ArrayList targets = data.getParcelableArrayList("forward_multi_target");
                        if (targets.size() > 1) {
                            multi = true;
                            for (Object rr : targets) {
                                Utils.ContactDescriptor c = Utils.parseResultRec(rr);
                                mTargets.add(c);
                            }
                        } else {
                            multi = false;
                            Utils.ContactDescriptor c = Utils.parseResultRec(targets.get(0));
                            mTargets.add(c);
                        }
                    } else {
                        multi = false;
                        Utils.ContactDescriptor cd = new Utils.ContactDescriptor();
                        cd.uin = data.getString("uin");
                        cd.uinType = data.getInt("uintype", -1);
                        cd.nick = data.getString("uinname", data.getString("uin"));
                        mTargets.add(cd);
                    }
                    if (unsupport) Utils.showToastShort(ctx, "暂不支持我的设备/临时聊天/讨论组");
                    LinearLayout main = new LinearLayout(ctx);
                    main.setOrientation(LinearLayout.VERTICAL);
                    LinearLayout heads = new LinearLayout(ctx);
                    heads.setGravity(Gravity.CENTER_VERTICAL);
                    heads.setOrientation(LinearLayout.HORIZONTAL);
                    View div = new View(ctx);
                    div.setBackgroundColor(ResUtils.skin_gray3.getDefaultColor());
                    TextView tv = new TextView(ctx);
                    tv.setText("[语音转发]" + path);
                    tv.setTextColor(ResUtils.skin_gray3);
                    tv.setTextSize(dip2sp(ctx, 16));
                    int pd = dip2px(ctx, 8);
                    tv.setPadding(pd, pd, pd, pd);
                    main.addView(heads, MATCH_PARENT, WRAP_CONTENT);
                    main.addView(div, MATCH_PARENT, 1);
                    main.addView(tv, MATCH_PARENT, WRAP_CONTENT);
                    int w = dip2px(ctx, 40);
                    LinearLayout.LayoutParams imglp = new LinearLayout.LayoutParams(w, w);
                    imglp.setMargins(pd, pd, pd, pd);
                    FaceImpl face = FaceImpl.getInstance();
                    if (multi) {
                        if (mTargets != null) for (Utils.ContactDescriptor cd : mTargets) {
                            ImageView imgview = new ImageView(ctx);
                            Bitmap bm = face.getBitmapFromCache(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin);
                            if (bm == null) {
                                imgview.setImageDrawable(ResUtils.loadDrawableFromAsset("face.png", ctx));
                                face.registerView(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin, imgview);
                            } else {
                                imgview.setImageBitmap(bm);
                            }
                            heads.addView(imgview, imglp);
                        }
                    } else {
                        Utils.ContactDescriptor cd = mTargets.get(0);
                        ImageView imgview = new ImageView(ctx);
                        Bitmap bm = face.getBitmapFromCache(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin);
                        if (bm == null) {
                            imgview.setImageDrawable(ResUtils.loadDrawableFromAsset("face.png", ctx));
                            face.registerView(cd.uinType == 1 ? FaceImpl.TYPE_TROOP : FaceImpl.TYPE_USER, cd.uin, imgview);
                        } else {
                            imgview.setImageBitmap(bm);
                        }
                        heads.setPadding(pd / 2, pd / 2, pd / 2, pd / 2);
                        TextView ni = new TextView(ctx);
                        ni.setText(cd.nick);
                        ni.setTextColor(0xFF000000);
                        ni.setPadding(pd, 0, 0, 0);
                        ni.setTextSize(dip2sp(ctx, 18));
                        heads.addView(imgview, imglp);
                        heads.addView(ni);
                    }
                    //String ret = "" +/*ctx.getIntent().getExtras();//*/iget_object(param.thisObject, "a", Bundle.class);
                    Dialog dialog = Utils.createDialog(ctx);
                    invoke_virtual(dialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                for (Utils.ContactDescriptor cd : mTargets) {
                                    Object sesssion = Utils.createSessionInfo(cd.uin, cd.uinType);
                                    XposedHelpers.callStaticMethod(DexKit.doFindClass(DexKit.C_FACADE), "a", Utils.getQQAppInterface(), sesssion, path);
                                }
                                Utils.showToast(ctx, TOAST_TYPE_SUCCESS, "已发送", Toast.LENGTH_SHORT);
                            } catch (Throwable e) {
                                log(e);
                                try {
                                    Utils.showToast(ctx, TOAST_TYPE_ERROR, "失败: " + e, Toast.LENGTH_SHORT);
                                } catch (Throwable ignored) {
                                    Toast.makeText(ctx, "失败: " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                            ctx.finish();
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(dialog, "setNegativeButton", "取消", new Utils.DummyCallback(), String.class, DialogInterface.OnClickListener.class);
                    dialog.setCancelable(true);
                    invoke_virtual(dialog, "setView", main, View.class);
                    invoke_virtual(dialog, "setTitle", "发送给", String.class);
                    dialog.show();
                }
            });
            Class cl_PttItemBuilder = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder");
            if (cl_PttItemBuilder == null) {
                Class cref = load("com/tencent/mobileqq/activity/aio/item/PttItemBuilder$2");
                try {
                    cl_PttItemBuilder = cref.getDeclaredField("this$0").getType();
                } catch (NoSuchFieldException e) {
                }
            }
            findAndHookMethod(cl_PttItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new XC_MethodHook(60) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    int id = (int) param.args[0];
                    Activity context = (Activity) param.args[1];
                    Object chatMessage = param.args[2];
                    if (id == R_ID_PTT_FORWARD) {
                        param.setResult(null);
                        String url = (String) invoke_virtual(chatMessage, "getLocalFilePath");
                        File file = new File(url);
                        if (!file.exists()) {
                            Utils.showToast(context, TOAST_TYPE_ERROR, "未找到语音文件", Toast.LENGTH_SHORT);
                            return;
                        }
                        Intent intent = new Intent(context, load("com/tencent/mobileqq/activity/ForwardRecentActivity"));
                        intent.putExtra("selection_mode", 0);
                        intent.putExtra("direct_send_if_dataline_forward", false);
                        intent.putExtra("forward_text", "null");
                        intent.putExtra("ptt_forward_path", file.getPath());
                        intent.putExtra("forward_type", -1);
                        intent.putExtra("caller_name", "ChatActivity");
                        intent.putExtra("k_smartdevice", false);
                        intent.putExtra("k_dataline", false);
                        intent.putExtra("k_forward_title", "语音转发");
                        context.startActivity(intent);
                    }
                }
            });
            for (Method m : cl_PttItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class))
                    XposedBridge.hookMethod(m, new XC_MethodHook(60) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                ConfigManager cfg = ConfigManager.getDefault();
                                if (!cfg.getBooleanOrFalse(qn_enable_ptt_forward)) return;
                            } catch (Exception ignored) {
                            }
                            Object arr = param.getResult();
                            Object QQCustomMenuItem = Array.get(arr, 0).getClass().newInstance();
                            iput_object(QQCustomMenuItem, "a", int.class, R_ID_PTT_FORWARD);
                            iput_object(QQCustomMenuItem, "a", String.class, "转发");
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
        return new int[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qn_enable_ptt_forward);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
