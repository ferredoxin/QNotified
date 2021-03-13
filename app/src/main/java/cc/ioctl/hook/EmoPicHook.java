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

import static nil.nadph.qnotified.util.Initiator._PicItemBuilder;
import static nil.nadph.qnotified.util.ReflexUtil.findField;
import static nil.nadph.qnotified.util.Utils.log;

import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.bridge.AIOUtilsImpl;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Toasts;

@FunctionEntry
public class EmoPicHook extends CommonDelayableHook {

    public static final EmoPicHook INSTANCE = new EmoPicHook();

    private EmoPicHook() {
        super("qn_sticker_as_pic", new DexDeobfStep(DexKit.C_AIO_UTILS));
    }

    @Override
    public boolean initOnce() {
        try {
            boolean canInit = checkPreconditions();
            if (!canInit && isEnabled()) {
                if (Looper.myLooper() != null) {
                    Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(),
                        "QNotified:表情转图片功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) {
                return false;
            }
            XposedHelpers
                .findAndHookMethod(_PicItemBuilder(), "onClick", View.class, new XC_MethodHook(51) {

                    Field f_picExtraData = null;
                    Field f_imageBizType = null;
                    Field f_imageType = null;

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        Object chatMsg = AIOUtilsImpl.getChatMessage((View) param.args[0]);
                        if (chatMsg == null) {
                            return;
                        }
                        if (f_picExtraData == null) {
                            f_picExtraData = findField(chatMsg.getClass(), null, "picExtraData");
                            f_picExtraData.setAccessible(true);
                        }
                        Object picMessageExtraData = f_picExtraData.get(chatMsg);
                        if (f_imageType == null) {
                            f_imageType = findField(chatMsg.getClass(), null, "imageType");
                            f_imageType.setAccessible(true);
                        }
                        f_imageType.setInt(chatMsg, 0);
                        if (picMessageExtraData != null) {
                            if (f_imageBizType == null) {
                                f_imageBizType = findField(picMessageExtraData.getClass(), null,
                                    "imageBizType");
                                f_imageBizType.setAccessible(true);
                            }
                            f_imageBizType.setInt(picMessageExtraData, 0);
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
