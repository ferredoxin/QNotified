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

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import ltd.nextalone.util.SystemServiceUtils;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.CustomMenu;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;

@FunctionEntry
public class PicMd5Hook extends CommonDelayableHook {

    public static final PicMd5Hook INSTANCE = new PicMd5Hook();

    PicMd5Hook() {
        super("qn_show_pic_md5");
    }

    @Override
    public boolean initOnce() {
        try {
            Class cl_PicItemBuilder = Initiator._PicItemBuilder();
            Class cl_BasePicItemBuilder = cl_PicItemBuilder.getSuperclass();
            try {
                XposedHelpers.findAndHookMethod(cl_PicItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
                XposedHelpers.findAndHookMethod(cl_BasePicItemBuilder, "a", int.class, Context.class, load("com/tencent/mobileqq/data/ChatMessage"), new MenuItemClickCallback());
            } catch (Exception e) {}
            for (Method m : cl_PicItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) {
                    continue;
                }
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
            for (Method m : cl_BasePicItemBuilder.getDeclaredMethods()) {
                if (!m.getReturnType().isArray()) {
                    continue;
                }
                Class[] ps = m.getParameterTypes();
                if (ps.length == 1 && ps[0].equals(View.class)) {
                    XposedBridge.hookMethod(m, new GetMenuItemCallBack());
                    break;
                }
            }
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
            if (LicenseStatus.sDisableCommonHooks) {
                return;
            }
            if (!INSTANCE.isEnabled()) {
                return;
            }
            Object arr = param.getResult();
            Class<?> clQQCustomMenuItem = arr.getClass().getComponentType();
            Object item_copy = CustomMenu
                .createItem(clQQCustomMenuItem, R.id.item_showPicMd5, "MD5");
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
                    if (chatMessage == null
                        || (md5 = (String) iget_object_or_null(chatMessage, "md5")) == null
                        || md5.length() == 0) {
                        Toasts.error(ctx, "获取图片MD5失败");
                        return;
                    }
                    CustomDialog.createFailsafe(ctx).setTitle("MD5").setCancelable(true)
                        .setMessage(md5).setPositiveButton("复制", (dialog, which) -> {
                        SystemServiceUtils.copyToClipboard(ctx, md5);
                    }).setNegativeButton("关闭", null).show();
                } catch (Throwable e) {
                    log(e);
                    Toasts.error(ctx, e.toString().replace("java.lang.", ""));
                }
            }
        }
    }
}
