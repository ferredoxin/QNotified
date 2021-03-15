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

import java.util.Objects;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.lifecycle.Parasitics;
import nil.nadph.qnotified.util.SavedInstanceStatePatchedClassReferencer;
import nil.nadph.qnotified.util.Utils;

/**
 * If you just want to create a MaterialDialog or AppCompatDialog,
 * see {@link #createMaterialDesignContext(Context)} and {@link #createAppCompatContext(Context)}
 **/
public class CommonContextWrapper extends ContextThemeWrapper {

    /**
     * Creates a new context wrapper with the specified theme with correct module ClassLoader.
     *
     * @param base  the base context
     * @param theme the resource ID of the theme to be applied on top of
     *              the base context's theme
     */
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
    public static Context applyNighMode(@NonNull Context base, int uiNightMode) {
        Objects.requireNonNull(base, "base is null");
        Configuration baseConfig = base.getResources().getConfiguration();
        if ((baseConfig.uiMode & Configuration.UI_MODE_NIGHT_MASK) == uiNightMode) {
            // config for base context is already what we want, just return
            return base;
        }
        try {
            Configuration conf = new Configuration();
            conf.uiMode = uiNightMode | (baseConfig.uiMode & ~Configuration.UI_MODE_NIGHT_MASK);
            return base.createConfigurationContext(conf);
        } catch (Exception e) {
            Utils.log(e);
            return base;
        }
    }

    @NonNull
    public static CommonContextWrapper createAppCompatContext(@NonNull Context base) {
        return new CommonContextWrapper(applyNighMode(base, ResUtils.getNightModeMasked()),
            R.style.Theme_AppCompat_DayNight);
    }

    @NonNull
    public static CommonContextWrapper createMaterialDesignContext(@NonNull Context base) {
        return new CommonContextWrapper(applyNighMode(base, ResUtils.getNightModeMasked()),
            R.style.Theme_MaiTungTMDesign);
    }
}
