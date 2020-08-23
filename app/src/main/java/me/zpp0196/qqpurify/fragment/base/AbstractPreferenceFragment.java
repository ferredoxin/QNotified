package me.zpp0196.qqpurify.fragment.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.*;
import me.zpp0196.qqpurify.activity.MainActivity;
import me.zpp0196.qqpurify.hook.P2CUtils;
import me.zpp0196.qqpurify.utils.Constants;
import me.zpp0196.qqpurify.utils.SettingUtils;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.config.MultiConfigItem;
import nil.nadph.qnotified.config.SwitchConfigItem;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragmentCompat implements Constants,
        Preference.OnPreferenceChangeListener, MainActivity.TabFragment, SettingUtils.ISetting {

    protected MainActivity mActivity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(getPrefRes(), rootKey);
        mActivity = (MainActivity) getActivity();
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
            // 绑定Value
            bindPreferenceValue(preference);
            // 绑定Summary
            bindPreferenceSummary(preference);
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
        String pref_key = preference.getKey();
        if (pref_key == null) return;
        pref_key = pref_key.replace("!", "");
        String[] __ = pref_key.split("\\$");
        String cfgName = __[0];
        String keyName = null;
        if (__.length > 1) {
            keyName = __[1];
        }
        AbstractConfigItem _item = P2CUtils.findConfigByName(cfgName);

        // 排除空值、多选、开关
        if (preference instanceof MultiSelectListPreference ||
                preference instanceof TwoStatePreference || _item == null) {
            return;
        }
        try {
            String val = ((MultiConfigItem) _item).getStringConfig(keyName);
            if (preference instanceof ListPreference) {
                if (val != null) {
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(val);
                    preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
                }
            } else {
                if (!TextUtils.isEmpty(val)) {
                    preference.setSummary(val);
                }
            }
        } catch (Exception e) {
            Utils.log(e);
            preference.setSummary((e + "").replaceAll("java\\.[a-z]+\\.", ""));
        }
    }

    @SuppressWarnings("unchecked")
    private void bindPreferenceValue(Preference preference) {
        String pref_key = preference.getKey();
        if (pref_key == null) return;
        pref_key = pref_key.replace("!", "");
        String[] __ = pref_key.split("\\$");
        String cfgName = __[0];
        String keyName = null;
        if (__.length > 1) {
            keyName = __[1];
        }
        AbstractConfigItem _item = P2CUtils.findConfigByName(cfgName);
        if (_item == null && (preference instanceof TwoStatePreference
                || preference instanceof ListPreference || preference instanceof MultiSelectListPreference
                || preference instanceof EditTextPreference)) {
            preference.setEnabled(false);
            preference.setSummary("暂不开放");
        } else {
            try {
                if (preference instanceof ListPreference) {
                    String val = ((MultiConfigItem) _item).getStringConfig(keyName);
                    if (val != null) {
                        ((ListPreference) preference).setValue(val);
                    }
                } else if (preference instanceof MultiSelectListPreference) {
                    Set<String> selected = new HashSet<String>(((MultiSelectListPreference) preference).getValues());
                    MultiConfigItem item = (MultiConfigItem) _item;
                    //CharSequence[] texts=((MultiSelectListPreference) preference).getEntries();
                    CharSequence[] vals = ((MultiSelectListPreference) preference).getEntryValues();
                    for (CharSequence val : vals) {
                        String kval = val.toString();
                        String __fullName = (keyName == null ? "" : keyName.concat("$")).concat(kval);
                        //implicit throw a NPE if key is illegal
                        if (item.hasConfig(__fullName)) {
                            boolean z;
                            if (z = item.getBooleanConfig(__fullName)) {
                                selected.add(kval);
                            } else {
                                selected.remove(kval);
                            }
                            //Utils.logd(String.format("Load/D \"%s\"->%s", __fullName, "" + z));
                        } /*else {
                            Utils.logd(String.format("Load/D \"%s\"->%s", __fullName, "null"));
                        }*/
                    }
                    ((MultiSelectListPreference) preference).setValues(selected);
                } else if (preference instanceof TwoStatePreference) {
                    if (keyName == null) {
                        SwitchConfigItem item = (SwitchConfigItem) _item;
                        ((TwoStatePreference) preference).setChecked(item.isEnabled());
                    } else {
                        MultiConfigItem item = (MultiConfigItem) _item;
                        if (item.hasConfig(keyName)) {
                            ((TwoStatePreference) preference).setChecked(item.getBooleanConfig(keyName));
                        }
                    }
                } else if (preference instanceof EditTextPreference) {
                    String val = ((MultiConfigItem) _item).getStringConfig(keyName);
                    if (val != null) {
                        ((EditTextPreference) preference).setText(val);
                    }
                }
            } catch (Exception e) {
                Utils.log(e);
                preference.setSummary((e + "").replaceAll("java\\.[a-z]+\\.", ""));
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        try {
            String pref_key = preference.getKey();
            if (pref_key == null) {
                return false;
            }
            boolean restartRequired = false;
            if (pref_key.contains("!")) {
                restartRequired = true;
                pref_key = pref_key.replace("!", "");
            }
            String[] __ = pref_key.split("\\$");
            String cfgName = __[0];
            String keyName = null;
            if (__.length > 1) {
                keyName = __[1];
            }
            AbstractConfigItem _item = P2CUtils.findConfigByName(cfgName);
            if (_item == null) {
                Toast.makeText(mActivity, "404", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (keyName == null && newValue instanceof Boolean && _item instanceof SwitchConfigItem) {
                SwitchConfigItem item = (SwitchConfigItem) _item;
                boolean val2 = (Boolean) newValue;
                item.setEnabled(val2);
            } else {
                MultiConfigItem item = (MultiConfigItem) _item;
                if (newValue instanceof CharSequence) {
                    item.setStringConfig(keyName, newValue.toString());
                } else if (newValue instanceof Integer) {
                    item.setIntConfig(keyName, (Integer) newValue);
                } else if (newValue instanceof Boolean) {
                    item.setBooleanConfig(keyName, (Boolean) newValue);
                } else if (newValue instanceof Set && preference instanceof MultiSelectListPreference) {
                    //handle String only
                    Set<String> selected = (Set<String>) newValue;
                    //CharSequence[] texts=((MultiSelectListPreference) preference).getEntries();
                    CharSequence[] vals = ((MultiSelectListPreference) preference).getEntryValues();
                    //CharSequence[] ents = ((MultiSelectListPreference) preference).getEntries();
//                    Utils.logd(String.format("Save/D selected=%s", selected));
//                    Utils.logd(String.format("Save/D val=%s", Arrays.toString(vals) ));
//                    Utils.logd(String.format("Save/D ent=%s", Arrays.toString(ents) ));
                    for (CharSequence val : vals) {
                        String __fullName = (keyName == null ? "" : keyName.concat("$")).concat(val.toString());
                        String kval = val.toString();
                        item.setBooleanConfig(__fullName, selected.contains(kval));
                        //Utils.logd(String.format("Save/D \"%s\"->%s", __fullName, selected.contains(kval)));
                    }
                } else {
                    throw new UnsupportedOperationException("" + newValue);
                }
            }
            bindPreferenceSummary(preference);
            if (_item instanceof BaseDelayableHook) {
                BaseDelayableHook hook = (BaseDelayableHook) _item;
                if (hook.isEnabled() && !hook.isInited()) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ViewBuilder.doSetupAndInit(mActivity, hook);
                        }
                    }).start();
                }
            }
            _item.sync();
            if (restartRequired) {
                Utils.showToastShort(mActivity, "重启" + Utils.getHostAppName() + "生效");
            }
            return true;
        } catch (Exception e) {
            Utils.log(e);
            Toast.makeText(mActivity, "保存失败: " + e.getMessage(), Toast.LENGTH_LONG).show();
            return false;
        }
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
