package me.zpp0196.qqpurify.fragment.base;

import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceScreen;
import androidx.preference.TwoStatePreference;
import java.util.Set;
import me.kyuubiran.hook.SimplifyQQSettingMe;
import me.singleneuron.qn_kernel.data.HostInfo;
import me.zpp0196.qqpurify.activity.MainActivity;
import me.zpp0196.qqpurify.hook.P2CUtils;
import me.zpp0196.qqpurify.utils.Constants;
import me.zpp0196.qqpurify.utils.SettingUtils;
import nil.nadph.qnotified.config.AbstractConfigItem;
import nil.nadph.qnotified.config.SwitchConfigItem;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.ui.ViewBuilder;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;
import org.ferredoxin.ferredoxin_ui.base.UiDescription;
import org.ferredoxin.ferredoxin_ui.base.UiItem;
import org.ferredoxin.ferredoxin_ui.base.UiPreference;
import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public abstract class AbstractPreferenceFragment extends PreferenceFragmentCompat implements
    Constants,
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

            //查找相关类
            String pref_key = preference.getKey();
            if (pref_key == null) {
                return;
            }
            pref_key = pref_key.replace("!", "");
            String[] __ = pref_key.split("\\$");
            String cfgName = __[0];
            String keyName = null;
            if (__.length > 1) {
                keyName = __[1];
            }
            AbstractConfigItem _item = P2CUtils.findConfigByName(cfgName);

            //单独处理与FerredoxinUI的桥接
            if (_item instanceof UiItem && keyName == null) {
                UiItem uiItem = (UiItem) _item;
                UiDescription uiDescription = uiItem.getPreference();
                if (uiDescription instanceof UiPreference) {
                    UiPreference uiPreference = (UiPreference) uiDescription;
                    bindUiItem(preference, uiPreference);
                    return;
                }
            }

            // 绑定Value
            bindPreferenceValue(preference, _item, keyName);
            // 绑定Summary
            //bindPreferenceSummary(preference, _item);
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

    private void bindPreferenceValue(Preference preference, AbstractConfigItem _item,
        String keyName) {

        if ((_item == null || !_item.isValid()) && (preference instanceof TwoStatePreference
            || preference instanceof ListPreference
            || preference instanceof MultiSelectListPreference
            || preference instanceof EditTextPreference)) {
            preference.setEnabled(false);
            preference.setSummary("暂不开放");
        } else {
            try {
                if (preference instanceof TwoStatePreference) {
                    if (keyName == null) {
                        SwitchConfigItem item = (SwitchConfigItem) _item;
                        ((TwoStatePreference) preference).setChecked(item.isEnabled());
                    } else {
                        if (_item instanceof SimplifyQQSettingMe) {
                            SimplifyQQSettingMe item = (SimplifyQQSettingMe) _item;
                            if (item.hasConfig(keyName)) {
                                ((TwoStatePreference) preference)
                                    .setChecked(item.getBooleanConfig(keyName));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Utils.log(e);
                preference.setSummary((e + "").replaceAll("java\\.[a-z]+\\.", ""));
            }
        }
    }

    //Todo 把这个函数移入FerredoxinUI
    private void bindUiItem(Preference preference, UiPreference uiPreference) {
        if (uiPreference instanceof UiSwitchPreference
            && preference instanceof TwoStatePreference) {
            preference.setSummary(uiPreference.getSummary());
            preference.setEnabled(uiPreference.getValid());
            preference.setOnPreferenceChangeListener(
                (preference12, newValue) -> {
                    boolean value = (boolean) newValue;
                    ((UiSwitchPreference) uiPreference).getValue().postValue(value);
                    ((UiSwitchPreference) uiPreference).getValue().observe(getViewLifecycleOwner(),
                        aBoolean -> {
                            if (aBoolean.equals(((TwoStatePreference) preference).isChecked())) {
                                return;
                            }
                            ((TwoStatePreference) preference).setChecked(aBoolean);
                        });
                    return true;
                });
        } else {
            preference.setSummary(uiPreference.getSummary());
            preference.setEnabled(uiPreference.getValid());
            preference.setOnPreferenceClickListener(
                preference1 -> uiPreference.getOnClickListener()
                    .invoke(AbstractPreferenceFragment.this.mActivity));
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
            if (keyName == null && newValue instanceof Boolean
                && _item instanceof SwitchConfigItem) {
                SwitchConfigItem item = (SwitchConfigItem) _item;
                boolean val2 = (Boolean) newValue;
                item.setEnabled(val2);
            } else if (_item instanceof SimplifyQQSettingMe) {
                SimplifyQQSettingMe item = (SimplifyQQSettingMe) _item;
                if (newValue instanceof Boolean) {
                    item.setBooleanConfig(keyName, (Boolean) newValue);
                } else if (newValue instanceof Set
                    && preference instanceof MultiSelectListPreference) {
                    //handle String only
                    Set<String> selected = (Set<String>) newValue;
                    CharSequence[] vals = ((MultiSelectListPreference) preference).getEntryValues();
                    for (CharSequence val : vals) {
                        String __fullName = (keyName == null ? "" : keyName.concat("$"))
                            .concat(val.toString());
                        String kval = val.toString();
                        item.setBooleanConfig(__fullName, selected.contains(kval));
                    }
                } else {
                    throw new UnsupportedOperationException("" + newValue);
                }
            }
            //bindPreferenceSummary(preference, _item);
            if (_item instanceof BaseDelayableHook) {
                BaseDelayableHook hook = (BaseDelayableHook) _item;
                if (hook.isEnabled() && !hook.isInited()) {
                    new Thread(() -> ViewBuilder.doSetupAndInit(mActivity, hook)).start();
                }
            }
            _item.sync();
            if (restartRequired) {
                Toasts.info(mActivity,
                    "重启" + HostInfo.getHostInfo().getHostName() + "生效");
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
