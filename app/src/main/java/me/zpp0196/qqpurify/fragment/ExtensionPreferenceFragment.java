package me.zpp0196.qqpurify.fragment;

import nil.nadph.qnotified.R;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class ExtensionPreferenceFragment extends AbstractPreferenceFragment {
    @Override
    protected int getPrefRes() {
        return R.xml.pref_extension;
    }

    @Override
    public String getTabTitle() {
        return "扩展";
    }

    @Override
    public String getToolbarTitle() {
        return "扩展功能";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_EXTENSION;
    }
}
