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
public class BaseStatus {

    /**
     * For {@link AbsFunctionItem}: This function is turned off, or is unavailable.<br/>
     * For {@link AbsHookTask}: This hook task is not executed yet.
     */
    public static final int STATUS_INACTIVE = 0;

    /**
     * For {@link AbsFunctionItem}: Rising edge when user turn on.<br/>
     * For {@link AbsHookTask}: The hook task is being executed.
     */
    public static final int STATUS_INITIALIZATION = 1;

    /**
     * For {@link AbsFunctionItem}: The function is on and working properly.<br/>
     * For {@link AbsHookTask}: The hook task has been executed successfully.
     */
    public static final int STATUS_SUCCESS = 2;

    /**
     * For {@link AbsFunctionItem}: The function working but sth may be wrong.<br/>
     * For {@link AbsHookTask}: The hook task has been executed with warning message.
     */
    public static final int STATUS_WARNING = 3;

    /**
     * For {@link AbsFunctionItem}: An error occurred so this function is unusable.<br/>
     * For {@link AbsHookTask}: The hook task execution failed.
     */
    public static final int STATUS_FAILED = 4;

    public final int status;

    /**
     * Optional message
     */
    @Nullable
    public final String message;

    public BaseStatus(int status, @Nullable String msg) {
        this.status = status;
        this.message = msg;
    }

    @NonNull
    public static final BaseStatus INACTIVE = new BaseStatus(STATUS_INACTIVE, null);

    @NonNull
    public static final BaseStatus INITIALIZATION = new BaseStatus(STATUS_INITIALIZATION, null);

    @NonNull
    public static final BaseStatus SUCCESS = new BaseStatus(STATUS_SUCCESS, null);

    @NonNull
    public static BaseStatus SUCCESS(@Nullable String msg) {
        return new BaseStatus(STATUS_SUCCESS, msg);
    }

    @NonNull
    public static final BaseStatus WARNING = new BaseStatus(STATUS_WARNING, null);

    @NonNull
    public static BaseStatus WARNING(@Nullable String msg) {
        return new BaseStatus(STATUS_WARNING, msg);
    }

    @NonNull
    public static final BaseStatus FAILED = new BaseStatus(STATUS_FAILED, null);

    @NonNull
    public static BaseStatus FAILED(@Nullable String msg) {
        return new BaseStatus(STATUS_FAILED, msg);
    }
}
