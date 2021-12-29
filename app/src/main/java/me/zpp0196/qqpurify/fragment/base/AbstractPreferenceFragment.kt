package me.zpp0196.qqpurify.fragment.base

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.preference.*
import kotlinx.coroutines.flow.update
import me.kyuubiran.hook.SimplifyQQSettingMe
import me.singleneuron.qn_kernel.data.hostInfo
import me.zpp0196.qqpurify.activity.MainActivity
import me.zpp0196.qqpurify.activity.MainActivity.TabFragment
import me.zpp0196.qqpurify.hook.P2CUtils
import me.zpp0196.qqpurify.utils.Constants
import me.zpp0196.qqpurify.utils.SettingUtils.ISetting
import nil.nadph.qnotified.config.AbstractConfigItem
import nil.nadph.qnotified.config.SwitchConfigItem
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.ui.ViewBuilder
import nil.nadph.qnotified.util.Toasts
import nil.nadph.qnotified.util.Utils
import org.ferredoxin.ferredoxinui.common.base.UiItem
import org.ferredoxin.ferredoxinui.common.base.UiPreference
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference
import org.ferredoxin.ferredoxinui.common.base.observeStateFlow

/**
 * Created by zpp0196 on 2019/2/9.
 */
abstract class AbstractPreferenceFragment : PreferenceFragmentCompat(), Constants, Preference.OnPreferenceChangeListener, TabFragment, ISetting {
    protected lateinit var mActivity: MainActivity

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(prefRes, rootKey)
        mActivity = activity as MainActivity
        initPreferences()
    }

    protected open fun initPreferences() {
        if (mActivity.mRefreshedFragment.contains(this)) {
            return
        }
        val preferenceScreen = preferenceScreen
        for (i in 0 until preferenceScreen.preferenceCount) {
            val preference = preferenceScreen.getPreference(i)
            initPreference(preference)
        }
        mActivity.mRefreshedFragment.add(this)
    }

    private fun initPreference(preference: Preference) {
        // 图标预留空间
        preference.isIconSpaceReserved = false
        // 排除PreferenceCategory
        if (preference.key != null) {
            // 不保存数据到 SharedPreference
            preference.isPersistent = false

            //查找相关类
            var pref_key = preference.key ?: return
            pref_key = pref_key.replace("!", "")
            val prefs = pref_key.split("\\$").toTypedArray()
            val cfgName = prefs[0]
            var keyName: String? = null
            if (prefs.size > 1) {
                keyName = prefs[1]
            }
            val _item = P2CUtils.findConfigByName(cfgName)

            //单独处理与FerredoxinUI的桥接
            if (_item is UiItem && keyName == null) {
                val uiItem = _item as UiItem
                val uiDescription = uiItem.preference
                if (uiDescription is UiPreference) {
                    bindUiItem(preference, uiDescription)
                    return
                }
            }

            // 绑定Value
            bindPreferenceValue(preference, _item, keyName)
            // 绑定Summary
            //bindPreferenceSummary(preference, _item);
            // 统一监听
            preference.onPreferenceChangeListener = this
        }
        // 遍历PreferenceCategory
        if (preference is PreferenceGroup) {
            for (i in 0 until preference.preferenceCount) {
                initPreference(preference.getPreference(i))
            }
        }
    }

    private fun bindPreferenceValue(preference: Preference, _item: AbstractConfigItem?,
                                    keyName: String?) {
        if ((_item == null || !_item.isValid) && (preference is TwoStatePreference
                || preference is ListPreference
                || preference is MultiSelectListPreference
                || preference is EditTextPreference)
        ) {
            preference.isEnabled = false
            preference.summary = "暂不开放"
        } else {
            try {
                if (preference is TwoStatePreference) {
                    if (keyName == null) {
                        val item = _item as SwitchConfigItem?
                        preference.isChecked = item!!.isEnabled
                    } else {
                        if (_item is SimplifyQQSettingMe) {

                            if (_item.hasConfig(keyName)) {
                                preference.isChecked = _item.getBooleanConfig(keyName)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Utils.log(e)
                preference.summary = (e.toString() + "").replace("java\\.[a-z]+\\.".toRegex(), "")
            }
        }
    }

    //Todo 把这个函数移入FerredoxinUI
    private fun bindUiItem(preference: Preference, uiPreference: UiPreference) {
        if (uiPreference is UiSwitchPreference
            && preference is TwoStatePreference
        ) {
            preference.setSummary(uiPreference.summary)
            preference.setEnabled(uiPreference.valid)
            preference.setOnPreferenceChangeListener { _: Preference?, newValue: Any ->
                val value = newValue as Boolean
                uiPreference.value.update {
                    value
                }
                true
            }
            observeStateFlow(uiPreference.value) {
                if (it?.equals(preference.isChecked) != false) {
                    return@observeStateFlow
                }
                preference.isChecked = it
            }
        }
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        try {
            var pref_key = preference.key ?: return false
            var restartRequired = false
            if (pref_key.contains("!")) {
                restartRequired = true
                pref_key = pref_key.replace("!", "")
            }
            val prefs = pref_key.split("\\$").toTypedArray()
            val cfgName = prefs[0]
            var keyName: String? = null
            if (prefs.size > 1) {
                keyName = prefs[1]
            }
            val _item = P2CUtils.findConfigByName(cfgName)
            if (_item == null) {
                Toast.makeText(mActivity, "404", Toast.LENGTH_SHORT).show()
                return false
            }
            if (keyName == null && newValue is Boolean
                && _item is SwitchConfigItem
            ) {
                _item.isEnabled = newValue
            } else if (_item is SimplifyQQSettingMe)
                if (newValue is Boolean) {
                    _item.setBooleanConfig(keyName!!, newValue)
                } else if (newValue is Set<*>
                    && preference is MultiSelectListPreference
                ) {
                    //handle String only
                    val selected = newValue
                    val vals = preference.entryValues
                    for (`val` in vals) {
                        val __fullName = if (keyName == null) "" else "$keyName$$`val`"
                        val kval = `val`.toString()
                        _item.setBooleanConfig(__fullName, selected.contains(kval))
                    }
                } else {
                    throw UnsupportedOperationException("" + newValue)
                }
            //bindPreferenceSummary(preference, _item);
            if (_item is BaseDelayableHook) {
                val hook = _item
                if (hook.isEnabled && !hook.isInited) {
                    Thread { ViewBuilder.doSetupAndInit(mActivity, hook) }.start()
                }
            }
            _item.sync()
            if (restartRequired) {
                Toasts.info(mActivity,
                    "重启" + hostInfo.hostName + "生效")
            }
            return true
        } catch (e: Exception) {
            Utils.log(e)
            Toast.makeText(mActivity, "保存失败: " + e.message, Toast.LENGTH_LONG).show()
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        initPreferences()
    }

    abstract val prefRes: Int
    override fun getFragment(): Fragment {
        return this
    }
}
