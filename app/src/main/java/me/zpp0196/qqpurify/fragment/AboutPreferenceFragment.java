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

package me.zpp0196.qqpurify.fragment;

import cc.ioctl.H;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.Utils;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class AboutPreferenceFragment extends AbstractPreferenceFragment {

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initPreferences() {
        super.initPreferences();
        findPreference("version_module").setSummary(Utils.QN_VERSION_NAME);
        findPreference("version_qq").setTitle(H.getAppName());
        findPreference("version_qq")
            .setSummary(String.format("%s (%d)", H.getVersionName(), H.getVersionCode()));
    }

    @Override
    public int getPrefRes() {
        return R.xml.pref_about;
    }

    @Override
    public String getTabTitle() {
        return "关于";
    }

    @Override
    public String getToolbarTitle() {
        return "关于模块";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_ABOUT;
    }
}
