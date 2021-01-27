package me.singleneuron.activity;

import android.os.*;

import androidx.annotation.*;
import androidx.fragment.app.*;

import java.util.*;

import me.singleneuron.base.bridge.*;
import me.singleneuron.data.*;
import me.singleneuron.fragment.*;
import nil.nadph.qnotified.*;
import nil.nadph.qnotified.activity.*;

public class BugReportActivity extends AppCompatTransferActivity {
    
    BugReportFragment bugReportFragment;
    LoadingBugReportFragment loadingBugReportFragment;
    private nil.nadph.qnotified.databinding.ActivityBugReportBinding binding;
    
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_DayNight);
        super.onCreate(savedInstanceState);
        setTitle("反馈");
        binding = nil.nadph.qnotified.databinding.ActivityBugReportBinding.inflate(getLayoutInflater());
        loadingBugReportFragment = new LoadingBugReportFragment();
        loadingBugReportFragment.setOnRetry(() -> new Thread(new runnable()).start());
        changeFragment(loadingBugReportFragment);
        new Thread(new runnable()).start();
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
