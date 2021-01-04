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

import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.NonNull;

public abstract class BaseDelayableHook extends AbsDelayableHook {

    @Override
    public boolean isTargetProc() {
        return (getEffectiveProc() & SyncUtils.getProcessType()) != 0;
    }

    @Override
    public abstract int getEffectiveProc();

    @Override
    public abstract boolean isInited();

    @Override
    public abstract boolean init();

    @Override
    public boolean sync() {
        return true;
    }

    @NonNull
    @Override
    public abstract Step[] getPreconditions();

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public boolean checkPreconditions() {
        for (Step i : getPreconditions()) {
            if (!i.isDone()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + (isInited() ? "inited" : "") + "," + (isEnabled() ? "enabled" : "") + "," + SyncUtils.getProcessName() + ")";
    }

}
