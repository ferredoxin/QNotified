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
package me.singleneuron.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import me.singleneuron.base.bridge.BugReport;
import me.singleneuron.data.BugReportArguments;
import me.singleneuron.fragment.BugReportFragment;
import me.singleneuron.fragment.LoadingBugReportFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.AppCompatTransferActivity;
import nil.nadph.qnotified.databinding.ActivityBugReportBinding;

public class BugReportActivity extends AppCompatTransferActivity {

    private ActivityBugReportBinding binding;
    BugReportFragment bugReportFragment;
    LoadingBugReportFragment loadingBugReportFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_DayNight);
        super.onCreate(savedInstanceState);
        setTitle("反馈");
        binding = ActivityBugReportBinding.inflate(getLayoutInflater());
        loadingBugReportFragment = new LoadingBugReportFragment();
        loadingBugReportFragment.setOnRetry(() -> new Thread(new runnable()).start());
        changeFragment(loadingBugReportFragment);
        setContentView(binding.getRoot());
    }

    void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.bug_report_content_frame, fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    private class runnable implements Runnable {

        @Override
        public void run() {
            try {
                ArrayList<BugReportArguments> list = BugReport.getInstance().getBugReportArgumentsList();
                runOnUiThread(() -> {
                    bugReportFragment = BugReportFragment.getInstance(list);
                    changeFragment(bugReportFragment);
                });
            } catch (Exception e) {
                loadingBugReportFragment.onError(e.toString());
                e.printStackTrace();
            }
        }
    }

}
