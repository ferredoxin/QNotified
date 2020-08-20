/*
    from Alcatraz323
    https://github.com/alcatraz323/noapplet
 */

package me.singleneuron.util;

import android.text.TextUtils;

public class NoAppletUtil {
    public static String replace(String url, String key, String value) {
        if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(key)) {
            url = url.replaceAll("(" + key + "=[^&]*)", key + "=" + value);
        }
        return url;
    }

    public static String removeMiniProgramNode(String url) {
        if (!TextUtils.isEmpty(url)) {
            url = url.replaceAll("(mini_program.*?)=([^&]*)&", "");
        }
        return url;
    }


}
