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

import cc.ioctl.util.BugUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class InterceptZipBomb extends CommonDelayableHook {

    public static final InterceptZipBomb INSTANCE = new InterceptZipBomb();

    private InterceptZipBomb() {
        super("bug_intercept_zip_bomb", SyncUtils.PROC_MAIN, true,
            new DexDeobfStep(DexKit.C_ZipUtils_biz));
    }

    @Override
    protected boolean initOnce() {
        try {
            Class<?> zipUtil = DexKit.doFindClass(DexKit.C_ZipUtils_biz);
            Method m;
            try {
                m = zipUtil.getMethod("a", File.class, String.class);
            } catch (NoSuchMethodException e) {
                m = zipUtil.getMethod("unZipFile", File.class, String.class);
            }
            XposedBridge.hookMethod(m, new XC_MethodHook(51) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    File file = (File) param.args[0];
                    ZipFile zipFile = new ZipFile(file);
                    Enumeration<? extends ZipEntry> entries = zipFile.entries();
                    long sizeSum = 0;
                    while (entries.hasMoreElements()) {
                        sizeSum += entries.nextElement().getSize();
                    }
                    zipFile.close();
                    if (sizeSum >= 104550400) {
                        param.setResult(null);
                        Toasts.show(HostInformationProviderKt.getHostInfo().getApplication(),
                            String.format("已拦截 %s ,解压后大小异常: %s",
                                file.getPath(), BugUtils.getSizeString(sizeSum)));
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }
}
