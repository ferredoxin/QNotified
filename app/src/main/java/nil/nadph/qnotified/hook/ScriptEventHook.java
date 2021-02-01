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

import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.script.QNScriptManager;

import static nil.nadph.qnotified.util.Utils.log;

public class ScriptEventHook extends CommonDelayableHook {
    private static final ScriptEventHook self = new ScriptEventHook();

    private ScriptEventHook() {
        super("__NOT_USED__", SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF);
    }

    public static ScriptEventHook get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        QNScriptManager.init();
        try {
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
    public boolean isEnabled() {
        return true;
    }
}
