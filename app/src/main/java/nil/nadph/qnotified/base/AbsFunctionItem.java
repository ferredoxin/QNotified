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

import nil.nadph.qnotified.mvc.InterfaceConfiguration;
import nil.nadph.qnotified.util.Const;

/**
 * A user-fronted function, which would be displayed in the function list
 * user interface, and be searchable.
 * You should not copy, clone or new too much of them, because it should be Singleton.
 */
public interface AbsFunctionItem {

    /**
     * Human-readable name that will be displayed to user, eg "隐藏下拉小程序"
     *
     * @return must NOT be null
     */
    @NonNull
    String getName();

    /**
     * Human-readable description for this function. This will be displayed to users.
     *
     * @return may be null
     */
    @Nullable
    CharSequence getDescription();

    /**
     * Under most circumstances, you just want to {@code return null;}.
     *
     * @return optional search keywords if you are afraid that user cannot find it with name or description.
     */
    @Nullable
    String[] getExtraSearchKeywords();

    /**
     * 这个选项用来表示该功能是不是已经写完了, 请不要用于显示是否兼容此版本QQ/TIM!
     * 还没开始写的功能 return false, 写完了 return true, 写完了不能用也 true.
     *
     * @return whether this function is available. If false, this function UI will turn grey and auto switch off.
     */
    boolean isAvailable();

    /**
     * See {@link #isAvailable()}.
     *
     * @return A human-readable CharSequence that represents why this function is unavailable. May be null.
     */
    @Nullable
    CharSequence getUnavailableReason();

    /**
     * @return Hook tasks which this function relies on. If no hook task is required,
     * return {@link BaseFunctionItem#EMPTY_HOOK_TASK}.
     */
    @Const
    @NonNull
    AbsHookTask[] getRequiredHooks();

    /**
     * If false returned, Toast "重启QQ生效" will be displayed to users.
     *
     * @return whether this hook supports runtime dynamic hook init.
     */
    boolean isRuntimeHookSupported();

    /**
     * For an average hook, return false if it works.
     *
     * @return whether this hook should be executed very early (discouraged, nearly when QQ/TIM startup).
     */
    boolean isNeedEarlyInit();

    // Enable/disable config ---------------------------

    /**
     * If your function is designed to be always on, return false.
     *
     * @return common practice is to return true
     */
    boolean hasEnableState();

    /**
     * @param enabled whether this function is CONFIGURED to be enabled
     */
    void setEnabled(boolean enabled);

    /**
     * @return whether this function is CONFIGURED to be enabled
     */
    boolean isEnabled();

    // User Interface Stuff ----------------------------------

    /**
     * Retrieve the UI configuration that control what will be displayed to user for this function.
     *
     * @return may be null, If null, a simplest list item with a enable/disable switch will be used.
     */
    @Nullable
    InterfaceConfiguration getInterfaceConfiguration();

    // SMART Stuff -------------------------------------------

    /**
     * Retrieve a status for current hook to tell framework controller whether this function is
     * working properly. This status usually comes from it's hook task, but not always the case.
     *
     * @return current status for this function
     */
    @NonNull
    BaseStatus getFunctionStatus();
}
