/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tencent.widget.XListView;

import java.util.List;

import de.psdev.licensesdialog.LicensesDialog;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.GnuLesserGeneralPublicLicense3;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;
import io.noties.markwon.Markwon;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.ui.ResUtils;

public class LicenseActivity extends IphoneTitleBarActivityCompat implements View.OnClickListener {

    @Override
    public boolean doOnCreate(Bundle bundle) {
        super.doOnCreate(bundle);

        final Notices notices = new Notices();
//        Notice commonsIo = new Notice("Apache Commons IO", "https://github.com/apache/commons-io", "Copyright 2002-2019 The Apache Software Foundation", new ApacheSoftwareLicense20());
        Notice flycoTabLayout = new Notice("FlycoTabLayout", "https://github.com/H07000223/FlycoTabLayout", "Copyright (c) 2015 H07000223", new MITLicense());
        Notice markwon = new Notice("Markwon", "https://github.com/noties/Markwon", "Copyright 2017 Dimitry Ivanov (mail@dimitryivanov.ru)", new ApacheSoftwareLicense20());
        Notice colorPicker = new Notice("ColorPicker", "https://github.com/jaredrummler/ColorPicker", "Copyright 2016 Jared Rummler\nCopyright 2015 Daniel Nilsson", new ApacheSoftwareLicense20());
//        notices.addNotice(commonsIo);
        notices.addNotice(new Notice("QQ净化", "https://github.com/zpp0196/QQPurify", "zpp0196", new ApacheSoftwareLicense20()));
        notices.addNotice(new Notice("NoApplet", "https://github.com/Alcatraz323/noapplet", "Alcatraz323", new MITLicense()));
        notices.addNotice(new Notice("原生音乐通知", "https://github.com/singleNeuron/XposedMusicNotify", "singleNeuron", new GnuLesserGeneralPublicLicense3()));
        notices.addNotice(flycoTabLayout);
        notices.addNotice(markwon);
        notices.addNotice(colorPicker);
        notices.addNotice(LicensesDialog.LICENSES_DIALOG_NOTICE);

        BaseAdapter mAdapter = new BaseAdapter() {
            final List<Notice> mNotices = notices.getNotices();
            final LayoutInflater inflater = LayoutInflater.from(LicenseActivity.this);
            final Markwon markwon = Markwon.create(LicenseActivity.this);

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
                    title = convertView.findViewById(R.id.sLicenseItem_title);
                    licenseView = convertView.findViewById(R.id.sLicenseItem_licensePrev);
                    title.setTextColor(ResUtils.skin_black);
                    licenseView.setTextColor(ResUtils.skin_black);
                    licenseView.setTypeface(Typeface.MONOSPACE);
                    title.setHighlightColor(ResUtils.skin_blue.getDefaultColor());
                } else {
                    title = convertView.findViewById(R.id.sLicenseItem_title);
                    licenseView = convertView.findViewById(R.id.sLicenseItem_licensePrev);
                }
                markwon.setMarkdown(title, "- " + notice.getName() + "  \n(<" + notice.getUrl() + ">)");
                licenseView.setText(notice.getCopyright() + "\n\n" + notice.getLicense().getSummaryText(LicenseActivity.this));
                return convertView;
            }
        };

        XListView licenseListView = new XListView(this, null);
        licenseListView.setId(R.id.rootMainList);
        licenseListView.setDivider(null);
        licenseListView.setAdapter(mAdapter);

        this.setContentView(licenseListView);
        setContentBackgroundDrawable(ResUtils.skin_background);
        setTitle(getString(de.psdev.licensesdialog.R.string.notices_title));
        setRightButton(de.psdev.licensesdialog.R.string.notices_close, this);
        return true;
    }

    @Override
    public void onClick(View v) {
        finish();
    }
}
