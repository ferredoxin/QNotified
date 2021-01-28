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
package com.rymmmmm.hook;

import android.content.res.AssetManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.dialog.RikkaCustomSplash;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//自定义启动图
public class CustomSplash extends CommonDelayableHook {
    private static final CustomSplash self = new CustomSplash();

    private static final byte[] TRANSPARENT_PNG = new byte[]{
            (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A, (byte) 0x0A,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x48, (byte) 0x44, (byte) 0x52,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01,
            (byte) 0x08, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1F, (byte) 0x15, (byte) 0xC4,
            (byte) 0x89, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x49, (byte) 0x44, (byte) 0x41,
            (byte) 0x54, (byte) 0x08, (byte) 0xD7, (byte) 0x63, (byte) 0x60, (byte) 0x00, (byte) 0x02, (byte) 0x00,
            (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0xE2, (byte) 0x26, (byte) 0x05, (byte) 0x9B,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x45, (byte) 0x4E, (byte) 0x44,
            (byte) 0xAE, (byte) 0x42, (byte) 0x60, (byte) 0x82};

    public static CustomSplash get() {
        return self;
    }

    protected CustomSplash() {
        super("__NOT_USED__");
    }

    @Override
    public boolean initOnce() {
        try {
            Method open = AssetManager.class.getDeclaredMethod("open", String.class, int.class);
            XposedBridge.hookMethod(open, new XC_MethodHook(53) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    String fileName = (String) param.args[0];
                    if ("splash.jpg".equals(fileName) || "splash_big.jpg".equals(fileName)) {
                        String customPath = RikkaCustomSplash.getCurrentSplashPath();
                        if (customPath == null) return;
                        File f = new File(customPath);
                        if (f.exists() && f.isFile() && f.canRead()) {
                            param.setResult(new FileInputStream(f));
                        }
                    }
                    if ("splash_logo.png".equals(fileName)) {
                        param.setResult(new ByteArrayInputStream(TRANSPARENT_PNG));
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return RikkaCustomSplash.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
