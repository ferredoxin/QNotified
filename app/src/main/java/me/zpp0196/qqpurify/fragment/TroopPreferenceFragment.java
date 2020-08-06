package me.zpp0196.qqpurify.fragment;

import nil.nadph.qnotified.R;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class TroopPreferenceFragment extends AbstractPreferenceFragment {
    @Override
    protected int getPrefRes() {
        return R.xml.pref_troop;
    }

    @Override
    public String getTabTitle() {
        return "群聊";
    }

    @Override
    public String getToolbarTitle() {
        return "群聊界面";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_TROOP;
    }
}
