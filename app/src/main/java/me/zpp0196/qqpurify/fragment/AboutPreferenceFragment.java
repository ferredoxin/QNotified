package me.zpp0196.qqpurify.fragment;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.preference.Preference;
import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import io.noties.markwon.Markwon;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.Utils;

import java.util.List;

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
        final Notices notices = new Notices();
//        Notice commonsIo = new Notice("Apache Commons IO", "https://github.com/apache/commons-io", "Copyright 2002-2019 The Apache Software Foundation", new ApacheSoftwareLicense20());
        Notice flycoTabLayout = new Notice("FlycoTabLayout", "https://github.com/H07000223/FlycoTabLayout", "Copyright (c) 2015 H07000223", new MITLicense());
        Notice markwon = new Notice("Markwon", "https://github.com/noties/Markwon", "Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)", new ApacheSoftwareLicense20());
        Notice colorPicker = new Notice("ColorPicker", "https://github.com/jaredrummler/ColorPicker", "Copyright 2016 Jared Rummler\nCopyright 2015 Daniel Nilsson", new ApacheSoftwareLicense20());
//        notices.addNotice(commonsIo);
        notices.addNotice(flycoTabLayout);
        notices.addNotice(markwon);
        notices.addNotice(colorPicker);
        notices.addNotice(LicensesDialog.LICENSES_DIALOG_NOTICE);
        //drop WebView, it will cause crash
        ListView listView = new ListView(mActivity);
        listView.setBackgroundDrawable(null);
        listView.setDivider(null);
        listView.setAdapter(new BaseAdapter() {
            final List<Notice> mNotices = notices.getNotices();
            final LayoutInflater inflater = LayoutInflater.from(mActivity);
            final Markwon markwon = Markwon.create(mActivity);

            @Override
            public int getCount() {
                return mNotices.size();
            }

            @Override
            public Object getItem(int position) {
                return mNotices.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView title;
                TextView licenseView;
                Notice notice = mNotices.get(position);
                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.simple_license_item, parent, false);
                }
                title = convertView.findViewById(R.id.sLicenseItem_title);
                licenseView = convertView.findViewById(R.id.sLicenseItem_licensePrev);
                markwon.setMarkdown(title, "- " + notice.getName() + "  \n(<" + notice.getUrl() + ">)");
                licenseView.setText(notice.getCopyright() + "\n\n" + notice.getLicense().getSummaryText(mActivity));
                return convertView;
            }
        });
//        new LicensesDialogFragment.Builder(mActivity)
//                .setNotices(notices)
//                .build()
//                .showNow(mActivity.getSupportFragmentManager(), "licenses-dialog");
////The execution of `new WenView(mActivity)` will cause the Resources reload, thus killing the host.
        new AlertDialog.Builder(mActivity).setTitle(de.psdev.licensesdialog.R.string.notices_title)
                .setView(listView).setCancelable(true)
                .setPositiveButton(de.psdev.licensesdialog.R.string.notices_close, null).show();
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
