package me.zpp0196.qqpurify.utils;

import android.content.Context;
import android.util.Log;
import androidx.annotation.StringDef;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zpp0196 on 2018/5/17.
 */
public class SettingUtils implements Constants {

    public interface ISetting {

        String SETTING_MAINUI = "mainui";
        String SETTING_SIDEBAR = "sidebar";
        String SETTING_CHAT = "chat";
        String SETTING_TROOP = "troop";
        String SETTING_EXTENSION = "extension";
        String SETTING_SETTING = "setting";
        String SETTING_ABOUT = "about";
        String SETTING_EARLIER = "earlier";
        String SETTING_DEFAULT = "default";

        @StringDef(value = {
                SETTING_MAINUI,
                SETTING_SIDEBAR,
                SETTING_CHAT,
                SETTING_TROOP,
                SETTING_EXTENSION,
                SETTING_SETTING,
                SETTING_ABOUT,
                SETTING_EARLIER,
                SETTING_DEFAULT,
        })
        @Retention(RetentionPolicy.SOURCE)
        @interface SettingGroup {
        }

        @SettingGroup
        String getSettingGroup();
    }

    private static final JSONObject DEFAULT_SETTING = new JSONObject();
    static final JSONObject DEFAULT_GROUPS = new JSONObject();
    static JSONObject mJsonData;

    private static File mDataFile;

    static {
        try {
            JSONObject mainui = new JSONObject();
            mainui.put("simulateMenu", false);
            mainui.put("hideHonestSay", false);
            mainui.put("hideCreateTroop", false);
            mainui.put("hideFriendGroups", new JSONArray().put(0, ""));
            mainui.put("hideCTEntry", false);

            JSONObject sidebar = new JSONObject();
            sidebar.put("addModuleEntry", true);

            JSONObject chat = new JSONObject();
            chat.put(KEY_GRAY_TIP_KEYWORDS, "会员 礼物 送给 豪气 魅力 进场");

            JSONObject troop = new JSONObject();

            JSONObject extension = new JSONObject();
            extension.put(KEY_IMAGE_BG_COLOR, "#80000000");
            extension.put(KEY_RENAME_BASE_FORMAT, "%l_%n.apk");
            extension.put(KEY_REDIRECT_FILE_REC_PATH, "/Tencent/QQfile_recv/");
            extension.put("hideReadTouch", false);
            extension.put("hideColorScreen", false);

            JSONObject setting = new JSONObject();
            setting.put(KEY_DISABLE_MODULE, false);
            setting.put(KEY_LOG_SWITCH, false);
            setting.put(KEY_LOG_COUNT, 10);

            DEFAULT_GROUPS.put(ISetting.SETTING_MAINUI, mainui);
            DEFAULT_GROUPS.put(ISetting.SETTING_SIDEBAR, sidebar);
            DEFAULT_GROUPS.put(ISetting.SETTING_CHAT, chat);
            DEFAULT_GROUPS.put(ISetting.SETTING_TROOP, troop);
            DEFAULT_GROUPS.put(ISetting.SETTING_EXTENSION, extension);
            DEFAULT_GROUPS.put(ISetting.SETTING_SETTING, setting);
            DEFAULT_SETTING.put(KEY_GROUPS, DEFAULT_GROUPS);
            DEFAULT_SETTING.put(KEY_LAST_MODIFIED, System.currentTimeMillis());
        } catch (JSONException ignore) {
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(Context context) throws Exception {
        try {
            if (mDataFile == null) {
                mDataFile = new File(context.getDir("qq_purify", 0), "config.json");
            }
            mJsonData = new JSONObject(DEFAULT_SETTING.toString());
            if (!mDataFile.exists()) {
                File pf = mDataFile.getParentFile();
                if (pf != null && !pf.exists()) {
                    pf.mkdirs();
                }
                mDataFile.createNewFile();
            }
            String data = nil.nadph.qnotified.util.Utils.getFileContent(mDataFile.getPath()).trim();
            if (data.isEmpty()) {
                write(DEFAULT_SETTING);
            } else {
                mJsonData = new JSONObject(data);
            }
            Log.d("QQPurifySetting", "init: " + mJsonData.toString());
        } catch (Exception e) {
            throw new Exception("读取配置文件失败，使用默认配置");
        }
    }

    public static void restore() throws IOException, JSONException {
        write(DEFAULT_SETTING);
    }

    public static List<String> getGroups(String suffix) {
        List<String> list = new ArrayList<>();
        for (Iterator<String> it = DEFAULT_GROUPS.keys(); it.hasNext(); ) {
            list.add(Utils.initialCapital(it.next()) + suffix);
        }
        return list;
    }

    public static long getLong(String key, long def) {
        try {
            return mJsonData.getLong(key);
        } catch (JSONException e) {
            return def;
        }
    }

    static void write(JSONObject data) throws IOException, JSONException {
        nil.nadph.qnotified.util.Utils.saveFileContent(mDataFile.getPath(), data.toString());
        mJsonData = new JSONObject(data.toString());
    }
}
