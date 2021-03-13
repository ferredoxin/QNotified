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

import static nil.nadph.qnotified.util.Initiator._MessageRecord;
import static nil.nadph.qnotified.util.Initiator._PicItemBuilder;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.findField;
import static nil.nadph.qnotified.util.Utils.getShort$Name;
import static nil.nadph.qnotified.util.Utils.isCallingFromEither;
import static nil.nadph.qnotified.util.Utils.log;

import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.ReflexUtil;
import nil.nadph.qnotified.util.Toasts;

@FunctionEntry
public class FlashPicHook extends CommonDelayableHook {

    public static final FlashPicHook INSTANCE = new FlashPicHook();
    private static Field MsgRecord_msgtype = null;
    private static Method MsgRecord_getExtInfoFromExtStr = null;

    private FlashPicHook() {
        super("qn_flash_as_pic", new DexDeobfStep(DexKit.C_FLASH_PIC_HELPER),
            new DexDeobfStep(DexKit.C_BASE_PIC_DL_PROC),
            new DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC));
    }

    public static boolean isFlashPic(Object msgRecord) {
        try {
            if (MsgRecord_msgtype == null) {
                MsgRecord_msgtype = _MessageRecord().getField("msgtype");
                MsgRecord_msgtype.setAccessible(true);
            }
            if (MsgRecord_getExtInfoFromExtStr == null) {
                MsgRecord_getExtInfoFromExtStr = _MessageRecord()
                    .getMethod("getExtInfoFromExtStr", String.class);
                MsgRecord_getExtInfoFromExtStr.setAccessible(true);
            }
            int msgtype = (int) MsgRecord_msgtype.get(msgRecord);
            return (msgtype == -2000 || msgtype == -2006)
                && !TextUtils.isEmpty(
                (String) MsgRecord_getExtInfoFromExtStr.invoke(msgRecord, "commen_flash_pic"));
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean initOnce() {
        try {
            boolean canInit = checkPreconditions();
            if (!canInit && isEnabled()) {
                if (Looper.myLooper() != null) {
                    Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(),
                        "QNotified:闪照功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) {
                return false;
            }
            Class clz = DexKit.loadClassFromCache(DexKit.C_FLASH_PIC_HELPER);
            Method isFlashPic = null;
            for (Method mi : clz.getDeclaredMethods()) {
                if (mi.getReturnType().equals(boolean.class)
                    && mi.getParameterTypes().length == 1) {
                    String name = mi.getName();
                    if (name.equals("a") || name.equals("z") || name.equals("W")) {
                        isFlashPic = mi;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(isFlashPic, new XC_MethodHook(52) {
                String sn_ItemBuilderFactory = null;
                String sn_BasePicDownloadProcessor = null;

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) {
                        return;
                    }
                    if (sn_BasePicDownloadProcessor == null) {
                        sn_BasePicDownloadProcessor = getShort$Name(
                            DexKit.doFindClass(DexKit.C_BASE_PIC_DL_PROC));
                    }
                    if (sn_ItemBuilderFactory == null) {
                        sn_ItemBuilderFactory = getShort$Name(
                            DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC));
                    }
                    if (isCallingFromEither(sn_ItemBuilderFactory, sn_BasePicDownloadProcessor,
                        "FlashPicItemBuilder")) {
                        param.setResult(false);
                    }
                }
            });
            Class tmp;
            Class mBaseBubbleBuilder$ViewHolder = load(
                "com.tencent.mobileqq.activity.aio.BaseBubbleBuilder$ViewHolder");
            if (mBaseBubbleBuilder$ViewHolder == null) {
                tmp = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder");
                for (Method mi : tmp.getDeclaredMethods()) {
                    if (Modifier.isAbstract(mi.getModifiers())
                        && mi.getParameterTypes().length == 0) {
                        mBaseBubbleBuilder$ViewHolder = mi.getReturnType();
                        break;
                    }
                }
            }
            Method m = null;
            for (Method mi : _PicItemBuilder().getDeclaredMethods()) {
                if (mi.getReturnType().equals(View.class) && mi.getParameterTypes().length == 5) {
                    m = mi;
                    break;
                }
            }
            final Method __tmnp_isF = isFlashPic;
            final Class<?> __tmp_mBaseBubbleBuilder$ViewHolder = mBaseBubbleBuilder$ViewHolder;
            XposedBridge.hookMethod(m, new XC_MethodHook() {
                private Field fBaseChatItemLayout = null;
                private Method setTailMessage = null;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    Object viewHolder = param.args[1];
                    if (viewHolder == null) {
                        return;
                    }
                    if (fBaseChatItemLayout == null) {
                        fBaseChatItemLayout = findField(viewHolder.getClass(),
                            load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"), "a");
                        if (fBaseChatItemLayout == null) {
                            fBaseChatItemLayout = ReflexUtil
                                .getFirstNSFFieldByType(viewHolder.getClass(),
                                    load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"));
                        }
                        fBaseChatItemLayout.setAccessible(true);
                    }
                    if (setTailMessage == null) {
                        setTailMessage = XposedHelpers.findMethodExact(
                            load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"),
                            "setTailMessage", boolean.class, CharSequence.class,
                            View.OnClickListener.class);
                        setTailMessage.setAccessible(true);
                    }
                    if (setTailMessage != null) {
                        Object baseChatItemLayout = fBaseChatItemLayout.get(viewHolder);
                        setTailMessage
                            .invoke(baseChatItemLayout, isFlashPic(param.args[0]), "闪照", null);
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
