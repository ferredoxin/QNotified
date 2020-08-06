package me.zpp0196.qqpurify.fragment.base;

import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.*;
import me.zpp0196.qqpurify.activity.MainActivity;
import me.zpp0196.qqpurify.utils.Constants;
import me.zpp0196.qqpurify.utils.Setting;
import me.zpp0196.qqpurify.utils.SettingUtils;
import org.json.JSONArray;

import java.util.Collection;
import java.util.Set;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragmentCompat implements Constants,
        Preference.OnPreferenceChangeListener, MainActivity.TabFragment, SettingUtils.ISetting {

    protected MainActivity mActivity;
    private Setting mSetting;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getPrefRes(), rootKey);
        mActivity = (MainActivity) getActivity();
        mSetting = Setting.getInstance(this);
        initPreferences();
    }

    protected void initPreferences() {
        if (mActivity.mRefreshedFragment.contains(this)) {
            return;
        }
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        for (int i = 0; i < preferenceScreen.getPreferenceCount(); i++) {
            Preference preference = preferenceScreen.getPreference(i);
            initPreference(preference);
        }
        mActivity.mRefreshedFragment.add(this);
    }

    private void initPreference(Preference preference) {
        // 图标预留空间
        preference.setIconSpaceReserved(false);
        // 排除PreferenceCategory
        if (preference.getKey() != null) {
            // 不保存数据到 SharedPreference
            preference.setPersistent(false);
            // 绑定Summary
            bindPreferenceSummary(preference);
            // 绑定Value
            bindPreferenceValue(preference);
            // 统一监听
            preference.setOnPreferenceChangeListener(this);
        }
        // 遍历PreferenceCategory
        if (preference instanceof PreferenceGroup) {
            PreferenceGroup preferenceGroup = ((PreferenceGroup) preference);
            for (int i = 0; i < preferenceGroup.getPreferenceCount(); i++) {
                initPreference(preferenceGroup.getPreference(i));
            }
        }
    }

    private void bindPreferenceSummary(Preference preference) {
        Object value = mSetting.get(preference);
        String stringValue = String.valueOf(value);

        // 排除空值、多选、开关
        if (preference instanceof MultiSelectListPreference ||
                preference instanceof TwoStatePreference || value == null) {
            return;
        }

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(stringValue);
            preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
        } else {
            preference.setSummary(stringValue);
        }
    }

    @SuppressWarnings("unchecked")
    private void bindPreferenceValue(Preference preference) {
        Object value = mSetting.get(preference);
        String stringValue = String.valueOf(value);

        if (preference instanceof ListPreference) {
            ((ListPreference) preference).setValue(stringValue);
        } else if (preference instanceof MultiSelectListPreference) {
            ((MultiSelectListPreference) preference).setValues((Set<String>) mSetting.get(preference));
        } else if (preference instanceof TwoStatePreference) {
            ((TwoStatePreference) preference).setChecked(value != null && (boolean) value);
        } else if (preference instanceof EditTextPreference && value != null) {
            ((EditTextPreference) preference).setText(stringValue);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            if (newValue instanceof Collection) {
                newValue = new JSONArray((Collection) newValue);
            }
            mSetting.put(preference.getKey(), newValue);
            bindPreferenceSummary(preference);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(mActivity, "保存失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPreferences();
    }

    protected abstract int getPrefRes();

    @Override
    public Fragment getFragment() {
        return this;
    }
}
