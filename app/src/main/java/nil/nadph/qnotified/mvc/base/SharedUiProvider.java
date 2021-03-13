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
package nil.nadph.qnotified.mvc.base;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import nil.nadph.qnotified.util.UiThread;

/**
 * An abstract user interface which you can control to interact with user. Implementation of this
 * interface may be a Fragment, (part of) an Activity, a Dialog or even a commandline interface. One
 * StyledUiProvider can only attach one AbsConfigSection at a time.
 */
public interface SharedUiProvider {

    /**
     * When in `inline` mode, title may get ignored
     *
     * @return the title of this config section, may be null
     */
    @Nullable
    String getTitle();

    /**
     * Get the current title for this config section.
     *
     * @param title the title of this config section, may be null if you want to hide the title
     */
    @UiThread
    void setTitle(@Nullable String title);

    /**
     * Notice: direct interaction with {@link android.view.View} is discouraged.
     *
     * @return may not be an activity
     */
    @NonNull
    Context getContext();

    /**
     * @hide
     */
    @Nullable
    AbsConfigSection getCurrentSection();

}
