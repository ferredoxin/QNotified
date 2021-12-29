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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.noties.markwon.Markwon;
import me.zpp0196.qqpurify.activity.MainActivity;
import me.zpp0196.qqpurify.utils.Utils;
import nil.nadph.qnotified.R;

/**
 * Created by zpp0196 on 2019/5/16.
 */
public class ReadmeFragment extends Fragment implements MainActivity.TabFragment {

    @Nullable
    @Override
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        ScrollView rootView = (ScrollView) inflater.inflate(R.layout.fragment_readme, null);
        TextView readme = rootView.findViewById(R.id.readme);
        Markwon markwon = Markwon.create(getActivity());
        markwon.setMarkdown(readme, Utils.getTextFromAssets(getActivity(), "readme.md"));
        return rootView;
    }

    @Override
    public String getTabTitle() {
        return "说明";
    }

    @Override
    public String getToolbarTitle() {
        return "使用说明";
    }

    @Override
    public Fragment getFragment() {
        return this;
    }
}
