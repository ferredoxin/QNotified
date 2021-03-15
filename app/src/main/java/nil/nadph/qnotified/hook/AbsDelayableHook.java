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
package nil.nadph.qnotified.hook;

import androidx.annotation.NonNull;
import nil.nadph.qnotified.config.SwitchConfigItem;
import nil.nadph.qnotified.controller.FunctionItemLoader;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Utils;

public abstract class AbsDelayableHook implements SwitchConfigItem {

    private int myId = -1;

    @NonNull
    public static AbsDelayableHook getHookByType(int hookId) {
        return queryDelayableHooks()[hookId];
    }

    @NonNull
    public static AbsDelayableHook[] queryDelayableHooks() {
        return FunctionItemLoader.enumerateFunctionItems();
    }

    public static void allowEarlyInit(@NonNull AbsDelayableHook hook) {
        if (hook == null) {
            return;
        }
        try {
            if (hook.isTargetProc() && hook.isEnabled() && hook.checkPreconditions() && !hook
                .isInited()) {
                hook.init();
            }
        } catch (Throwable e) {
            Utils.log(e);
        }
    }

    public abstract boolean isTargetProc();

    public abstract int getEffectiveProc();

    public abstract boolean isInited();

    public abstract boolean init();

    @Override
    public abstract boolean sync();

    @NonNull
    public abstract Step[] getPreconditions();

    @Override
    public abstract boolean isValid();

    public abstract boolean checkPreconditions();

    public final int getId() {
        if (myId != -1) {
            return myId;
        }
        AbsDelayableHook[] hooks = queryDelayableHooks();
        for (int i = 0; i < hooks.length; i++) {
            if (hooks[i].getClass().equals(getClass())) {
                myId = i;
                return myId;
            }
        }
        return -1;
    }

    /**
     * safe to call, no Throwable allowed
     *
     * @return whether the config item is enabled
     */
    @Override
    public abstract boolean isEnabled();

    /**
     * This method must be safe to call even if it is NOT inited
     *
     * @param enabled has no effect if isValid() returns false
     */
    @Override
    public abstract void setEnabled(boolean enabled);

}
