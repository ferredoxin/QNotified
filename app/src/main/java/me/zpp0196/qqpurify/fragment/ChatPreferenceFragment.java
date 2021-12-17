package me.zpp0196.qqpurify.fragment;

import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import nil.nadph.qnotified.R;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class ChatPreferenceFragment extends AbstractPreferenceFragment {

    @Override
    public int getPrefRes() {
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

