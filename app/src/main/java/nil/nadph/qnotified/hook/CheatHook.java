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

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;

import java.util.*;

import de.robv.android.xposed.*;
import me.singleneuron.util.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.step.*;
import nil.nadph.qnotified.ui.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class CheatHook extends BaseDelayableHook {
    
    public static final String qh_random_cheat = "qh_random_cheat";
    private static final CheatHook self = new CheatHook();
    private final String[] diceItem = {"1", "2", "3", "4", "5", "6"};
    private final String[] morraItem = {"石头", "剪刀", "布"};
    private boolean inited = false;
    
    private int diceNum = -1;
    private int morraNum = -1;
    
    private CheatHook() {
    }
    
    public static CheatHook get() {
        return self;
    }
    
    @Override
    public boolean init() {
        if (inited)
            return true;
        try {
            XposedHelpers.findAndHookMethod(DexKit.doFindClass(DexKit.C_PNG_FRAME_UTIL), "a", int.class, new XC_MethodHook(43) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks)
                        return;
                    try {
                        if (!isEnabled())
                            return;
                    } catch (Throwable e) {
                        log(e);
                    }
                    int num = (int) param.args[0];
                    if (num == 6) {
                        if (diceNum == -1) {
                            Utils.showErrorToastAnywhere("diceNum/E unexpected -1");
                        } else {
                            param.setResult(diceNum);
                        }
                    } else if (num == 3) {
                        if (morraNum == -1) {
                            Utils.showErrorToastAnywhere("morraNum/E unexpected -1");
                        } else {
                            param.setResult(morraNum);
                        }
                    }
                }
            });
            
            String fuckingMethod = "a";
            
            if (Utils.getHostVersionCode() >= QQVersion.QQ_8_4_8) {
                fuckingMethod = "sendMagicEmoticon";
            }
            if (Utils.getHostVersionCode() >= QQVersion.QQ_8_5_0) {
                XposedHelpers.findAndHookMethod(Class.forName("com.tencent.mobileqq.emoticonview" +
                        ".sender.PicEmoticonInfoSender"),
                    fuckingMethod, load("com.tencent.mobileqq.app.QQAppInterface"),
                    Context.class, _SessionInfo(), load("com.tencent.mobileqq.data.Emoticon"),
                    load("com.tencent.mobileqq.emoticon.EmojiStickerManager$StickerInfo"),
                    new XC_MethodHook(43) {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks)
                                return;
                            try {
                                if (!isEnabled())
                                    return;
                            } catch (Throwable e) {
                                log(e);
                            }
                            Context context = (Context) param.args[1];
                            Object emoticon = param.args[3];
                            String name = (String) XposedHelpers.getObjectField(emoticon,
                                "name");
                            if ("随机骰子".equals(name) || "骰子".equals(name)) {
                                param.setResult(null);
                                showDiceDialog(context, param);
                            } else if ("猜拳".equals(name)) {
                                param.setResult(null);
                                showMorraDialog(context, param);
                            }
                        }
                    });
            } else {
                XposedHelpers.findAndHookMethod(DexKit.doFindClass(DexKit.C_PIC_EMOTICON_INFO),
                    fuckingMethod, load("com.tencent.mobileqq.app.QQAppInterface"),
                    Context.class, _SessionInfo(), load("com.tencent.mobileqq.data.Emoticon"),
                    load("com.tencent.mobileqq.emoticon.EmojiStickerManager$StickerInfo"),
                    new XC_MethodHook(43) {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks)
                                return;
                            try {
                                if (!isEnabled())
                                    return;
                            } catch (Throwable e) {
                                log(e);
                            }
                            Context context = (Context) param.args[1];
                            Object emoticon = param.args[3];
                            String name = (String) XposedHelpers.getObjectField(emoticon,
                                "name");
                            if ("随机骰子".equals(name) || "骰子".equals(name)) {
                                param.setResult(null);
                                showDiceDialog(context, param);
                            } else if ("猜拳".equals(name)) {
                                param.setResult(null);
                                showMorraDialog(context, param);
                            }
                        }
                    });
            }
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
        
    }
    
    private void showDiceDialog(Context context, XC_MethodHook.MethodHookParam param) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, CustomDialog.themeIdForDialog())
            .setTitle("自定义骰子")
            .setSingleChoiceItems(diceItem, diceNum, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    diceNum = which;
                }
            })
            .setNegativeButton("取消", null)
            .setNeutralButton("随机", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    diceNum = Math.abs(new Random().nextInt(6));
                    try {
                        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            })
            .create();
        alertDialog.show();
    }
    
    private void showMorraDialog(Context context, XC_MethodHook.MethodHookParam param) {
        AlertDialog alertDialog = new AlertDialog.Builder(context, CustomDialog.themeIdForDialog())
            .setTitle("自定义猜拳")
            .setSingleChoiceItems(morraItem, morraNum, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    morraNum = which;
                }
            })
            .setNegativeButton("取消", null)
            .setNeutralButton("随机", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    morraNum = Math.abs(new Random().nextInt(3));
                    try {
                        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            })
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                    } catch (Exception e) {
                        XposedBridge.log(e);
                    }
                }
            })
            .create();
        alertDialog.show();
    }
    
    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_PNG_FRAME_UTIL), new DexDeobfStep(DexKit.C_PIC_EMOTICON_INFO)};
    }
    
    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }
    
    @Override
    public boolean isInited() {
        return inited;
    }
    
    @Override
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app))
                return false;
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qh_random_cheat);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qh_random_cheat, enabled);
            mgr.save();
        } catch (Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "",
                            Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }
}
