package me.zpp0196.qqpurify.fragment;

import android.app.AlertDialog;
import androidx.preference.Preference;
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
        findPreference("licenses").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AboutPreferenceFragment.this.showLicensesDialog();
                return false;
            }
        });
    }

    void showLicensesDialog() {
//        Notices notices = new Notices();
//        Notice commonsIo = new Notice("Apache Commons IO", "https://github.com/apache/commons-io", "Copyright 2002-2019 The Apache Software Foundation", new ApacheSoftwareLicense20());
//        Notice flycoTabLayout = new Notice("FlycoTabLayout", "https://github.com/H07000223/FlycoTabLayout", "Copyright (c) 2015 H07000223", new MITLicense());
//        Notice markwon = new Notice("Markwon", "https://github.com/noties/Markwon", "Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)", new ApacheSoftwareLicense20());
//        Notice colorPicker = new Notice("ColorPicker", "https://github.com/jaredrummler/ColorPicker", "Copyright 2016 Jared Rummler\nCopyright 2015 Daniel Nilsson", new ApacheSoftwareLicense20());
//        notices.addNotice(commonsIo);
//        notices.addNotice(flycoTabLayout);
//        notices.addNotice(markwon);
//        notices.addNotice(colorPicker);
//        new LicensesDialogFragment.Builder(mActivity)
//                .setNotices(notices)
//                .build()
//                .showNow(mActivity.getSupportFragmentManager(), "licenses-dialog");
        new AlertDialog.Builder(mActivity).setTitle("开放源代码许可")
                .setMessage("FlycoTabLayout\nCopyright (c) 2015 H07000223\n" +
                        "https://github.com/H07000223/FlycoTabLayout\n\n" +
                        "Markwon\nCopyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)\n" +
                        "https://github.com/noties/Markwon\n\n" +
                        "ColorPicker\nCopyright 2016 Jared Rummler\nCopyright 2015 Daniel Nilsson\n" +
                        "https://github.com/jaredrummler/ColorPicker\n\n" +
                        "这里本应使用 de.psdev.licensesdialog.LicensesDialogFragment 展示本程序使用的开源第三方库, " +
                        "但是因为执行 new WebView(mContext) 会导致本模块注入宿主的资源因重置而丢失(结果是整个插件" +
                        "连带宿主一同奔溃, 且二次注入后 obtainStyledAttributes() 仍然存在问题), 故先用此下策")
                .setPositiveButton(android.R.string.ok, null).show();
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
