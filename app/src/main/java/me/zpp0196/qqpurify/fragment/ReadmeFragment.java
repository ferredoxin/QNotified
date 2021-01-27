package me.zpp0196.qqpurify.fragment;

import android.annotation.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.*;

import io.noties.markwon.*;
import me.zpp0196.qqpurify.activity.*;
import me.zpp0196.qqpurify.utils.*;
import nil.nadph.qnotified.R;

/**
 * Created by zpp0196 on 2019/5/16.
 */
public class ReadmeFragment extends Fragment implements MainActivity.TabFragment {
    
    @Nullable
    @Override
    @SuppressLint("InflateParams")
    @SuppressWarnings("ConstantConditions")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
