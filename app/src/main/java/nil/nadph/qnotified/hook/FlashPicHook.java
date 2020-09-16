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
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class FlashPicHook extends BaseDelayableHook {
    public static final String qn_flash_as_pic = "qn_flash_as_pic";
    private static final FlashPicHook self = new FlashPicHook();
    private boolean inited = false;

    private FlashPicHook() {
    }

    public static FlashPicHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            final ConfigManager cfg = ConfigManager.getDefaultConfig();
            boolean canInit = checkPreconditions();
            if (!canInit && ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic)) {
                if (Looper.myLooper() != null) {
                    showToast(getApplication(), TOAST_TYPE_ERROR, "QNotified:闪照功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) return false;
            Class clz = DexKit.loadClassFromCache(DexKit.C_FLASH_PIC_HELPER);
            Method isFlashPic = null;
            for (Method mi : clz.getDeclaredMethods()) {
                if (mi.getReturnType().equals(boolean.class) && mi.getParameterTypes().length == 1) {
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
                    try {
                        if (!ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic))
                            return;
                    } catch (Exception e) {
                        log(e);
                    }
                    if (sn_BasePicDownloadProcessor == null) {
                        sn_BasePicDownloadProcessor = getShort$Name(DexKit.doFindClass(DexKit.C_BASE_PIC_DL_PROC));
                    }
                    if (sn_ItemBuilderFactory == null) {
                        sn_ItemBuilderFactory = getShort$Name(DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC));
                    }
                    if (isCallingFromEither(sn_ItemBuilderFactory, sn_BasePicDownloadProcessor, "FlashPicItemBuilder")) {
                        param.setResult(false);
                    }
                }
            });
            Class tmp;
            Class mBaseBubbleBuilder$ViewHolder = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder$ViewHolder");
            if (mBaseBubbleBuilder$ViewHolder == null) {
                tmp = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder");
                for (Method mi : tmp.getDeclaredMethods()) {
                    if (Modifier.isAbstract(mi.getModifiers()) && mi.getParameterTypes().length == 0) {
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
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!cfg.getBooleanOrFalse(qn_flash_as_pic)) return;
                    Object viewHolder = param.args[1];
                    if (viewHolder == null) return;
                    if (fBaseChatItemLayout == null) {
                        fBaseChatItemLayout = Utils.findField(viewHolder.getClass(), load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"), "a");
                        if (fBaseChatItemLayout == null) {
                            fBaseChatItemLayout = Utils.getFirstNSFFieldByType(viewHolder.getClass(), load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"));
                        }
                        fBaseChatItemLayout.setAccessible(true);
                    }
                    if (setTailMessage == null) {
                        setTailMessage = XposedHelpers.findMethodExact(load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"),
                                "setTailMessage", boolean.class, CharSequence.class, View.OnClickListener.class);
                        setTailMessage.setAccessible(true);
                    }
                    if (setTailMessage != null) {
                        Object baseChatItemLayout = fBaseChatItemLayout.get(viewHolder);
                        setTailMessage.invoke(baseChatItemLayout, isFlashPic(param.args[0]), "闪照", null);
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

    private static Field MsgRecord_msgtype = null;
    private static Method MsgRecord_getExtInfoFromExtStr = null;

    public static boolean isFlashPic(Object msgRecord) {
        try {
            if (MsgRecord_msgtype == null) {
                MsgRecord_msgtype = _MessageRecord().getField("msgtype");
                MsgRecord_msgtype.setAccessible(true);
            }
            if (MsgRecord_getExtInfoFromExtStr == null) {
                MsgRecord_getExtInfoFromExtStr = _MessageRecord().getMethod("getExtInfoFromExtStr", String.class);
                MsgRecord_getExtInfoFromExtStr.setAccessible(true);
            }
            int msgtype = (int) MsgRecord_msgtype.get(msgRecord);
            return (msgtype == -2000 || msgtype == -2006)
                    && !TextUtils.isEmpty((String) MsgRecord_getExtInfoFromExtStr.invoke(msgRecord, "commen_flash_pic"));
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_FLASH_PIC_HELPER), new DexDeobfStep(DexKit.C_BASE_PIC_DL_PROC), new DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_flash_as_pic, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}
