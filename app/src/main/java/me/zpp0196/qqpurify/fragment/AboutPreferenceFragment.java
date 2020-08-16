package me.zpp0196.qqpurify.fragment;

import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.Utils;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class AboutPreferenceFragment extends AbstractPreferenceFragment {

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initPreferences() {
        super.initPreferences();
        String qqVersion = Utils.getHostInfo(mActivity).versionName;

        findPreference("version_module").setSummary(Utils.QN_VERSION_NAME);
        findPreference("version_qq").setSummary(qqVersion);
//moved to LicenseActivity
//        findPreference("licenses").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                AboutPreferenceFragment.this.showLicensesDialog();
//                return false;
//            }
//        });
    }

    @Override
    protected int getPrefRes() {
        return R.xml.pref_about;
    }

    @Override
    public String getTabTitle() {
        return "关于";
    }

    @Override
    public String getToolbarTitle() {
        return "关于模块";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_ABOUT;
    }
}
