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

import java.lang.reflect.*;

import cn.lliiooll.event.*;
import de.robv.android.xposed.*;
import me.singleneuron.data.*;
import nil.nadph.qnotified.hook.*;
import nil.nadph.qnotified.script.*;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Initiator.*;


public class ScriptEventHook extends CommonDelayableHook {
    private static final ScriptEventHook self = new ScriptEventHook();
    
    private ScriptEventHook() {
        super("qn_script_event_hook");
    }
    
    public static ScriptEventHook get() {
        return self;
    }
    
    @Override
    public boolean initOnce() {
        QNScriptManager.init();
        Class<?> clazz = _QQMessageFacade();
        for (Method m : clazz.getDeclaredMethods()) {
            Class<?>[] argt = m.getParameterTypes();
            if (argt.length == 1 && argt[0] == _MessageRecord()) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks || !isEnabled()) return;
                        Object msgRecord = param.args[0];
                        MsgRecordData data = new MsgRecordData(msgRecord);
                        if (data.isTroop() == 0) {
                            QNEventBus.broadcast(QNFriendMessageEvent.create(data));
                        }
                    }
                });
            }
        }
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
}
