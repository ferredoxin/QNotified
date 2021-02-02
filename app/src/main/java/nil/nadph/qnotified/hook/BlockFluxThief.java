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
package nil.nadph.qnotified.hook;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.HttpURLConnection;
import java.net.URL;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;

public class BlockFluxThief extends CommonDelayableHook {
    public static final BlockFluxThief INSTANCE = new BlockFluxThief();

    private BlockFluxThief() {
        super("bug_block_flux_thief", SyncUtils.PROC_ANY, true, new DexDeobfStep(DexKit.C_ZipUtils_biz));
    }

    @Override
    protected boolean initOnce() {
        try {
            Method downloadImage = null;
            for (Method m : DexKit.doFindClass(DexKit.C_HttpDownloader).getDeclaredMethods()) {
                if (m.getReturnType() != File.class || Modifier.isStatic(m.getModifiers())) {
                    continue;
                }
                Class<?>[] argt = m.getParameterTypes();
                if (argt.length != 5 || argt[0] != OutputStream.class || argt[3] != int.class || argt[4] != URL.class) {
                    continue;
                }
                downloadImage = m;
                break;
            }
            XposedBridge.hookMethod(downloadImage, new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks || !isEnabled()) {
                        return;
                    }
                    long maxSize = 32 * 1024 * 1024;//32MiB
                    String url = (String) iget_object_or_null(param.args[1], "urlStr");
                    Class<?> cHttpDownloader = param.method.getDeclaringClass();
                    Method mGetFilePath = cHttpDownloader.getMethod("getFilePath", String.class);
                    if (mGetFilePath == null) {
                        mGetFilePath = Utils.hasMethod(mGetFilePath, "d", null, String.class, String.class);
                    }
                    String savePath = (String) mGetFilePath.invoke(null, url);
                    if (!new File(savePath).exists()) {
                        try {
                            long size = requestUrlSizeBlocked(url);
                            if (size != -1) {
                                if (size > maxSize) {
                                    param.setResult(null);
                                    Toasts.show(null, String.format("已拦截异常图片加载, 大小: %s", BugUtils.getSizeString(size)));
                                }
                            } else {
                                // TODO: 2021-1-9 Unknown size, do nothing?
                            }
                        } catch (IOException e) {
                            Utils.logd("BlockFluxThief/Req " + e.toString() + " URL=" + url);
                        }
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    static long requestUrlSizeBlocked(String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();
        String lenStr = conn.getHeaderField("Content-Length");
        conn.getInputStream().close();
        conn.disconnect();
        if (lenStr == null) {
            return -1L;
        } else {
            try {
                return Long.parseLong(lenStr);
            } catch (Throwable th) {
                Utils.logd(String.format("BlockFluxThief/W [%d] %s %s", code, lenStr, url));
                return -1;
            }
        }
    }
}
