package me.zpp0196.qqpurify.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.TwoStatePreference;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by zpp0196 on 2019/5/12.
 */
public class Setting extends SettingUtils {

    private String mGroupName;
    private static final Map<String, Setting> mSettings = new HashMap<>();

    public static Setting getInstance(ISetting setting) {
        return getInstance(setting.getSettingGroup());
    }

    public static Setting getInstance(@ISetting.SettingGroup String groupName) {
        if (mSettings.containsKey(groupName)) {
            return mSettings.get(groupName);
        }
        Setting setting = new Setting(groupName);
        mSettings.put(groupName, setting);
        return setting;
    }

    private Setting(String groupName) {
        this.mGroupName = groupName;
        if (!mSettings.containsKey(mGroupName)) {
            mSettings.put(mGroupName, this);
        }
    }

    @Nullable
    public Object get(Preference preference) {
        return nullOrValue(get(preference.getKey(), getDefaultValue(preference)));
    }

    @Nullable
    public Object get(String key) {
        return nullOrValue(get(key, getDefaultValue(key)));
    }

    @NonNull
    @SuppressWarnings("unchecked")
    public <T> T get(String key, @NonNull T defValue) {
        JSONObject group = getGroup();
        if (group.has(key)) {
            try {
                if (!(defValue instanceof Collection)) {
                    return (T) group.get(key);
                }
                JSONArray array = group.getJSONArray(key);
                Collection<String> collection = ((Collection) defValue);
                for (int i = 0; i < array.length(); i++) {
                    collection.add(String.valueOf(array.get(i)));
                }
                return (T) collection;
            } catch (Exception e) {
                return defValue;
            }
        }
        return defValue;
    }

    public void put(String key, Object value) throws JSONException, IOException {
        JSONObject group = getGroup();
        group.put(key, value);
        if (!mJsonData.has(KEY_GROUPS)) {
            mJsonData.put(KEY_GROUPS, new JSONObject());
        }
        JSONObject groups = mJsonData.getJSONObject(KEY_GROUPS);
        groups.put(mGroupName, group);
        mJsonData.put(KEY_GROUPS, groups);
        mJsonData.put(KEY_LAST_MODIFIED, System.currentTimeMillis());
        write(mJsonData);
    }

    @NonNull
    private Object getDefaultValue(Preference preference) {
        Object value = getDefaultValue(preference.getKey());
        value = nullOrValue(value);
        if (value != null) {
            return value;
        }
        if (preference instanceof TwoStatePreference) {
            return false;
        }
        if (preference instanceof MultiSelectListPreference) {
            return new HashSet<>();
        }
        return "";
    }

    @NonNull
    private Object getDefaultValue(String key) {
        try {
            return getDefaultGroup().get(key);
        } catch (JSONException e) {
            return new Object();
        }
    }

    private JSONObject getGroup() {
        JSONObject mGroup;
        try {
            mGroup = mJsonData.getJSONObject(KEY_GROUPS).getJSONObject(mGroupName);
        } catch (Exception e) {
            mGroup = getDefaultGroup();
        }
        return mGroup;
    }

    private Object nullOrValue(Object val) {
        return val == null || val.getClass().equals(Object.class) ? null : val;
    }

    private JSONObject getDefaultGroup() {
        try {
            return DEFAULT_GROUPS.getJSONObject(mGroupName);
        } catch (JSONException ignore) {
            return new JSONObject();
        }
    }
}
