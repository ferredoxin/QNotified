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

import static nil.nadph.qnotified.util.Utils.log;

import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Toasts;

public class GalleryBgHook extends CommonDelayableHook {

    private static final GalleryBgHook self = new GalleryBgHook();

    private GalleryBgHook() {
        super("qn_gallery_bg", SyncUtils.PROC_PEAK, new DexDeobfStep(DexKit.C_ABS_GAL_SCENE));
    }

    public static GalleryBgHook get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            boolean canInit = checkPreconditions();
            if (!canInit && isEnabled()) {
                if (Looper.myLooper() != null) {
                    Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(),
                        "QNotified:聊天图片背景功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) {
                return false;
            }
            XposedHelpers
                .findAndHookMethod(DexKit.doFindClass(DexKit.C_ABS_GAL_SCENE), "a", ViewGroup.class,
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (!isEnabled()) {
                                return;
                            }
                            for (Field f : param.method.getDeclaringClass().getDeclaredFields()) {
                                if (f.getType().equals(View.class)) {
                                    f.setAccessible(true);
                                    View v = (View) f.get(param.thisObject);
                                    v.setBackgroundColor(0x00000000);
                                    return;
                                }
                            }
                        }
                    });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
