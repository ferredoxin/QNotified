package me.zpp0196.qqpurify.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Toast;
import androidx.preference.Preference;
import com.jaredrummler.android.colorpicker.ColorPickerDialogListener;
import me.zpp0196.qqpurify.fragment.base.AbstractPreferenceFragment;
import me.zpp0196.qqpurify.fragment.custom.ColorPickerPreference;
import me.zpp0196.qqpurify.utils.ThemeUtils;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.util.Toasts;

/**
 * Created by zpp0196 on 2019/2/9.
 */
public class SettingPreferenceFragment extends AbstractPreferenceFragment
    implements Preference.OnPreferenceClickListener, ColorPickerDialogListener {

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void initPreferences() {
        super.initPreferences();
        findPreference("restoreDefault").setOnPreferenceClickListener(this);
        findPreference("displayDesktop").setOnPreferenceClickListener(this);

        ColorPickerPreference appThemeColor = findPreference("appThemeColor");
        appThemeColor.setPersistent(false);
        appThemeColor.setColor(ThemeUtils.getThemeColor(mActivity));
        appThemeColor.setPresets(ThemeUtils.getColors(mActivity));
        appThemeColor.setSummary(ThemeUtils.getThemeTitle());
        appThemeColor.setColorPickerDialogListener(this);
        appThemeColor.setOnPreferenceChangeListener(null);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("restoreDefault".equals(preference.getKey())) {
            new AlertDialog.Builder(mActivity).setCancelable(false)
                .setTitle("提示")
                .setMessage("请确认是否恢复到默认设置")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (Math.random() < 2) {
                                throw new UnsupportedOperationException("不支持此操作");
                            }
                            mActivity.mRefreshedFragment.clear();
                            SettingPreferenceFragment.this.initPreferences();
                            Toast.makeText(mActivity, "已恢复到默认设置", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, "恢复失败: " + e.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
        } else if ("displayDesktop".equals(preference.getKey())) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setComponent(new ComponentName("nil.nadph.qnotified",
                    "nil.nadph.qnotified.activity.ConfigV2Activity"));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toasts.show(getActivity(), "拉起失败!\n注: 内置模块无法显示桌面图标\n" + e.toString());
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return super.onPreferenceChange(preference, newValue);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        ThemeUtils.setColor(mActivity, color);
        mActivity.recreate();
    }

    @Override
    public void onDialogDismissed(int dialogId) {
    }

    @Override
    protected int getPrefRes() {
        return R.xml.pref_setting;
    }

    @Override
    public String getTabTitle() {
        return "设置";
    }

    @Override
    public String getToolbarTitle() {
        return "模块设置";
    }

    @Override
    public String getSettingGroup() {
        return SETTING_SETTING;
    }
}
