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
package nil.nadph.qnotified.mvc.base;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

/**
 * Only one AbsConfigSection can be attached to a StyledUiProvider at a time.
 */
public interface AbsConfigSection {

    /**
     * Called when this config section is to be created.
     *
     * @param ui The ui you may use
     * @return unused
     */
    boolean onAttach(@NonNull SharedUiProvider ui);

    /**
     * Called before this config section is to be destroyed.
     *
     * @param ui The ui you may use
     */
    void onDetach(@NonNull SharedUiProvider ui);

    /**
     * See {@link Fragment#onPause()}
     *
     * @param ui The ui you may use
     */
    void onPause(@NonNull SharedUiProvider ui);

    /**
     * See {@link Fragment#onResume()}
     *
     * @param ui The ui you may use
     */
    void onResume(@NonNull SharedUiProvider ui);

    /**
     * Cope with it when user press back button with the unsaved config<br/> Return true to
     * intercept the exit event, and you usually want to show a dialog to ask user whether to save
     * the unsaved config. Return false to finish the config section. This exit event usually comes
     * from {@link Activity#onBackPressed()} etc.
     *
     * @param ui The ui you may use
     * @return whether this exit event should be intercepted
     */
    boolean onInterceptExitEvent(@NonNull SharedUiProvider ui);

    /**
     * See {@link Activity#onActivityResult(int, int, Intent)}.
     *
     * @param ui          The ui you may use
     * @param requestCode The integer request code originally supplied to startActivityForResult(),
     *                    allowing you to identify who this result came from.
     * @param resultCode  The integer result code returned by the child activity through its
     *                    setResult().
     * @param data        An Intent, which can return result data to the caller (various data can be
     *                    attached to Intent "extras").
     */
    @SuppressWarnings("JavadocReference")
    void onActivityResult(@NonNull SharedUiProvider ui, int requestCode, int resultCode,
        Intent data);
}
