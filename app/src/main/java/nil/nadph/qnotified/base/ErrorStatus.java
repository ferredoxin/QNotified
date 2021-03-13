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
package nil.nadph.qnotified.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Status representing {@link AbsFunctionItem}, or {@link AbsHookTask}.
 */
public class ErrorStatus {

    /**
     * For {@link AbsFunctionItem}: This function is turned off, or is unavailable.<br/> For {@link
     * AbsHookTask}: This hook task is not executed yet.
     */
    public static final int STATUS_INACTIVE = 0;

    /**
     * For {@link AbsFunctionItem}: Rising edge when user turns on.<br/> For {@link AbsHookTask}:
     * The hook task is being executed.
     */
    public static final int STATUS_INITIALIZATION = 1;

    /**
     * For {@link AbsFunctionItem}: Turn on a function that is not available.<br/> For {@link
     * AbsHookTask}: The hook task is being executed before its preparations are ready. This value
     * should only be treated as an edge, not a constant level.
     */
    public static final int STATUS_REJECTED = 2;

    /**
     * For {@link AbsFunctionItem}: The function is on and working properly.<br/> For {@link
     * AbsHookTask}: The hook task has been executed successfully.
     */
    public static final int STATUS_SUCCESS = 3;

    /**
     * For {@link AbsFunctionItem}: The function working but sth may be wrong.<br/> For {@link
     * AbsHookTask}: The hook task has been executed with warning message.
     */
    public static final int STATUS_WARNING = 4;

    /**
     * For {@link AbsFunctionItem}: An error occurred so this function is unusable.<br/> For {@link
     * AbsHookTask}: The hook task execution failed.
     */
    public static final int STATUS_FAILED = 5;
    @NonNull
    public static final ErrorStatus INACTIVE = new ErrorStatus(STATUS_INACTIVE, null);
    @NonNull
    public static final ErrorStatus INITIALIZATION = new ErrorStatus(STATUS_INITIALIZATION, null);
    @NonNull
    public static final ErrorStatus REJECTED = new ErrorStatus(STATUS_REJECTED, null);
    @NonNull
    public static final ErrorStatus SUCCESS = new ErrorStatus(STATUS_SUCCESS, null);
    @NonNull
    public static final ErrorStatus WARNING = new ErrorStatus(STATUS_WARNING, null);
    @NonNull
    public static final ErrorStatus FAILED = new ErrorStatus(STATUS_FAILED, null);
    public final int status;
    /**
     * Optional message
     */
    @Nullable
    public final String message;

    public ErrorStatus(int status, @Nullable String msg) {
        this.status = status;
        this.message = msg;
    }

    @NonNull
    public static ErrorStatus SUCCESS(@Nullable String msg) {
        return new ErrorStatus(STATUS_SUCCESS, msg);
    }

    @NonNull
    public static ErrorStatus WARNING(@Nullable String msg) {
        return new ErrorStatus(STATUS_WARNING, msg);
    }

    @NonNull
    public static ErrorStatus FAILED(@Nullable String msg) {
        return new ErrorStatus(STATUS_FAILED, msg);
    }

    public boolean isInactive() {
        return status == STATUS_INACTIVE;
    }

    public boolean isSuccess() {
        return status == STATUS_SUCCESS;
    }

    public boolean isWarning() {
        return status == STATUS_WARNING;
    }

    public boolean isDone() {
        return status == STATUS_SUCCESS || status == STATUS_WARNING;
    }

    public boolean isFailed() {
        return status == STATUS_FAILED;
    }

    public boolean isRejected() {
        return status == STATUS_REJECTED;
    }
}
