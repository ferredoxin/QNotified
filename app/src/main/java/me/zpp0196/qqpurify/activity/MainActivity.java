package me.zpp0196.qqpurify.activity;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import java.util.ArrayList;
import java.util.List;
import me.zpp0196.qqpurify.fragment.AboutPreferenceFragment;
import me.zpp0196.qqpurify.fragment.ChatPreferenceFragment;
import me.zpp0196.qqpurify.fragment.ExtensionPreferenceFragment;
import me.zpp0196.qqpurify.fragment.MainuiPreferenceFragment;
import me.zpp0196.qqpurify.fragment.ReadmeFragment;
import me.zpp0196.qqpurify.fragment.SettingPreferenceFragment;
import me.zpp0196.qqpurify.fragment.SidebarPreferenceFragment;
import me.zpp0196.qqpurify.fragment.TroopPreferenceFragment;
import me.zpp0196.qqpurify.utils.Constants;
import me.zpp0196.qqpurify.utils.ThemeUtils;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.AppCompatTransferActivity;

/**
 * Created by zpp0196 on 2019/5/15.
 */
public class MainActivity extends AppCompatTransferActivity implements
    ViewPager.OnPageChangeListener,
    OnTabSelectListener, Constants {

    public List<TabFragment> mRefreshedFragment = new ArrayList<>();
    private TextView mTitleTextView;
    private List<TabFragment> mFragments = new ArrayList<>();

    public static boolean hasAppCompatAttr(Context ctx) {
        TypedArray a = ctx.obtainStyledAttributes(R.styleable.AppCompatTheme);
        boolean hasVal = a.hasValue(R.styleable.AppCompatTheme_windowActionBar);
        a.recycle();
        return hasVal;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_Def);
        super.onCreate(savedInstanceState);
        setTheme(ThemeUtils.getStyleId(this));
        setContentView(R.layout.activity_main);
        initTabLayout();
        initToolbar();
    }

    private void initTabLayout() {
        mFragments.add(new MainuiPreferenceFragment());
        mFragments.add(new SidebarPreferenceFragment());
        mFragments.add(new ChatPreferenceFragment());
        mFragments.add(new TroopPreferenceFragment());
        mFragments.add(new ExtensionPreferenceFragment());
        mFragments.add(new ReadmeFragment());
        mFragments.add(new SettingPreferenceFragment());
        mFragments.add(new AboutPreferenceFragment());

        ViewPager viewPager = findViewById(R.id.viewPager);
        viewPager.setAdapter(new MainAdapter(getSupportFragmentManager(), mFragments));
        viewPager.addOnPageChangeListener(this);
        SlidingTabLayout slidingTabLayout = findViewById(R.id.slidingTabLayout);
        slidingTabLayout.setViewPager(viewPager);
        slidingTabLayout.setOnTabSelectListener(this);
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitleTextView = toolbar.findViewById(R.id.title);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        updateTitle(0);
    }

    private void updateTitle(int position) {
        String actionBarTitle = mFragments.get(position).getToolbarTitle();
        mTitleTextView.setText(actionBarTitle);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        updateTitle(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onTabSelect(int position) {
        updateTitle(position);
    }

    @Override
    public void onTabReselect(int position) {
        updateTitle(position);
    }

    public interface TabFragment {

        String getTabTitle();

        String getToolbarTitle();

        Fragment getFragment();
    }


    public static class MainAdapter extends FragmentPagerAdapter {

        private List<TabFragment> mFragmentList;

        MainAdapter(@NonNull FragmentManager fm, List<TabFragment> fragmentList) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.mFragmentList = fragmentList;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentList.get(position).getTabTitle();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position).getFragment();
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
