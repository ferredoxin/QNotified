/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.script.QNScriptEventBus;
import nil.nadph.qnotified.script.QNScriptManager;
import nil.nadph.qnotified.script.params.FriendMessageParam;
import nil.nadph.qnotified.script.params.ParamFactory;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

public class ScriptEventHook extends BaseDelayableHook {
    private static final ScriptEventHook self = new ScriptEventHook();
    private boolean inited = false;

    private ScriptEventHook() {
    }

    public static ScriptEventHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
        /*

            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.data.MessageForText"), "doParse", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    int istroop = (Integer) iget_object_or_null(param.thisObject, "istroop");
                    String uin = (String) iget_object_or_null(param.thisObject, "frienduin");
                    String msg = (String) iget_object_or_null(param.thisObject, "mMessageSource");
                    if (istroop != 1) {
                        QNScriptEventBus.onFriendMessage(ParamFactory.friendMessage()
                                .setContent(msg)
                                .setUin(uin)
                                .create());
                    } else {
                        String senderuin = (String) iget_object_or_null(param.thisObject, "senderuin");
                        QNScriptEventBus.onGroupMessage(ParamFactory.groupMessage()
                                .setContent(msg)
                                .setSenderUin(senderuin)
                                .setUin(uin)
                                .create());
                    }
                }
            });

         */
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }

    @Override
    public boolean checkPreconditions() {
        return true;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
