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

package nil.nadph.qnotified.base.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.Objects;
import nil.nadph.qnotified.base.AbsFunctionItem;
import nil.nadph.qnotified.base.AbsHookTask;
import nil.nadph.qnotified.base.ErrorStatus;
import nil.nadph.qnotified.base.annotation.FunctionInfo;
import nil.nadph.qnotified.hook.AbsDelayableHook;
import nil.nadph.qnotified.mvc.base.AbsConfigSection;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Utils;

public class Delayable2SingleHookFuncItemProxy implements AbsFunctionItem, AbsHookTask {

    private final AbsDelayableHook h;
    private final String identifier;
    private final String functionName;
    private final CharSequence description;
    private ErrorStatus status = null;

    public Delayable2SingleHookFuncItemProxy(@NonNull AbsDelayableHook h) {
        this.h = Objects.requireNonNull(h);
        Class<?> clazz = h.getClass();
        String shortClassName = Utils.getShort$Name(clazz);
        identifier = shortClassName;
        FunctionInfo info = clazz.getAnnotation(FunctionInfo.class);
        if (info == null) {
            functionName = shortClassName;
            description = null;
        } else {
            functionName = info.name();
            String[] desc = info.description();
            if (desc.length > 0) {
                description = desc[0];
            } else {
                description = null;
            }
        }
    }

    @NonNull
    @Override
    public String getName() {
        return functionName;
    }

    @Nullable
    @Override
    public CharSequence getDescription() {
        return description;
    }

    @Nullable
    @Override
    public String[] getExtraSearchKeywords() {
        return null;
    }

    @Override
    public boolean isCompatible() {
        return h.isValid();
    }

    @Nullable
    @Override
    public String getCompatibleVersions() {
        return null;
    }

    @NonNull
    @Override
    public AbsHookTask[] getRequiredHooks() {
        return new AbsHookTask[]{this};
    }

    @Override
    public boolean isRuntimeHookSupported() {
        return true;
    }

    @Override
    public boolean isTodo() {
        return false;
    }

    @Override
    public boolean isNeedEarlyInit() {
        return false;
    }

    @Override
    public boolean hasEnableState() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return h.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        h.setEnabled(enabled);
    }

    @NonNull
    @Override
    public String getUniqueIdentifier() {
        return identifier;
    }

    @Override
    public boolean isShowMainSwitch() {
        return true;
    }

    @Nullable
    @Override
    public CharSequence getSummaryText() {
        return null;
    }

    @Override
    public boolean hasConfigSection() {
        return false;
    }

    @Nullable
    @Override
    public AbsConfigSection createConfigSection() {
        return null;
    }

    @NonNull
    @Override
    public ErrorStatus getFunctionStatus() {
        if (status == null) {
            return ErrorStatus.INACTIVE;
        } else {
            return status;
        }
    }

    @Override
    public String toString() {
        return h.toString() + "(proxy)";
    }

    @NonNull
    @Override
    public ErrorStatus getTaskStatus() {
        if (status == null) {
            return ErrorStatus.INACTIVE;
        } else {
            return status;
        }
    }

    @NonNull
    @Override
    public ErrorStatus execute() {
        if (status == null) {
            try {
                boolean ret = h.init();
                status = ret ? ErrorStatus.SUCCESS : ErrorStatus.FAILED;
            } catch (Throwable e) {
                status = ErrorStatus.FAILED(e.toString());
            }
        }
        return status;
    }

    @Override
    public boolean isExecuted() {
        return status != null;
    }

    @NonNull
    @Override
    public Step[] getPreparations() {
        return h.getPreconditions();
    }

    @Override
    public int getTargetProcess() {
        return h.getEffectiveProc();
    }
}
