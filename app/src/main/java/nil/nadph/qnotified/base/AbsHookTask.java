/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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
package nil.nadph.qnotified.base;

import androidx.annotation.NonNull;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Const;
import nil.nadph.qnotified.util.NoThrow;

/**
 * The hook task which will get executed once at most, when plugin starts up or when user switches a
 * function on. NOTICE: A hook task will be executed at most ONCE, if user enable-disable-enable a
 * function, the task will only get executed ONCE then the function is enable at the first time.
 * This hook task is meant for lazy-init hook so that application won't run too slowly, and this
 * task MUST NOT be time-consuming because it will be executed on the UI Thread, if you want to
 * execute a time-consuming task, see {@link Step}.
 */
public interface AbsHookTask {

    /**
     * If this task has not been executed, {@link ErrorStatus#INACTIVE} will be returned.
     *
     * @return current status for this task.
     */
    @NonNull
    ErrorStatus getTaskStatus();

    /**
     * Execute this task now, wait it to finish, and get the result. If this task has been executed,
     * then it should NOT be executed again.
     *
     * @return status when(after) this task is executed.
     */
    @NonNull
    @NoThrow
    ErrorStatus execute();

    /**
     * @return whether this task has been executed
     */
    boolean isExecuted();

    /**
     * Sth related to {@link nil.nadph.qnotified.util.DexKit}. <br/> if there are no steps, return
     * {@link BaseHookTask#EMPTY_STEPS}.
     *
     * @return time-consuming steps that should be done BEFORE this task is executed.
     */
    @Const
    @NonNull
    Step[] getPreparations();

    /**
     * On which processes this hook task should be executed. If current process does not match the
     * target process, the task will NOT be executed.
     *
     * @return process ids, see {@link SyncUtils#isTargetProcess(int)}.
     */
    @Const
    int getTargetProcess();
}
