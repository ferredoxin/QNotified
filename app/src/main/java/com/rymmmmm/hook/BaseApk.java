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

import static me.singleneuron.util.QQVersion.QQ_8_6_0;
import static nil.nadph.qnotified.util.Utils.log;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import cc.ioctl.dialog.RikkaBaseApkFormatDialog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

//重命名base.apk
@FunctionEntry
public class BaseApk extends CommonDelayableHook {

    public static final BaseApk INSTANCE = new BaseApk();

    protected BaseApk() {
        super("__NOT_USED__");
    }

    @Override
    public boolean initOnce() {
        try {
            if (HostInformationProviderKt.requireMinQQVersion(QQ_8_6_0)) {
                Class c = Initiator.load("com.tencent.mobileqq.utils.FileUtils");
                XposedHelpers.findAndHookMethod(c, "getFileName", String.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        try {
                            String fileName = (String) param.getResult();
                            String localFile = (String) param.args[0];
                            Utils.logd("LocalFile=" + localFile + ",FileName=" + fileName);
                            if (fileName.equals("base.apk")) {
                                PackageManager packageManager = HostInformationProviderKt
                                    .getHostInfo().getApplication().getPackageManager();
                                PackageInfo packageArchiveInfo = packageManager
                                    .getPackageArchiveInfo(localFile,
                                        PackageManager.GET_ACTIVITIES);
                                ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                applicationInfo.sourceDir = localFile;
                                applicationInfo.publicSourceDir = localFile;
                                String format = RikkaBaseApkFormatDialog
                                    .getCurrentBaseApkFormat();
                                if (format != null) {
                                    String result = format
                                        .replace("%n", applicationInfo.loadLabel(packageManager)
                                            .toString())
                                        .replace("%p", applicationInfo.packageName)
                                        .replace("%v", packageArchiveInfo.versionName)
                                        .replace("%c", String.valueOf(
                                            HostInformationProviderKt.getHostInfo()
                                                .getApplication()));
                                    param.setResult(result);
                                }
                            }
                        } catch (Exception e) {
                            Utils.log(e);
                        }
                    }
                });
                return true;
            }
            final Class<?> _ItemManagerClz = Initiator
                .load("com.tencent.mobileqq.troop.utils.TroopFileTransferManager$Item");
            for (Method m : Initiator._TroopFileUploadMgr().getDeclaredMethods()) {
                if (m.getName().equals("b") && !Modifier.isStatic(m.getModifiers()) && m
                    .getReturnType().equals(int.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 3 && argt[0] == long.class && argt[1] == _ItemManagerClz
                        && argt[2] == Bundle.class) {
                        XposedBridge.hookMethod(m, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                                if (LicenseStatus.sDisableCommonHooks) {
                                    return;
                                }
                                if (!isEnabled()) {
                                    return;
                                }
                                Object item = param.args[1];
                                Field localFile = XposedHelpers
                                    .findField(_ItemManagerClz, "LocalFile");
                                Field fileName = XposedHelpers
                                    .findField(_ItemManagerClz, "FileName");
                                if (fileName.get(item).equals("base.apk")) {
                                    PackageManager packageManager = HostInformationProviderKt
                                        .getHostInfo().getApplication().getPackageManager();
                                    PackageInfo packageArchiveInfo = packageManager
                                        .getPackageArchiveInfo((String) localFile.get(item),
                                            PackageManager.GET_ACTIVITIES);
                                    ApplicationInfo applicationInfo = packageArchiveInfo.applicationInfo;
                                    applicationInfo.sourceDir = (String) localFile.get(item);
                                    applicationInfo.publicSourceDir = (String) localFile.get(item);
                                    String format = RikkaBaseApkFormatDialog
                                        .getCurrentBaseApkFormat();
                                    if (format != null) {
                                        String result = format
                                            .replace("%n", applicationInfo.loadLabel(packageManager)
                                                .toString())
                                            .replace("%p", applicationInfo.packageName)
                                            .replace("%v", packageArchiveInfo.versionName)
                                            .replace("%c", String.valueOf(
                                                HostInformationProviderKt.getHostInfo()
                                                    .getApplication()));
                                        fileName.set(item, result);
                                    }
                                }
                            }
                        });
                    }
                }
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return RikkaBaseApkFormatDialog.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //Unsupported.
    }
}
