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
package cn.lliiooll.hook;

import cn.lliiooll.activity.MuteCfgActivity;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator._MessageRecord;
import static nil.nadph.qnotified.util.Initiator._QQMessageFacade;


public class MuteAllMsgHook extends CommonDelayableHook {
    private static final MuteAllMsgHook self = new MuteAllMsgHook();

    private MuteAllMsgHook() {
        super("qn_muteall_msg");
    }

    public static MuteAllMsgHook get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        Class<?> clazz = _QQMessageFacade();
        for (Method m : clazz.getDeclaredMethods()) {
            Class<?>[] argt = m.getParameterTypes();
            if (argt.length == 1 && argt[0] == _MessageRecord()) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks || !isEnabled()) return;
                        Object msgRecord = param.args[0];
                        if (isEnabled()) XposedHelpers.setBooleanField(msgRecord, "isread", true);
                    }
                });
            }
        }
        return true;
    }
}
