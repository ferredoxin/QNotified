/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.util;

import static cc.ioctl.util.DateTimeUtil.getRelTimeStrSec;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.TimeZone;
import javax.net.ssl.HttpsURLConnection;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ViewBuilder;

public class UpdateCheck implements View.OnClickListener, Runnable {

    public static final String UPDATE_INFO_GET_WEEKLY = "https://api.appcenter.ms/v0.1/public/sdk/apps/ddf4b597-1833-45dd-af28-96ca504b8123/distribution_groups/8a11cc3e-47da-4e3b-84e7-ac306a128aaf/releases/latest";
    public static final String qn_update_info = "qn_update_info";
    public static final String qn_update_time = "qn_update_time";
    private final int RL_LOAD = 1;
    private final int RL_SHOW_RET = 2;
    int currVerCode = Utils.QN_VERSION_CODE;
    String currVerName = Utils.QN_VERSION_NAME;
    ViewGroup viewGroup;
    private boolean clicked = false;
    private PHPArray result;
    private int runlevel;

    public UpdateCheck() {
    }

    public static long iso8601ToTimestampMs(String expr) {
        if (!expr.endsWith("Z")) {
            throw new IllegalArgumentException("reject, not UTC");
        }
        String[] arr = expr.replace("Z", "").split("T");
        String[] p1 = arr[0].split("-");
        int yyyy = Integer.parseInt(p1[0]);
        int MM = Integer.parseInt(p1[1]);
        int dd = Integer.parseInt(p1[2]);
        String[] p2 = arr[1].split("[:.]");
        int HH = Integer.parseInt(p2[0]);
        int mm = Integer.parseInt(p2[1]);
        int ss = Integer.parseInt(p2[2]);
        int ms = Integer.parseInt(p2[3]);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(yyyy, MM - 1, dd, HH, mm, ss);
        return calendar.getTime().getTime() + ms;
    }

    public String doRefreshInfo() {
        String content = null;
        int failed = 0;
        try {
            URL reqURL = new URL(UPDATE_INFO_GET_WEEKLY);
            HttpsURLConnection httpsConn = (HttpsURLConnection) reqURL.openConnection();
            InputStream in = httpsConn.getInputStream();
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            byte[] buf = new byte[256];
            int len;
            while ((len = in.read(buf)) != -1) {
                bais.write(buf, 0, len);
            }
            in.close();
            content = bais.toString("UTF-8");
            httpsConn.disconnect();
            ConfigManager cache = ConfigManager.getCache();
            cache.putString(qn_update_info, content);
            cache.getAllConfig().put(qn_update_time, System.currentTimeMillis() / 1000L);
            cache.save();
            return content;
        } catch (IOException e) {
            final IOException e2 = e;
            runlevel = 0;
            if (content == null) {
                new Handler(viewGroup.getContext().getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(viewGroup.getContext(), "检查更新失败:" + e2, Toast.LENGTH_SHORT)
                            .show();
                    }
                });
            }
        }
        return null;
    }

    private String getCachedUpdateInfoOrNull() {
        try {
            ConfigManager cache = ConfigManager.getCache();
            String str = cache.getString(qn_update_info);
            long time = cache.getLongOrDefault(qn_update_time, 0);
            return str;
        } catch (Exception e) {
            log(e);
            return null;
        }

    }

    public void setVersionTip(ViewGroup vg) {
        String str = getCachedUpdateInfoOrNull();
        if (str != null) {
            doSetVersionTip(vg, PHPArray.fromJson(str));
        }
    }

    public void doSetVersionTip(ViewGroup vg, PHPArray json) {
        viewGroup = vg;
        try {
            TextView tv_v = viewGroup.findViewById(ViewBuilder.R_ID_VALUE);
            TextView tv_t = viewGroup.findViewById(ViewBuilder.R_ID_TITLE);
            long currBuildTime = Utils.getBuildTimestamp();
            long latestBuildTime;
            String latestName;
            latestBuildTime = iso8601ToTimestampMs((String) json.__("uploaded_at")._$());
            latestName = (String) json.__("short_version")._$();
            if (latestBuildTime - currBuildTime > 10 * 60 * 1000L) {
                //has newer
                tv_v.setText(latestName);
                tv_v.setTextColor(Color.argb(255, 242, 140, 72));
                tv_t.setText("有新版本可用");
                if (clicked) {
                    doShowUpdateInfo();
                }
            } else {
                tv_v.setText("已是最新");
            }
        } catch (Exception e) {
            log(e);
        }
    }

    @Override
    public void run() {
        switch (runlevel) {
            case RL_LOAD:
                String ret = doRefreshInfo();
                if (ret == null) {
                    return;
                }
                runlevel = 2;
                result = PHPArray.fromJson(ret);
                new Handler(viewGroup.getContext().getMainLooper()).post(this);
                return;
            case RL_SHOW_RET:
                doSetVersionTip(viewGroup, result);
                runlevel = 0;
                if (clicked) {
                    doShowUpdateInfo();
                }
                return;
        }
    }

    private void doShowUpdateInfo() {
        try {
            clicked = false;
            Activity ctx = (Activity) viewGroup.getContext();
            CustomDialog dialog = CustomDialog.create(ctx);
            dialog.setTitle("当前" + currVerName + " (" + currVerCode + ")");
            dialog.setCancelable(true);
            dialog.setNegativeButton("关闭", new Utils.DummyCallback());
            SpannableStringBuilder sb = new SpannableStringBuilder();

            PHPArray ver = (PHPArray) result;
            String vn = (String) ver.__("short_version")._$();
            int vc = Integer.parseInt((String) ver.__("version")._$());
            String desc = "" + ver.__("release_notes")._$();
            String md5 = (String) ver.__("fingerprint")._$();
            String download_url = (String) ver.__("download_url")._$();
            long time = iso8601ToTimestampMs((String) ver.__("uploaded_at")._$());
            String date = getRelTimeStrSec(time / 1000L);

            SpannableString tmp = new SpannableString(vn + " (" + vc + ")");
            tmp.setSpan(new RelativeSizeSpan(1.8f), 0, tmp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(tmp);

            if (currVerName.equals(vn)) {
                sb.append("当前版本");
            } else if (time - Utils.getBuildTimestamp() > 10 * 60 * 1000) {
                sb.append("新版本");
            } else if (time - Utils.getBuildTimestamp() < -10 * 60 * 1000) {
                sb.append("旧版本");
            }

            sb.append("\n发布于").append(date);

            sb.append("\nmd5:").append(md5).append("\n");
            sb.append(desc);
            sb.append("\n下载地址:\n");
            tmp = new SpannableString((String) download_url);
            tmp.setSpan(new URLSpan((String) download_url), 0, tmp.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            sb.append(tmp);
            sb.append("\n");
            sb.append("\n");

            dialog.setMessage(sb);
            TextView tv = dialog.getMessageTextView();
            if (tv != null) {
                tv.setLinksClickable(true);
                tv.setEnabled(true);
                tv.setFocusable(true);
                try {
                    tv.setFocusableInTouchMode(true);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        tv.setTextIsSelectable(true);
                    }
                    tv.setAutoLinkMask(Linkify.WEB_URLS);
                } catch (NoSuchMethodError ignored) {
                }
            }
            dialog.show();
        } catch (Exception e) {
            log(e);
        }
    }

    @Override
    public void onClick(View v) {
        viewGroup = (ViewGroup) v;
        clicked = true;
        if (result == null) {
            runlevel = 1;
            new Thread(this).start();
        } else {
            doShowUpdateInfo();
        }
    }
}
