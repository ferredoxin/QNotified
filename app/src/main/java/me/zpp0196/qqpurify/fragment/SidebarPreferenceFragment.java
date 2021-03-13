package me.zpp0196.qqpurify.fragment;

import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import nil.nadph.qnotified.R;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class SidebarPreferenceFragment extends AbstractPreferenceFragment {

    @Override
    protected int getPrefRes() {
        return R.xml.pref_sidebar;
    }

    @Override
    public String getTabTitle() {
        return "侧滑";
    }

    @Override
    public String getToolbarTitle() {
        return "侧滑栏和设置";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_SIDEBAR;
    }
}
