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

package nil.nadph.qnotified.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.lifecycle.Parasitics;
import nil.nadph.qnotified.util.SavedInstanceStatePatchedClassReferencer;
import nil.nadph.qnotified.util.Utils;

public class CommonContextWrapper extends ContextThemeWrapper {

    public CommonContextWrapper(@NonNull Context base, int theme) {
        super(base, theme);
        Parasitics.injectModuleResources(getResources());
    }

    private ClassLoader mXref = null;

    @NonNull
    @Override
    public ClassLoader getClassLoader() {
        if (mXref == null) {
            mXref = new SavedInstanceStatePatchedClassReferencer(CommonContextWrapper.class.getClassLoader());
        }
        return mXref;
    }

    @NonNull
    public static CommonContextWrapper createAppCompatContext(Context base) {
        if (base == null) {
            throw new NullPointerException("base is null");
        }
        try {
            int uiMode = ResUtils.isInNightMode() ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
            Configuration conf = new Configuration(base.getResources().getConfiguration());
            conf.uiMode = uiMode
                | (base.getResources().getConfiguration().uiMode & ~Configuration.UI_MODE_NIGHT_MASK);
            base = base.createConfigurationContext(conf);
        } catch (Exception e) {
            Utils.log(e);
        }
        return new CommonContextWrapper(base, R.style.Theme_AppCompat_DayNight);
    }
}
