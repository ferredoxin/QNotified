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

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.CustomMenu;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class PicMd5Hook extends BaseDelayableHook {
    public static final String qn_show_pic_md5 = "qn_show_pic_md5";
    private static final PicMd5Hook self = new PicMd5Hook();
    private boolean inited = false;

    PicMd5Hook() {
    }

    public static PicMd5Hook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class cl_PicItemBuilder = Initiator._PicItemBuilder();
            Class cl_BasePicItemBuilder = cl_PicItemBuilder.getSuperclass();
            MainHook.findAndHookMethodIfExists(cl_PicItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
            MainHook.findAndHookMethodIfExists(cl_BasePicItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
            for (Method m : cl_PicItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
            for (Method m : cl_BasePicItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) continue;
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public static class GetMenuItemCallBack extends XC_MethodHook {
        public GetMenuItemCallBack() {
            super(60);
        }

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            if (LicenseStatus.sDisableCommonHooks) return;
            try {
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                if (!PicMd5Hook.get().isEnabled()) return;
            } catch (Exception ignored) {
            }
            Object arr = param.getResult();
            Class<?> clQQCustomMenuItem = arr.getClass().getComponentType();
            Object item_copy = CustomMenu.createItem(clQQCustomMenuItem, R.id.item_showPicMd5, "MD5");
            Object ret = Array.newInstance(clQQCustomMenuItem, Array.getLength(arr) + 1);
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(arr, 0, ret, 0, Array.getLength(arr));
            Array.set(ret, Array.getLength(arr), item_copy);
            param.setResult(ret);
        }
    }

    public static class MenuItemClickCallback extends XC_MethodHook {
        public MenuItemClickCallback() {
            super(60);
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            int id = (int) param.args[0];
            final Activity ctx = (Activity) param.args[1];
            final Object chatMessage = param.args[2];
            if (id == R.id.item_showPicMd5) {
                param.setResult(null);
                try {
                    final String md5;
                    if (chatMessage == null || (md5 = (String) iget_object_or_null(chatMessage, "md5")) == null || md5.length() == 0) {
                        showToast(ctx, TOAST_TYPE_ERROR, "获取图片MD5失败", Toast.LENGTH_SHORT);
                        return;
                    }
                    CustomDialog.createFailsafe(ctx).setTitle("MD5").setCancelable(true).setMessage(md5).setPositiveButton("复制", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ClipboardManager clipboardManager = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                            clipboardManager.setPrimaryClip(ClipData.newPlainText(null, md5));
                        }
                    }).setNegativeButton("关闭", null).show();
                } catch (Throwable e) {
                    log(e);
                    showToast(ctx, TOAST_TYPE_ERROR, e.toString().replace("java.lang.", ""), Toast.LENGTH_SHORT);
                }
            }
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
//        try {
//            ConfigManager mgr = ConfigManager.getDefaultConfig();
//            mgr.getAllConfig().put(qn_show_pic_md5, enabled);
//            mgr.save();
//        } catch (final Exception e) {
//            Utils.log(e);
//            if (Looper.myLooper() == Looper.getMainLooper()) {
//                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
//            } else {
//                SyncUtils.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
//                    }
//                });
//            }
//        }
    }

    @Override
    public boolean isEnabled() {
        return true;
//        try {
//            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_show_pic_md5);
//        } catch (Exception e) {
//            log(e);
//            return false;
//        }
    }
}
