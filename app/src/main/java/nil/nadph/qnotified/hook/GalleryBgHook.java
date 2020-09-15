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

import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.*;

public class GalleryBgHook extends BaseDelayableHook {
    public static final String qn_gallery_bg = "qn_gallery_bg";
    private static final GalleryBgHook self = new GalleryBgHook();
    private boolean inited = false;

    private GalleryBgHook() {
    }

    public static GalleryBgHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            boolean canInit = checkPreconditions();
            if (!canInit && ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_gallery_bg)) {
                if (Looper.myLooper() != null) {
                    showToast(getApplication(), TOAST_TYPE_ERROR, "QNotified:聊天图片背景功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) return false;
            XposedHelpers.findAndHookMethod(DexKit.doFindClass(DexKit.C_ABS_GAL_SCENE), "a", ViewGroup.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
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
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_PEAK;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ABS_GAL_SCENE)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_gallery_bg, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_gallery_bg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}
