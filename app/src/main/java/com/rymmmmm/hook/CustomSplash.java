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
package com.rymmmmm.hook;

import android.content.res.AssetManager;
import cc.ioctl.dialog.RikkaCustomSplash;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//自定义启动图
@FunctionEntry
public class CustomSplash extends CommonDelayableHook {

    public static final CustomSplash INSTANCE = new CustomSplash();

    private static final byte[] TRANSPARENT_PNG = new byte[]{
        (byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47, (byte) 0x0D, (byte) 0x0A, (byte) 0x1A,
        (byte) 0x0A,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0D, (byte) 0x49, (byte) 0x48, (byte) 0x44,
        (byte) 0x52,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x01,
        (byte) 0x08, (byte) 0x06, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x1F, (byte) 0x15,
        (byte) 0xC4,
        (byte) 0x89, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0B, (byte) 0x49, (byte) 0x44,
        (byte) 0x41,
        (byte) 0x54, (byte) 0x08, (byte) 0xD7, (byte) 0x63, (byte) 0x60, (byte) 0x00, (byte) 0x02,
        (byte) 0x00,
        (byte) 0x00, (byte) 0x05, (byte) 0x00, (byte) 0x01, (byte) 0xE2, (byte) 0x26, (byte) 0x05,
        (byte) 0x9B,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x49, (byte) 0x45, (byte) 0x4E,
        (byte) 0x44,
        (byte) 0xAE, (byte) 0x42, (byte) 0x60, (byte) 0x82};

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
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    String fileName = (String) param.args[0];
                    if ("splash.jpg".equals(fileName) || "splash_big.jpg".equals(fileName)) {
                        String customPath = RikkaCustomSplash.getCurrentSplashPath();
                        if (customPath == null) {
                            return;
                        }
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
