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
import nil.nadph.qnotified.mvc.base.AbsConfigSection;
import nil.nadph.qnotified.util.Const;

/**
 * A user-fronted function, which would be displayed in the function list user interface, and be
 * searchable. You should not copy, clone or new too much of them, because it should be Singleton.
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
     * @return optional search keywords if you are afraid user cannot find it with a name or
     * description.
     */
    @Nullable
    String[] getExtraSearchKeywords();

    /**
     * Whether function is compatible with current version QQ/TIM.
     *
     * @return true if this function is compatible with the current version of QQ/TIM
     */
    boolean isCompatible();

    /**
     * A HUMAN-READABLE String meaning which versions are compatible(NOT FOR NLP). Keep it short.
     * See {@link #isCompatible()}.
     *
     * @return human-readable, eg."QQ7.6.0-8.5.10, TIM>=2.3.0". May be null.
     */
    @Nullable
    String getCompatibleVersions();

    /**
     * @return Hook tasks which this function relies on. If no hook task is required, return {@link
     * BaseFunctionItem#EMPTY_HOOK_TASK}.
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
     * Please return false
     *
     * @return false
     */
    boolean isTodo();

    /**
     * For an average hook, return false if it works.
     *
     * @return whether this hook should be executed very early (discouraged, nearly when QQ/TIM
     * startup).
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
     * @return whether this function is CONFIGURED to be enabled
     */
    boolean isEnabled();

    /**
     * @param enabled whether this function is CONFIGURED to be enabled
     */
    void setEnabled(boolean enabled);

    /**
     * If you this function instance is not Proxy, just {@code return getClass().getName();},
     * otherwise return a constant unique string from your InvocationHandler.
     *
     * @return a unique but constant string id for this function
     */
    @NonNull
    String getUniqueIdentifier();

    // User Interface Stuff ----------------------------------

    /**
     * False if you want to hide the main switch, or its main switch is controlled by some other
     * condition.
     *
     * @return whether a main switch for this function should be shown to user
     */
    boolean isShowMainSwitch();

    /**
     * Optional SHORT text to show a status for current function.
     *
     * @return eg "1/4" or "3个群", may be null
     */
    @Nullable
    CharSequence getSummaryText();

    /**
     * This return value should correspond with {@link #createConfigSection()}. If false, a simplest
     * list item with an enable-disable switch will be used.
     *
     * @return whether this function has a detailed config section(user interface)
     */
    boolean hasConfigSection();

    /**
     * Create the detailed config user interface Retrieve the UI configuration that control what
     * will be displayed to user for this function.
     *
     * @return must NOT be null if {@link #hasConfigSection()} returns true, otherwise return null
     */
    @Nullable
    AbsConfigSection createConfigSection();

    // SMART Stuff -------------------------------------------

    /**
     * Retrieve a status for the current hook to tell framework controller whether this function is
     * working properly. This status usually comes from its hook task, but not always the case.
     *
     * @return current status for this function
     */
    @NonNull
    ErrorStatus getFunctionStatus();
}
