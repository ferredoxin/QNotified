package me.singleneuron.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import nil.nadph.qnotified.databinding.FragmentLoadingBugReportBinding;
import nil.nadph.qnotified.util.Utils;

public class LoadingBugReportFragment extends Fragment {

    FragmentLoadingBugReportBinding binding;
    Runnable mRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLoadingBugReportBinding.inflate(inflater);
        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.textView.setText("加载中...");
                binding.textView2.setText("");
                binding.button.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                mRunnable.run();
            }
        });
        return binding.getRoot();
    }

    public void onError(String string) {
        Utils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textView.setText("加载失败");
                binding.textView2.setText(string);
                binding.button.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void setOnRetry(Runnable runnable) {
        mRunnable = runnable;
    }
}
