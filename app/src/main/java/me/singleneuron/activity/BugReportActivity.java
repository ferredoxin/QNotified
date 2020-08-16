package me.singleneuron.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.util.ArrayList;

import me.singleneuron.base.BaseBugReport;
import me.singleneuron.data.BugReportArguments;
import me.singleneuron.fragment.BugReportFragment;
import me.singleneuron.fragment.LoadingBugReportFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.AppCompatTransferActivity;
import nil.nadph.qnotified.databinding.ActivityBugReportBinding;

public class BugReportActivity extends AppCompatTransferActivity {

    private ActivityBugReportBinding binding;
    private BugReportFragment bugReportFragment;
    private LoadingBugReportFragment loadingBugReportFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBugReportBinding.inflate(getLayoutInflater());
        loadingBugReportFragment = new LoadingBugReportFragment();
        loadingBugReportFragment.setOnRetry(() -> thread.start());
        changeFragment(loadingBugReportFragment);
        thread.start();
        setContentView(binding.getRoot());
    }

    private void changeFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.bug_report_content_frame,fragment).addToBackStack(fragment.getClass().getSimpleName()).commit();
    }

    Thread thread = new Thread(){
        @Override
        public void run() {
            super.run();
            try {
                ArrayList<BugReportArguments> list = BaseBugReport.getInstance().getBugReportArgumentsList();
                runOnUiThread(()-> {
                    bugReportFragment = BugReportFragment.getInstance(list);
                    changeFragment(bugReportFragment);
                });
            } catch (Exception e) {
                loadingBugReportFragment.onError(e.toString());
                e.printStackTrace();
            }
        }
    };

}
