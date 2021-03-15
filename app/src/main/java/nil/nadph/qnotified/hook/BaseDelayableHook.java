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
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.AbsFunctionItem;
import nil.nadph.qnotified.base.internal.Delayable2SingleHookFuncItemProxy;
import nil.nadph.qnotified.base.internal.IFunctionItemInterface;
import nil.nadph.qnotified.step.Step;


public abstract class BaseDelayableHook extends AbsDelayableHook implements IFunctionItemInterface {

    private AbsFunctionItem mStub = null;

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
        return getClass().getSimpleName() + "(" + (isInited() ? "inited" : "") + "," + (isEnabled()
            ? "enabled" : "") + "," + SyncUtils.getProcessName() + ")";
    }

    @NonNull
    @Override
    public AbsFunctionItem asFunctionItem() {
        if (mStub == null) {
            mStub = new Delayable2SingleHookFuncItemProxy(this);
        }
        return mStub;
    }
}
