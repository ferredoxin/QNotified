/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

import static nil.nadph.qnotified.util.Utils.isEmpty;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.ResUtils;

@MainProcess
public class NewsHelper implements Runnable {

    public static final String NEWS_INFO_GET2 = "https://raw.githubusercontent.com/ferredoxin/QNotified/master/news.json";
    public static final String NEWS_INFO_GET1 = "https://gitee.com/kernelex/QNotified/raw/master/news.json";
    private static final String QN_CACHED_NEWS = "qn_cached_news";
    private static final int INTERVAL_SEC = 3600;

    private final WeakReference<TextView> ptv;

    private NewsHelper(@Nullable WeakReference<TextView> p) {
        ptv = p;
    }

    public static void asyncFetchNewsIfNeeded(@Nullable TextView tv) {
        boolean needUpdate = true;
        ConfigManager cfg = ConfigManager.getCache();
        try {
            String old = cfg.getString(QN_CACHED_NEWS);
            News news;
            if (old != null) {
                try {
                    news = News.formJson(old);
                    needUpdate = news.time + INTERVAL_SEC < System.currentTimeMillis() / 1000L;
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
        if (needUpdate) {
            if (tv != null) {
                new Thread(new NewsHelper(new WeakReference<>(tv))).start();
            } else {
                new Thread(new NewsHelper(null)).start();
            }
        }
    }


    public static void getCachedNews(TextView tv) {
        ConfigManager cfg = ConfigManager.getCache();
        String ret = cfg.getString(QN_CACHED_NEWS);
        boolean show;
        News news = null;
        if (ret != null) {
            try {
                news = News.formJson(ret);
            } catch (Exception ignored) {
            }
        }
        show = null != news;
        if (show) {
            show = (news.persist || (news.time + news.ttl > System.currentTimeMillis() / 1000L))
                && !isEmpty(news.text);
        }
        if (show) {
            tv.setText(news.text);
            if (news.color != null && news.color.length() > 0) {
                try {
                    int color = Integer.parseInt(news.color);
                    tv.setTextColor(color);
                } catch (NumberFormatException ignored) {
                }
                try {
                    ColorStateList color = (ColorStateList) ResUtils.class.getField(news.color)
                        .get(null);
                    tv.setTextColor(color);
                } catch (Exception ignored) {
                }
            }
            tv.setTextIsSelectable(news.select);
            tv.setAutoLinkMask(news.link ? Linkify.WEB_URLS : 0);
            tv.setVisibility(View.VISIBLE);
        } else {
            tv.setVisibility(View.GONE);
        }
    }

    @Override
    public void run() {
        String content = null;
        int failed = 0;
        try {
            URL reqURL = new URL(NEWS_INFO_GET2);
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
            ConfigManager cfg = ConfigManager.getCache();
            cfg.putString(QN_CACHED_NEWS, content);
            cfg.save();
        } catch (IOException e) {
            //try another
        }
        if (content == null) {
            try {
                URL reqURL = new URL(NEWS_INFO_GET1);
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
                ConfigManager cfg = ConfigManager.getCache();
                cfg.putString(QN_CACHED_NEWS, content);
                cfg.save();
            } catch (IOException ignored) {
            }
        }
        final TextView textView;
        if (content != null && ptv != null && (textView = ptv.get()) != null) {
            ((Activity) textView.getContext()).runOnUiThread(() -> getCachedNews(textView));
        }
    }

    private static class News {

        public String text = null;
        public String color = null;
        public long time = 0;
        public int ttl = 0;
        public boolean select = false;
        public boolean link = false;
        public boolean persist = false;

        public static News formJson(String str) {
            JsonObject json = JsonParser.parseString(str).getAsJsonObject();
            News ret = new News();
            try {
                ret.text = json.get("text").toString();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.color = json.get("color").toString();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.time = json.get("time").getAsLong();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.ttl = json.get("ttl").getAsInt();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.persist = json.get("persist").getAsBoolean();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.link = json.get("link").getAsBoolean();
            } catch (Exception e) {
                log(e);
            }
            try {
                ret.select = json.get("select").getAsBoolean();
            } catch (Exception e) {
                log(e);
            }
            return ret;
        }
    }
}
