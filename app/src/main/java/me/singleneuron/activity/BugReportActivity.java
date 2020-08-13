package me.singleneuron.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import me.singleneuron.fragment.BugReportFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.AppCompatTransferActivity;
import nil.nadph.qnotified.databinding.ActivityBugReportBinding;

public class BugReportActivity extends AppCompatTransferActivity {

    private ActivityBugReportBinding binding;
    private BugReportFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBugReportBinding.inflate(getLayoutInflater());
        fragment = new BugReportFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.bug_report_content_frame,fragment).addToBackStack(BugReportFragment.class.getSimpleName()).commit();
        setContentView(binding.getRoot());
    }
}
