package me.zpp0196.qqpurify.fragment;

import nil.nadph.qnotified.R;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class ChatPreferenceFragment extends AbstractPreferenceFragment {
    @Override
    protected int getPrefRes() {
        return R.xml.pref_chat;
    }

    @Override
    public String getTabTitle() {
        return "聊天";
    }

    @Override
    public String getToolbarTitle() {
        return "聊天界面";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_CHAT;
    }
}

