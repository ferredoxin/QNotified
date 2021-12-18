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
package nil.nadph.qnotified.lifecycle;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.logi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.ui.activity.SettingActivity;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.MainProcess;

/**
 * Used to jump into module proxy Activities from external Intent
 *
 * @author cinit
 */
public class JumpActivityEntryHook {

    public static final String JUMP_ACTION_CMD = "qn_jump_action_cmd";
    public static final String JUMP_ACTION_TARGET = "qn_jump_action_target";
    public static final String JUMP_ACTION_START_ACTIVITY = "nil.nadph.qnotified.START_ACTIVITY";
    public static final String JUMP_ACTION_SETTING_ACTIVITY = "nil.nadph.qnotified.SETTING_ACTIVITY";
    public static final String JUMP_ACTION_REQUEST_SKIP_DIALOG = "nil.nadph.qnotified.REQUEST_SKIP_DIALOG";
    private static boolean __jump_act_init = false;

    @MainProcess
    @SuppressLint("PrivateApi")
    public static void initForJumpActivityEntry(Context ctx) {
        if (__jump_act_init) {
            return;
        }
        try {
            Class<?> clz = load("com.tencent.mobileqq.activity.JumpActivity");
            if (clz == null) {
                logi("class JumpActivity not found.");
                return;
            }
            Method doOnCreate = clz.getDeclaredMethod("doOnCreate", Bundle.class);
            XposedBridge.hookMethod(doOnCreate, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity activity = (Activity) param.thisObject;
                    Intent intent = activity.getIntent();
                    String cmd;
                    if (intent == null || (cmd = intent.getStringExtra(JUMP_ACTION_CMD)) == null) {
                        return;
                    }
                    if (JUMP_ACTION_SETTING_ACTIVITY.equals(cmd)) {
                        if (LicenseStatus.sDisableCommonHooks) {
                        } else {
                            Intent realIntent = new Intent(intent);
                            realIntent
                                .setComponent(new ComponentName(activity, SettingActivity.class));
                            activity.startActivity(realIntent);
                        }
                    } else if (JUMP_ACTION_START_ACTIVITY.equals(cmd)) {
                        String target = intent.getStringExtra(JUMP_ACTION_TARGET);
                        if (!TextUtils.isEmpty(target)) {
                            try {
                                Class<?> activityClass = Class.forName(target);
                                Intent realIntent = new Intent(intent);
                                realIntent.setComponent(new ComponentName(activity, activityClass));
                                activity.startActivity(realIntent);
                            } catch (Exception e) {
                                logi("Unable to start Activity: " + e.toString());
                            }
                        }
                    }
                }
            });
            __jump_act_init = true;
        } catch (Exception e) {
            log(e);
        }
    }
}
