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
package nil.nadph.qnotified.util;

import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.regex.Pattern;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;
import nil.nadph.qnotified.config.ConfigManager;

import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.Utils.*;

/**
 * What the fuck?
 * I hadn't obfuscated the source code.
 * I just don't want to name it,
 * leaving it a()
 */
@SuppressWarnings("rawtypes")
public class DexKit {

    //WARN: NEVER change the index!
    public static final int C_DIALOG_UTIL = 1;
    public static final int C_FACADE = 2;
    public static final int C_FLASH_PIC_HELPER = 3;
    public static final int C_BASE_PIC_DL_PROC = 4;
    public static final int C_ITEM_BUILDER_FAC = 5;
    public static final int C_AIO_UTILS = 6;
    public static final int C_ABS_GAL_SCENE = 7;
    //public static final int C_FAV_EMO_ROAM_HANDLER = 8;
    public static final int C_FAV_EMO_CONST = 9;
    public static final int C_MSG_REC_FAC = 10;
    public static final int C_CONTACT_UTILS = 11;
    public static final int C_VIP_UTILS = 12;
    public static final int C_ARK_APP_ITEM_BUBBLE_BUILDER = 13;
    public static final int C_PNG_FRAME_UTIL = 14;
    public static final int C_PIC_EMOTICON_INFO = 15;
    public static final int C_SIMPLE_UI_UTIL = 16;
    public static final int C_TROOP_GIFT_UTIL = 17;
    public static final int C_TEST_STRUCT_MSG = 18;
    public static final int C_QZONE_MSG_NOTIFY = 19;
    public static final int C_APP_CONSTANTS = 20;
    public static final int C_CustomWidgetUtil = 21;
    public static final int C_MessageCache = 22;
    public static final int C_ScreenShotHelper = 23;
    public static final int C_TimeFormatterUtils = 24;
    public static final int C_TogetherControlHelper = 25;
    //unknown class name
    public static final int C_GroupAppActivity = 26;
    //the last index
    public static final int DEOBF_NUM_C = 26;

    public static final int N_BASE_CHAT_PIE__INIT = 20001;
    public static final int N_BASE_CHAT_PIE__handleNightMask = 20002;
    public static final int N_BASE_CHAT_PIE__updateSession = 20003;
    public static final int DEOBF_NUM_N = 3;


    @Nullable
    public static boolean prepareFor(int i) {
        if (i / 10000 == 0) {
            return doFindClass(i) != null;
        } else {
            return doFindMethod(i) != null;
        }
    }

    @Nullable
    public static boolean checkFor(int i) {
        if (i / 10000 == 0) {
            return loadClassFromCache(i) != null;
        } else {
            return getMethodDescFromCache(i) != null;
        }
    }

    @Nullable
    public static Class loadClassFromCache(int i) {
        Class ret = Initiator.load(c(i));
        if (ret != null) return ret;
        DexMethodDescriptor m = getMethodDescFromCache(i);
        if (m == null) return null;
        return Initiator.load(m.declaringClass);
    }

    @Nullable
    public static Class doFindClass(int i) {
        Class ret = Initiator.load(c(i));
        if (ret != null) return ret;
        DexMethodDescriptor m = getMethodDescFromCache(i);
        if (m == null) m = doFindMethodDesc(i);
        if (m == null) return null;
        return Initiator.load(m.declaringClass);
    }

    @Nullable
    public static Method getMethodFromCache(int i) {
        if (i / 10000 == 0)
            throw new IllegalStateException("Index " + i + " attempted to access method!");
        DexMethodDescriptor m = getMethodDescFromCache(i);
        if (m == null) return null;
        if (m.name.equals("<init>") || m.name.equals("<clinit>")) {
            logi("getMethodFromCache(" + i + ") methodName == " + m.name + " , return null");
            return null;
        }
        try {
            return m.getMethodInstance(Initiator.getHostClassLoader());
        } catch (NoSuchMethodException e) {
            log(e);
            return null;
        }
    }

    @Nullable
    public static Method doFindMethod(int i) {
        if (i / 10000 == 0)
            throw new IllegalStateException("Index " + i + " attempted to access method!");
        DexMethodDescriptor m = doFindMethodDesc(i);
        if (m == null) return null;
        if (m.name.equals("<init>") || m.name.equals("<clinit>")) {
            logi("doFindMethod(" + i + ") methodName == " + m.name + " , return null");
            return null;
        }
        try {
            return m.getMethodInstance(Initiator.getHostClassLoader());
        } catch (NoSuchMethodException e) {
            log(e);
            return null;
        }
    }

    @Nullable
    public static DexMethodDescriptor getMethodDescFromCache(int i) {
        try {
            ConfigManager cache = ConfigManager.getCache();
            int lastVersion = cache.getIntOrDefault("cache_" + a(i) + "_code", 0);
            if (getHostVersionCode32() != lastVersion) {
                return null;
            }
            String name = cache.getString("cache_" + a(i) + "_method");
            if (name != null && name.length() > 0) {
                return new DexMethodDescriptor(name);
            }
            return null;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    @Nullable
    public static DexMethodDescriptor doFindMethodDesc(int i) {
        DexMethodDescriptor ret = getMethodDescFromCache(i);
        if (ret != null) return ret;
        int ver = -1;
        try {
            ver = getHostVersionCode32();
        } catch (Throwable ignored) {
        }
        try {
            HashSet<DexMethodDescriptor> methods;
            ConfigManager cache = ConfigManager.getCache();
            DexDeobfReport report = new DexDeobfReport();
            report.target = i;
            report.version = ver;
            methods = e(i, report);
            if (methods == null || methods.size() == 0) {
                report.v("No method candidate found.");
                logi("Unable to deobf: " + c(i));
                return null;
            }
            report.v(methods.size() + " method(s) found: " + methods);
            if (methods.size() == 1) {
                ret = methods.iterator().next();
            } else {
                ret = a(i, methods, report);
            }
            report.v("Final decision:" + (ret == null ? null : ret.toString()));
            cache.putString("deobf_log_" + a(i), report.toString());
            if (ret == null) {
                logi("Multiple classes candidates found, none satisfactory.");
                return null;
            }
            cache.putString("cache_" + a(i) + "_method", ret.toString());
            cache.getAllConfig().put("cache_" + a(i) + "_code", getHostVersionCode32());
            cache.save();
        } catch (Exception e) {
            log(e);
        }
        return ret;
    }


    public static String a(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return "dialog_util";
            case C_FACADE:
                return "facade";
            case C_FLASH_PIC_HELPER:
                return "flash_helper";
            case C_BASE_PIC_DL_PROC:
                return "base_pic_dl_proc";
            case C_ITEM_BUILDER_FAC:
                return "item_builder_fac";
            case C_AIO_UTILS:
                return "aio_utils";
            case C_ABS_GAL_SCENE:
                return "abs_gal_sc";
            case C_FAV_EMO_CONST:
                return "fav_emo_const";
            case C_MSG_REC_FAC:
                return "msg_rec_fac";
            case C_CONTACT_UTILS:
                return "contact_utils";
            case C_VIP_UTILS:
                return "vip_utils";
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return "ark_app_item_bubble_builder";
            case C_PNG_FRAME_UTIL:
                return "png_frame_util";
            case C_PIC_EMOTICON_INFO:
                return "pic_emoticon_info";
            case C_SIMPLE_UI_UTIL:
                return "simple_ui_util";
            case C_TROOP_GIFT_UTIL:
                return "troop_gift_util";
            case C_TEST_STRUCT_MSG:
                return "test_struct_msg";
            case C_QZONE_MSG_NOTIFY:
                return "qzone_msg_notify";
            case C_APP_CONSTANTS:
                return "app_constants";
            case C_CustomWidgetUtil:
                return "CustomWidgetUtil";
            case C_MessageCache:
                return "MessageCache";
            case C_ScreenShotHelper:
                return "ScreenShotHelper";
            case C_TimeFormatterUtils:
                return "TimeFormatterUtils";
            case C_TogetherControlHelper:
                return "TogetherControlHelper";
            case C_GroupAppActivity:
                return "GroupAppActivity";
            case N_BASE_CHAT_PIE__INIT:
                return "base_chat_pie__init";
            case N_BASE_CHAT_PIE__handleNightMask:
                return "base_chat_pie__handleNightMask";
            case N_BASE_CHAT_PIE__updateSession:
                return "base_chat_pie__updateSession";
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM_C);
    }

    public static String c(int i) {
        String ret;
        switch (i) {
            case C_DIALOG_UTIL:
                ret = "com/tencent/mobileqq/utils/DialogUtil";
                break;
            case C_FACADE:
                ret = "com/tencent/mobileqq/activity/ChatActivityFacade";
                break;
            case C_FLASH_PIC_HELPER:
                ret = "com.tencent.mobileqq.app.FlashPicHelper";
                break;
            case C_BASE_PIC_DL_PROC:
                ret = "com/tencent/mobileqq/transfile/BasePicDownloadProcessor";
                break;
            case C_ITEM_BUILDER_FAC:
                ret = "com/tencent/mobileqq/activity/aio/item/ItemBuilderFactory";
                break;
            case C_AIO_UTILS:
                ret = "com.tencent.mobileqq.activity.aio.AIOUtils";
                break;
            case C_ABS_GAL_SCENE:
                ret = "com/tencent/common/galleryactivity/AbstractGalleryScene";
                break;
            case C_FAV_EMO_CONST:
                ret = "com/tencent/mobileqq/emosm/favroaming/FavEmoConstant";
                break;
            case C_MSG_REC_FAC:
                ret = "com/tencent/mobileqq/service/message/MessageRecordFactory";
                break;
            case C_CONTACT_UTILS:
                ret = "com/tencent/mobileqq/utils/ContactUtils";
                break;
            case C_VIP_UTILS:
                ret = "com/tencent/mobileqq/utils/VipUtils";
                break;
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                ret = "com/tencent/mobileqq/activity/aio/item/ArkAppItemBubbleBuilder";
                break;
            case C_PNG_FRAME_UTIL:
                ret = "com.tencent.mobileqq.magicface.drawable.PngFrameUtil";
                break;
            case C_PIC_EMOTICON_INFO:
                ret = "com.tencent.mobileqq.emoticonview.PicEmoticonInfo";
                break;
            case C_SIMPLE_UI_UTIL:
                //dummy, placeholder, just a guess
                ret = "com.tencent.mobileqq.theme.SimpleUIUtil";
                break;
            case C_TROOP_GIFT_UTIL:
                ret = "com/tencent/mobileqq/troop/utils/TroopGiftUtil";
                break;
            case C_TEST_STRUCT_MSG:
                ret = "com/tencent/mobileqq/structmsg/TestStructMsg";
                break;
            case C_QZONE_MSG_NOTIFY:
                ret = "cooperation/qzone/push/MsgNotification";
                break;
            case C_APP_CONSTANTS:
                ret = "com.tencent.mobileqq.app.AppConstants";
                break;
            case C_CustomWidgetUtil:
                ret = "com.tencent.widget.CustomWidgetUtil";
                break;
            case C_MessageCache:
                ret = "com/tencent/mobileqq/service/message/MessageCache";
                break;
            case C_ScreenShotHelper:
                ret = "com.tencent.mobileqq.screendetect.ScreenShotHelper";
                break;
            case C_TimeFormatterUtils:
                ret = "com.tencent.mobileqq.utils.TimeFormatterUtils";
                break;
            case C_TogetherControlHelper:
                //guess
                ret = "com.tencent.mobileqq.together.TogetherControlHelper";
                break;
            case C_GroupAppActivity:
                //unknown
                ret = "com.tencent.mobileqq.IntimateRelationship";
                break;
            case N_BASE_CHAT_PIE__INIT:
            case N_BASE_CHAT_PIE__handleNightMask:
            case N_BASE_CHAT_PIE__updateSession:
                ret = _BaseChatPie().getName();
                break;
            default:
                ret = null;
        }
        if (ret != null) {
            return ret.replace("/", ".");
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM_C);
    }

    public static byte[][] b(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new byte[][]{new byte[]{0x1B, 0x61, 0x6E, 0x64, 0x72, 0x6F, 0x69, 0x64, 0x2E, 0x70, 0x65, 0x72, 0x6D, 0x69, 0x73, 0x73, 0x69, 0x6F, 0x6E, 0x2E, 0x53, 0x45, 0x4E, 0x44, 0x5F, 0x53, 0x4D, 0x53}};
            case C_FACADE:
                return new byte[][]{new byte[]{0x20, 0x72, 0x65, 0x53, 0x65, 0x6E, 0x64, 0x45, 0x6D, 0x6F}};
            case C_FLASH_PIC_HELPER:
                return new byte[][]{new byte[]{0x0E, 0x46, 0x6C, 0x61, 0x73, 0x68, 0x50, 0x69, 0x63, 0x48, 0x65, 0x6C, 0x70, 0x65, 0x72}};
            case C_BASE_PIC_DL_PROC:
                return new byte[][]{new byte[]{0x2C, 0x42, 0x61, 0x73, 0x65, 0x50, 0x69, 0x63, 0x44, 0x6F, 0x77, 0x6E, 0x6C}};
            case C_ITEM_BUILDER_FAC:
                return new byte[][]{
                        new byte[]{0x24, 0x49, 0x74, 0x65, 0x6D, 0x42, 0x75, 0x69, 0x6C, 0x64, 0x65, 0x72, 0x20, 0x69, 0x73, 0x3A, 0x20, 0x44},
                        new byte[]{0x2A, 0x66, 0x69, 0x6E, 0x64, 0x49, 0x74, 0x65, 0x6D, 0x42, 0x75, 0x69, 0x6C, 0x64, 0x65, 0x72, 0x3A, 0x20, 0x69, 0x6E, 0x76, 0x6F, 0x6B, 0x65, 0x64, 0x2E}
                };
            case C_AIO_UTILS:
                return new byte[][]{new byte[]{0x0D, 0x6F, 0x70, 0x65, 0x6E, 0x41, 0x49, 0x4F, 0x20, 0x62, 0x79, 0x20, 0x4D, 0x54}};
            case C_ABS_GAL_SCENE:
                return new byte[][]{new byte[]{0x16, 0x67, 0x61, 0x6C, 0x6C, 0x65, 0x72, 0x79, 0x20, 0x73, 0x65, 0x74, 0x43, 0x6F, 0x6C, 0x6F, 0x72, 0x20, 0x62, 0x6C}};
            case C_FAV_EMO_CONST:
                return new byte[][]{
                        new byte[]{0x11, 0x68, 0x74, 0x74, 0x70, 0x3A, 0x2F, 0x2F, 0x70, 0x2E, 0x71, 0x70, 0x69, 0x63, 0x2E},
                        new byte[]{0x12, 0x68, 0x74, 0x74, 0x70, 0x73, 0x3A, 0x2F, 0x2F, 0x70, 0x2E, 0x71, 0x70, 0x69, 0x63, 0x2E},
                };
            case C_MSG_REC_FAC:
                return new byte[][]{new byte[]{0x2C, 0x63, 0x72, 0x65, 0x61, 0x74, 0x65, 0x50, 0x69, 0x63, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65}};
            case C_CONTACT_UTILS:
                return new byte[][]{new byte[]{0x07, 0x20, 0x2D, 0x20, 0x57, 0x69, 0x46, 0x69}};
            case C_VIP_UTILS:
                return new byte[][]{new byte[]{0x05, 0x6A, 0x68, 0x61, 0x6E, 0x5F}};
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return new byte[][]{new byte[]{0x0F, 0x64, 0x65, 0x62, 0x75, 0x67, 0x41, 0x72, 0x6B, 0x4D, 0x65, 0x74, 0x61, 0x20, 0x3D, 0x20}};
            case C_PNG_FRAME_UTIL:
                return new byte[][]{new byte[]{0x2A, 0x66, 0x75, 0x6E, 0x63, 0x20, 0x63, 0x68, 0x65, 0x63, 0x6B, 0x52, 0x61, 0x6E, 0x64, 0x6F, 0x6D, 0x50, 0x6E, 0x67, 0x45, 0x78}};
            case C_PIC_EMOTICON_INFO:
                return new byte[][]{new byte[]{0x20, 0x73, 0x65, 0x6E, 0x64, 0x20, 0x65, 0x6D, 0x6F, 0x74, 0x69, 0x6F, 0x6E, 0x20, 0x2B, 0x20, 0x31, 0x3A}};
            case C_SIMPLE_UI_UTIL:
                return new byte[][]{new byte[]{0x15, 0x6B, 0x65, 0x79, 0x5F, 0x73, 0x69, 0x6D, 0x70, 0x6C, 0x65, 0x5F, 0x73, 0x74, 0x61, 0x74, 0x75, 0x73, 0x5F, 0x73}};
            case C_TROOP_GIFT_UTIL:
                return new byte[][]{new byte[]{0x1A, 0x2E, 0x74, 0x72, 0x6F, 0x6F, 0x70, 0x2E, 0x73, 0x65, 0x6E, 0x64, 0x5F, 0x67, 0x69, 0x66, 0x74, 0x54}};
            case C_TEST_STRUCT_MSG:
                return new byte[][]{new byte[]{0x0D, 0x54, 0x65, 0x73, 0x74, 0x53, 0x74, 0x72, 0x75, 0x63, 0x74, 0x4D, 0x73, 0x67}};
            case C_QZONE_MSG_NOTIFY:
                return new byte[][]{new byte[]{0x14, 0x75, 0x73, 0x65, 0x20, 0x73, 0x6D, 0x61, 0x6C, 0x6C, 0x20, 0x69, 0x63, 0x6F, 0x6E, 0x20, 0x2C, 0x65, 0x78, 0x70, 0x3A}};
            case C_APP_CONSTANTS:
                return new byte[][]{new byte[]{0x0B, 0x2E, 0x69, 0x6E, 0x64, 0x69, 0x76, 0x41, 0x6E, 0x69, 0x6D, 0x2F}};
            case C_MessageCache:
                return new byte[][]{new byte[]{0x12, 0x51, 0x2E, 0x6D, 0x73, 0x67, 0x2E, 0x4D, 0x65, 0x73, 0x73, 0x61, 0x67, 0x65, 0x43, 0x61, 0x63, 0x68, 0x65}};
            case C_ScreenShotHelper:
                return new byte[][]{new byte[]{0x1D, 0x6F, 0x6E, 0x41, 0x63, 0x74, 0x69, 0x76, 0x69, 0x74, 0x79, 0x52, 0x65, 0x73, 0x75, 0x6D, 0x65, 0x48, 0x69, 0x64, 0x65, 0x46, 0x6C, 0x6F, 0x61, 0x74, 0x56, 0x69, 0x65, 0x77}};
            case C_TimeFormatterUtils:
                return new byte[][]{new byte[]{0x12, 0x54, 0x69, 0x6D, 0x65, 0x46, 0x6F, 0x72, 0x6D, 0x61, 0x74, 0x74, 0x65, 0x72, 0x55, 0x74, 0x69, 0x6C, 0x73}};
            case C_TogetherControlHelper:
                return new byte[][]{new byte[]{0x16, 0x53, 0x49, 0x4E, 0x47, 0x20, 0x74, 0x6F, 0x67, 0x65, 0x74, 0x68, 0x65, 0x72, 0x20, 0x69, 0x73, 0x20, 0x63, 0x6C, 0x69, 0x63, 0x6B}};
            case C_GroupAppActivity:
                return new byte[][]{new byte[]{0x11, 0x6F, 0x6E, 0x44, 0x72, 0x61, 0x77, 0x65, 0x72, 0x53, 0x74, 0x61, 0x72, 0x74, 0x4F, 0x70, 0x65, 0x6E}};
            case N_BASE_CHAT_PIE__INIT:
                return new byte[][]{new byte[]{0x0F, 0x69, 0x6E, 0x70, 0x75, 0x74, 0x20, 0x73, 0x65, 0x74, 0x20, 0x65, 0x72, 0x72, 0x6F, 0x72}};
            case N_BASE_CHAT_PIE__handleNightMask:
                return new byte[][]{new byte[]{0x2D, 0x23, 0x68, 0x61, 0x6E, 0x64, 0x6C, 0x65, 0x4E, 0x69, 0x67, 0x68, 0x74, 0x4D, 0x61, 0x73, 0x6B, 0x23, 0x20, 0x3A, 0x20, 0x69, 0x6E, 0x4E, 0x69, 0x67, 0x68, 0x74, 0x4D, 0x6F, 0x64, 0x65}};
            case N_BASE_CHAT_PIE__updateSession:
                return new byte[][]{new byte[]{0x19, 0x41, 0x49, 0x4F, 0x54, 0x69, 0x6D, 0x65, 0x20, 0x75, 0x70, 0x64, 0x61, 0x74, 0x65, 0x53, 0x65, 0x73, 0x73, 0x69, 0x6F, 0x6E, 0x20, 0x65, 0x6E, 0x64}};
            case C_CustomWidgetUtil:
                return new byte[][]{new byte[]{0x03, 0x4E, 0x45, 0x57, 0x00}};

        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM_C);
    }

    public static int[] d(int i) {
        switch (i) {
            case C_DIALOG_UTIL:
                return new int[]{1, 4, 3};
            case C_FACADE:
                return new int[]{2, 6, 3};
            case C_FLASH_PIC_HELPER:
                return new int[]{1, 3};
            case C_BASE_PIC_DL_PROC:
                return new int[]{4, 7, 2};
            case C_ITEM_BUILDER_FAC:
                return new int[]{11, 6, 1};
            case C_AIO_UTILS:
                return new int[]{2, 11, 6};
            case C_ABS_GAL_SCENE:
                return new int[]{1};
            case C_FAV_EMO_CONST:
                return new int[]{3, 4, 5};
            case C_MSG_REC_FAC:
                return new int[]{4};
            case C_CONTACT_UTILS:
                return new int[]{4};
            case C_VIP_UTILS:
                return new int[]{4, 2, 3};
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                return new int[]{2, 11, 6};
            case C_PNG_FRAME_UTIL:
                return new int[]{3, 2};
            case C_PIC_EMOTICON_INFO:
                return new int[]{3, 4};
            case C_SIMPLE_UI_UTIL:
                return new int[]{4, 2};
            case C_TROOP_GIFT_UTIL:
                return new int[]{4, 9, 2};
            case C_TEST_STRUCT_MSG:
                return new int[]{4, 7, 2};
            case C_QZONE_MSG_NOTIFY:
                return new int[]{4, 3};
            case C_APP_CONSTANTS:
                return new int[]{1};
            case C_MessageCache:
                return new int[]{1, 4};
            case C_ScreenShotHelper:
                return new int[]{4, 2};
            case C_TimeFormatterUtils:
                return new int[]{1, 4};
            case C_TogetherControlHelper:
                return new int[]{2, 11, 6};
            case C_GroupAppActivity:
                return new int[]{2, 11, 6};
            case N_BASE_CHAT_PIE__INIT:
            case N_BASE_CHAT_PIE__handleNightMask:
            case N_BASE_CHAT_PIE__updateSession:
                return new int[]{7, 6, 3};
            case C_CustomWidgetUtil:
                return new int[]{5, 4, 9};
        }
        throw new IndexOutOfBoundsException("No class index for " + i + ",max = " + DEOBF_NUM_C);
    }

    private static DexMethodDescriptor a(int i, HashSet<DexMethodDescriptor> __methods, DexDeobfReport report) {
        switch (i) {
            case C_DIALOG_UTIL:
            case C_FACADE:
            case C_AIO_UTILS:
            case C_CONTACT_UTILS:
            case C_MSG_REC_FAC:
            case C_VIP_UTILS:
            case C_SIMPLE_UI_UTIL:
            case C_TROOP_GIFT_UTIL:
            case C_TEST_STRUCT_MSG:
            case C_TimeFormatterUtils:
                a:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    return m;
                }
                break;
            case C_FLASH_PIC_HELPER:
                a:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    if (clz.getDeclaredMethods().length > 8) continue;
                    return m;
                }
                break;
            case C_BASE_PIC_DL_PROC:
                for (DexMethodDescriptor md : __methods) {
                    Class clz = Initiator.load(md.declaringClass);
                    for (Field f : clz.getDeclaredFields()) {
                        int m = f.getModifiers();
                        if (Modifier.isStatic(m) && Modifier.isFinal(m) && f.getType().equals(Pattern.class))
                            return md;
                    }
                }
                break;
            case C_ITEM_BUILDER_FAC:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (clz.getDeclaredFields().length > 30) return m;
                }
                break;
            case C_ABS_GAL_SCENE:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (!Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (f.getType().equals(View.class))
                            return m;
                    }
                }
                break;
            case C_FAV_EMO_CONST:
                a:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    if (clz.getDeclaredMethods().length > 3) continue;
                    return m;
                }
                break;
            case C_ARK_APP_ITEM_BUBBLE_BUILDER:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class sp = clz.getSuperclass();
                    if (Object.class.equals(sp)) continue;
                    if (!Modifier.isAbstract(sp.getModifiers())) continue;
                    if (sp.getName().contains("Builder")) return m;
                    return m;
                }
                break;
            case C_PNG_FRAME_UTIL:
                for (DexMethodDescriptor md : __methods) {
                    Class clz = Initiator.load(md.declaringClass);
                    for (Method m : clz.getMethods()) {
                        if (m.getName().equals("b")) continue;
                        if (!m.getReturnType().equals(int.class)) continue;
                        if (!Modifier.isStatic(m.getModifiers())) continue;
                        Class[] argt = m.getParameterTypes();
                        if (argt.length == 1 && int.class.equals(argt[0])) return md;
                    }
                    return md;
                }
                break;
            case C_PIC_EMOTICON_INFO:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class s = clz.getSuperclass();
                    if (Object.class.equals(s)) continue;
                    s = s.getSuperclass();
                    if (Object.class.equals(s)) continue;
                    s = s.getSuperclass();
                    if (Object.class.equals(s)) return m;
                }
                break;
            case C_QZONE_MSG_NOTIFY:
                for (DexMethodDescriptor md : __methods) {
                    Class clz = Initiator.load(md.declaringClass);
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    Class s = clz.getSuperclass();
                    if (!Object.class.equals(s)) continue;
                    for (Method m : clz.getDeclaredMethods()) {
                        if (!m.getReturnType().equals(void.class)) continue;
                        Class<?>[] argt = m.getParameterTypes();
                        if (argt.length > 7 && argt[0].equals(_QQAppInterface())) {
                            return md;
                        }
                    }
                }
                break;
            case C_APP_CONSTANTS:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (!Modifier.isInterface(clz.getModifiers())) continue;
                    if (clz.getDeclaredFields().length < 50) continue;
                    return m;
                }
                break;
            case N_BASE_CHAT_PIE__INIT:
            case N_BASE_CHAT_PIE__handleNightMask:
            case N_BASE_CHAT_PIE__updateSession:
                for (DexMethodDescriptor m : __methods) {
                    if (m.declaringClass.replace('/', '.').contains(_BaseChatPie().getName()))
                        return m;
                }
                break;
            case C_CustomWidgetUtil:
                a:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (clz.isEnum()) continue;
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    if (Object.class != clz.getSuperclass()) continue;
                    for (Field f : clz.getDeclaredFields()) {
                        if (!Modifier.isStatic(f.getModifiers())) continue a;
                    }
                    return m;
                }
                break;
            case C_MessageCache:
                for (DexMethodDescriptor m : __methods) {
                    if ("<clinit>".equals(m.name)) {
                        return m;
                    }
                }
                break;
            case C_ScreenShotHelper:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (clz.isEnum()) continue;
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    if (Object.class != clz.getSuperclass()) continue;
                    return m;
                }
                break;
            case C_TogetherControlHelper:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (clz.isEnum()) continue;
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    if (Object.class != clz.getSuperclass()) continue;
                    return m;
                }
                break;
            case C_GroupAppActivity:
                for (DexMethodDescriptor m : __methods) {
                    Class clz = Initiator.load(m.declaringClass);
                    if (clz.isEnum()) continue;
                    if (Modifier.isAbstract(clz.getModifiers())) continue;
                    if (clz.getSuperclass() == Object.class) continue;
                    return m;
                }
                break;
        }
        return null;
    }

    public static boolean p(int i) {
        if (i == C_CustomWidgetUtil || i == N_BASE_CHAT_PIE__INIT) {
            return true;
        }
        return false;
    }

    @Nullable
    private static HashSet<DexMethodDescriptor> e(int i, DexDeobfReport rep) {
        ClassLoader loader = Initiator.getHostClassLoader();
        int record = 0;
        int[] qf = d(i);
        byte[][] keys = b(i);
        boolean check = p(i);
        if (qf != null) for (int dexi : qf) {
            record |= 1 << dexi;
            try {
                for (byte[] k : keys) {
                    HashSet<DexMethodDescriptor> ret = findMethodsByConstString(k, dexi, loader);
                    if (ret != null && ret.size() > 0) {
                        if (check) {
                            DexMethodDescriptor m = a(i, ret, rep);
                            if (m != null) {
                                ret.clear();
                                ret.add(m);
                                return ret;
                            }
                        } else {
                            return ret;
                        }
                    }
                }
            } catch (FileNotFoundException ignored) {
            }
        }
        int dexi = 1;
        while (true) {
            if ((record & (1 << dexi)) != 0) {
                dexi++;
                continue;
            }
            try {
                for (byte[] k : keys) {
                    HashSet<DexMethodDescriptor> ret = findMethodsByConstString(k, dexi, loader);
                    if (ret != null && ret.size() > 0) {
                        if (check) {
                            DexMethodDescriptor m = a(i, ret, rep);
                            if (m != null) {
                                ret.clear();
                                ret.add(m);
                                return ret;
                            }
                        } else {
                            return ret;
                        }
                    }
                }
            } catch (FileNotFoundException ignored) {
                return null;
            }
            dexi++;
        }
    }

    @Nullable
    public static byte[] getClassDeclaringDex(String klass, @Nullable int[] qf) {
        ClassLoader loader = Initiator.getHostClassLoader();
        int record = 0;
        if (qf != null) for (int dexi : qf) {
            record |= 1 << dexi;
            try {
                String name;
                byte[] buf = new byte[4096];
                byte[] content;
                if (dexi == 1) name = "classes.dex";
                else name = "classes" + dexi + ".dex";
                HashSet<URL> urls = new HashSet<>(3);
                try {
                    Enumeration<URL> eu;
                    eu = (Enumeration<URL>) invoke_virtual(loader, "findResources", name, String.class);
                    if (eu != null) {
                        while (eu.hasMoreElements()) {
                            urls.add(eu.nextElement());
                        }
                    }
                } catch (Throwable e) {
                    log(e);
                }
                if (!loader.getClass().equals(PathClassLoader.class) && !loader.getClass().equals(DexClassLoader.class)
                        && loader.getParent() != null) {
                    try {
                        Enumeration<URL> eu;
                        eu = (Enumeration<URL>) invoke_virtual(loader.getParent(), "findResources", name, String.class);
                        if (eu != null) {
                            while (eu.hasMoreElements()) {
                                urls.add(eu.nextElement());
                            }
                        }
                    } catch (Throwable e) {
                        log(e);
                    }
                }
                if (urls.size() == 0) throw new FileNotFoundException(name);
                InputStream in;
                try {
                    for (URL url : urls) {
                        in = url.openStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int ii;
                        while ((ii = in.read(buf)) != -1) {
                            baos.write(buf, 0, ii);
                        }
                        in.close();
                        content = baos.toByteArray();
                        if (DexFlow.hasClassInDex(content, klass)) {
                            return content;
                        }
                    }
                } catch (IOException e) {
                    log(e);
                    return null;
                }
            } catch (FileNotFoundException ignored) {
            }
        }
        int dexi = 1;
        while (true) {
            if ((record & (1 << dexi)) != 0) {
                dexi++;
                continue;
            }
            try {
                String name;
                byte[] buf = new byte[4096];
                byte[] content;
                if (dexi == 1) name = "classes.dex";
                else name = "classes" + dexi + ".dex";
                HashSet<URL> urls = new HashSet<>(3);
                try {
                    Enumeration<URL> eu;
                    eu = (Enumeration<URL>) invoke_virtual(loader, "findResources", name, String.class);
                    if (eu != null) {
                        while (eu.hasMoreElements()) {
                            urls.add(eu.nextElement());
                        }
                    }
                } catch (Throwable e) {
                    log(e);
                }
                if (!loader.getClass().equals(PathClassLoader.class) && !loader.getClass().equals(DexClassLoader.class)
                        && loader.getParent() != null) {
                    try {
                        Enumeration<URL> eu;
                        eu = (Enumeration<URL>) invoke_virtual(loader.getParent(), "findResources", name, String.class);
                        if (eu != null) {
                            while (eu.hasMoreElements()) {
                                urls.add(eu.nextElement());
                            }
                        }
                    } catch (Throwable e) {
                        log(e);
                    }
                }
                if (urls.size() == 0) throw new FileNotFoundException(name);
                InputStream in;
                try {
                    for (URL url : urls) {
                        in = url.openStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        int ii;
                        while ((ii = in.read(buf)) != -1) {
                            baos.write(buf, 0, ii);
                        }
                        in.close();
                        content = baos.toByteArray();
                        if (DexFlow.hasClassInDex(content, klass)) {
                            return content;
                        }
                    }
                } catch (IOException e) {
                    log(e);
                    return null;
                }
            } catch (FileNotFoundException ignored) {
                return null;
            }
            dexi++;
        }
    }

    public static ArrayList<Integer> a(byte[] buf, byte[] target) {
        ArrayList<Integer> rets = new ArrayList<>();
        int[] ret = new int[1];
        ret[0] = DexFlow.arrayIndexOf(buf, target, 0, buf.length);
        ret[0] = DexFlow.arrayIndexOf(buf, DexFlow.int2u4le(ret[0]), 0, buf.length);
        //System.out.println(ret[0]);
        int strIdx = (ret[0] - DexFlow.readLe32(buf, 0x3c)) / 4;
        if (strIdx > 0xFFFF) {
            target = DexFlow.int2u4le(strIdx);
        } else target = DexFlow.int2u2le(strIdx);
        int off = 0;
        while (true) {
            off = DexFlow.arrayIndexOf(buf, target, off + 1, buf.length);
            if (off == -1) break;
            if (buf[off - 2] == (byte) 26/*Opcodes.OP_CONST_STRING*/
                    || buf[off - 2] == (byte) 27)/* Opcodes.OP_CONST_STRING_JUMBO*/ {
                ret[0] = off - 2;
                int opcodeOffset = ret[0];
                if (buf[off - 2] == (byte) 27 && strIdx < 0x10000) {
                    if (DexFlow.readLe32(buf, opcodeOffset + 2) != strIdx) continue;
                }
                rets.add(opcodeOffset);
            }
        }
        return rets;
    }

    /**
     * get ALL the possible class names
     *
     * @param key    the pattern
     * @param i      C_XXXX
     * @param loader to get dex file
     * @return ["abc","ab"]
     * @throws FileNotFoundException apk has no classesN.dex
     */
    public static HashSet<DexMethodDescriptor> findMethodsByConstString(byte[] key, int i, ClassLoader loader) throws FileNotFoundException {
        String name;
        byte[] buf = new byte[4096];
        byte[] content;
        if (i == 1) name = "classes.dex";
        else name = "classes" + i + ".dex";
        HashSet<URL> urls = new HashSet<>(3);
        try {
            Enumeration<URL> eu;
            eu = (Enumeration<URL>) invoke_virtual(loader, "findResources", name, String.class);
            if (eu != null) {
                while (eu.hasMoreElements()) {
                    urls.add(eu.nextElement());
                }
            }
        } catch (Throwable e) {
            log(e);
        }
        if (!loader.getClass().equals(PathClassLoader.class) && !loader.getClass().equals(DexClassLoader.class)
                && loader.getParent() != null) {
            try {
                Enumeration<URL> eu;
                eu = (Enumeration<URL>) invoke_virtual(loader.getParent(), "findResources", name, String.class);
                if (eu != null) {
                    while (eu.hasMoreElements()) {
                        urls.add(eu.nextElement());
                    }
                }
            } catch (Throwable e) {
                log(e);
            }
        }
        //log("dex" + i + ":" + url);
        if (urls.size() == 0) throw new FileNotFoundException(name);
        InputStream in;
        try {
            HashSet<DexMethodDescriptor> rets = new HashSet<>();
            for (URL url : urls) {
                in = url.openStream();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                int ii;
                while ((ii = in.read(buf)) != -1) {
                    baos.write(buf, 0, ii);
                }
                in.close();
                content = baos.toByteArray();
				/*if (i == 1) {
					log("dex" + i + ".len :" + content.length);
				}*/
                ArrayList<Integer> opcodeOffsets = a(content, key);
                for (int j = 0; j < opcodeOffsets.size(); j++) {
                    try {
                        DexMethodDescriptor desc = DexFlow.getDexMethodByOpOffset(content, opcodeOffsets.get(j), true);
                        if (desc != null) rets.add(desc);
                    } catch (InternalError ignored) {
                    }
                }
            }
            return rets;
        } catch (IOException e) {
            log(e);
            return null;
        }
    }

    public static class DexDeobfReport {
        int target;
        int version;
        String result;
        String log;
        long time;

        public DexDeobfReport() {
            time = System.currentTimeMillis();
        }

        public void v(String str) {
            if (log == null) log = str + "\n";
            else log = log + str + "\n";
        }

        @Override
        public String toString() {
            return "Deobf target: " + target + '\n' +
                    "Time: " + time + '\n' +
                    "Version code: " + version + '\n' +
                    "Result: " + result + '\n' +
                    log;
        }
    }

}
