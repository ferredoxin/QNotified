package me.zpp0196.qqpurify.fragment;

import me.zpp0196.qqpurify.fragment.base.*;
import nil.nadph.qnotified.*;

/**
 * Created by zpp0196 on 2019/5/15.
 */
public class MainuiPreferenceFragment extends AbstractPreferenceFragment {
    @Override
    protected int getPrefRes() {
        return R.xml.pref_mainui;
    }
    
    @Override
    public String getTabTitle() {
        return "主页";
    }
    
    @Override
    public String getToolbarTitle() {
        return "主界面";
    }
    
    @Override
    public String getSettingGroup() {
        return SETTING_MAINUI;
    }
}
