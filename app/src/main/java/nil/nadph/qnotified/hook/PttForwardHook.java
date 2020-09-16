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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import androidx.core.view.ViewCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.ChatActivityFacade;
import nil.nadph.qnotified.bridge.SessionInfoImpl;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.HighContrastBorder;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.ui.ViewBuilder.newLinearLayoutParams;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;


public class PttForwardHook extends BaseDelayableHook {

    public static final int R_ID_PTT_FORWARD = 0x30EE77CB;
    public static final int R_ID_PTT_SAVE = 0x30EE77CC;
    public static final String qn_enable_ptt_forward = "qn_enable_ptt_forward";
    public static final String qn_enable_ptt_save = "qn_enable_ptt_save";
    public static final String qn_cache_ptt_save_last_parent_dir = "qn_cache_ptt_save_last_parent_dir";
    private static final PttForwardHook self = new PttForwardHook();
    private boolean inited = false;

    private PttForwardHook() {
    }

    public static PttForwardHook get() {
        return self;
    }

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
                    Field f = findField(param.thisObject.getClass(), Bundle.class, "a");
                    if (f == null) {
                        f = getFirstNSFFieldByType(param.thisObject.getClass(), Bundle.class);
                    }
                    f.setAccessible(true);
                    Bundle data = (Bundle) f.get(param.thisObject);
                    if (!data.containsKey("ptt_forward_path")) return;
                    param.setResult(null);
                    final String path = data.getString("ptt_forward_path");
                    Activity ctx = Utils.iget_object_or_null(param.thisObject, "a", Activity.class);
                    if (ctx == null)
                        ctx = Utils.iget_object_or_null(param.thisObject, "mActivity", Activity.class);
                    if (path == null || !new File(path).exists()) {
                        Utils.showToast(ctx, TOAST_TYPE_ERROR, "Error: Invalid ptt file!", Toast.LENGTH_SHORT);
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
                        cd.nick = data.getString("uinname");
                        if (cd.nick == null) cd.nick = data.getString("uin");
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
                    CustomDialog dialog = CustomDialog.create(ctx);
                    final Activity finalCtx = ctx;
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                for (Utils.ContactDescriptor cd : mTargets) {
                                    Parcelable sesssion = SessionInfoImpl.createSessionInfo(cd.uin, cd.uinType);
                                    ChatActivityFacade.sendPttMessage(getQQAppInterface(), sesssion, path);
                                }
                                Utils.showToast(finalCtx, TOAST_TYPE_SUCCESS, "已发送", Toast.LENGTH_SHORT);
                            } catch (Throwable e) {
                                log(e);
                                try {
                                    Utils.showToast(finalCtx, TOAST_TYPE_ERROR, "失败: " + e, Toast.LENGTH_SHORT);
                                } catch (Throwable ignored) {
                                    Toast.makeText(finalCtx, "失败: " + e, Toast.LENGTH_SHORT).show();
                                }
                            }
                            finalCtx.finish();
                        }
                    });
                    dialog.setNegativeButton("取消", new Utils.DummyCallback());
                    dialog.setCancelable(true);
                    dialog.setView(main);
                    dialog.setTitle("发送给");
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
                    } else if (id == R_ID_PTT_SAVE) {
                        param.setResult(null);
                        String url = (String) invoke_virtual(chatMessage, "getLocalFilePath");
                        File file = new File(url);
                        if (!file.exists()) {
                            Utils.showToast(context, TOAST_TYPE_ERROR, "未找到语音文件", Toast.LENGTH_SHORT);
                            return;
                        }
                        showSavePttFileDialog(context, file);
                    }
                }
            });
            for (Method m : cl_PttItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class<?>[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class))
                    XposedBridge.hookMethod(m, new XC_MethodHook(60) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks) return;
                            try {
                                ConfigManager cfg = ConfigManager.getDefaultConfig();
                                if (!cfg.getBooleanOrFalse(qn_enable_ptt_forward)) return;
                            } catch (Exception ignored) {
                            }
                            Object arr = param.getResult();
                            Class<?> clQQCustomMenuItem = arr.getClass().getComponentType();
                            Object ret;
                            if (isSavePttEnabled()) {
                                Object item_forward = CustomMenu.createItem(clQQCustomMenuItem, R_ID_PTT_FORWARD, "转发");
                                Object item_save = CustomMenu.createItem(clQQCustomMenuItem, R_ID_PTT_SAVE, "保存");
                                ret = Array.newInstance(clQQCustomMenuItem, Array.getLength(arr) + 2);
                                Array.set(ret, 0, Array.get(arr, 0));
                                //noinspection SuspiciousSystemArraycopy
                                System.arraycopy(arr, 1, ret, 2, Array.getLength(arr) - 1);
                                Array.set(ret, 1, item_forward);
                                Array.set(ret, Array.getLength(ret) - 1, item_save);
                            } else {
                                Object item_forward = CustomMenu.createItem(clQQCustomMenuItem, R_ID_PTT_FORWARD, "转发");
                                ret = Array.newInstance(clQQCustomMenuItem, Array.getLength(arr) + 1);
                                Array.set(ret, 0, Array.get(arr, 0));
                                //noinspection SuspiciousSystemArraycopy
                                System.arraycopy(arr, 1, ret, 2, Array.getLength(arr) - 1);
                                Array.set(ret, 1, item_forward);
                            }
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
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_FACADE)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_enable_ptt_forward, enabled);
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

    private static void showSavePttFileDialog(Activity context, final File ptt) {
        CustomDialog dialog = CustomDialog.createFailsafe(context);
        final Context ctx = dialog.getContext();
        final EditText editText = new EditText(ctx);
        TextView tv = new TextView(ctx);
        tv.setText("格式为.slk/.amr 一般无法直接打开slk格式 而且大多数语音均为slk格式(转发语音可以看到格式) 请自行寻找软件进行转码");
        tv.setPadding(20, 10, 20, 10);
        String lastSaveDir = ConfigManager.getCache().getString(qn_cache_ptt_save_last_parent_dir);
        if (TextUtils.isEmpty(lastSaveDir)) {
            File f = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
            if (f == null) {
                f = Environment.getExternalStorageDirectory();
            }
            lastSaveDir = f.getPath();
        }
        editText.setText(new File(lastSaveDir, Utils.getPathTail(ptt)).getPath());
        editText.setTextSize(16);
        int _5 = dip2px(ctx, 5);
        editText.setPadding(_5, _5, _5, _5);
        //editText.setBackgroundDrawable(new HighContrastBorder());
        ViewCompat.setBackground(editText, new HighContrastBorder());
        LinearLayout linearLayout = new LinearLayout(ctx);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(tv, MATCH_PARENT, WRAP_CONTENT);
        linearLayout.addView(editText, newLinearLayoutParams(MATCH_PARENT, WRAP_CONTENT, _5 * 2));
        final AlertDialog alertDialog = (AlertDialog) dialog
                .setTitle("输入保存路径(请自行转码)")
                .setView(linearLayout)
                .setPositiveButton("保存", null)
                .setNegativeButton("取消", null)
                .create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editText.getText().toString();
                if (path.equals("")) {
                    showToast(ctx, TOAST_TYPE_ERROR, "请输入路径", Toast.LENGTH_SHORT);
                    return;
                }
                if (!path.startsWith("/")) {
                    showToast(ctx, TOAST_TYPE_ERROR, "请输入完整路径(以\"/\"开头)", Toast.LENGTH_SHORT);
                    return;
                }
                File f = new File(path);
                File dir = f.getParentFile();
                if (dir == null || !dir.exists() || !dir.isDirectory()) {
                    showToast(ctx, TOAST_TYPE_ERROR, "文件夹不存在", Toast.LENGTH_SHORT);
                    return;
                }
                if (!dir.canWrite()) {
                    showToast(ctx, TOAST_TYPE_ERROR, "文件夹无访问权限", Toast.LENGTH_SHORT);
                    return;
                }
                FileOutputStream fout = null;
                FileInputStream fin = null;
                try {
                    if (!f.exists()) f.createNewFile();
                    fin = new FileInputStream(ptt);
                    fout = new FileOutputStream(f);
                    byte[] buf = new byte[1024];
                    int i;
                    while ((i = fin.read(buf)) > 0) {
                        fout.write(buf, 0, i);
                    }
                    fout.flush();
                    alertDialog.dismiss();
                    ConfigManager cache = ConfigManager.getCache();
                    String pdir = f.getParent();
                    if (pdir != null) {
                        cache.putString(qn_cache_ptt_save_last_parent_dir, pdir);
                        cache.save();
                    }
                } catch (IOException e) {
                    showToast(ctx, TOAST_TYPE_ERROR, "失败:" + e.toString().replace("java.io.", ""), Toast.LENGTH_SHORT);
                } finally {
                    if (fin != null) {
                        try {
                            fin.close();
                        } catch (IOException ignored) {
                        }
                    }
                    if (fout != null) {
                        try {
                            fout.close();
                        } catch (IOException ignored) {
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_enable_ptt_forward);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }


    public boolean isSavePttEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_enable_ptt_save);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
