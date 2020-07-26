package nil.nadph.qnotified.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.rikka.BaseApk;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;

import static nil.nadph.qnotified.util.Utils.log;

public class RikkaBaseApkFormatDialog extends RikkaDialog.RikkaConfigItem {
    private static final String DEFAULT_BASE_APK_FORMAT = "%n_%v.apk";

    private static final String rq_base_apk_format = "rq_base_apk_format";
    private static final String rq_base_apk_enabled = "rq_base_apk_enabled";

    @Nullable
    private AlertDialog dialog;
    @Nullable
    private LinearLayout vg;

    private String currentFormat;
    private boolean enableBaseApk;

    public RikkaBaseApkFormatDialog(@NonNull RikkaDialog d) {
        super(d);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("BaseApk").setNegativeButton("取消", null)
                .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.rikka_base_apk_dialog, null);
        final TextView preview = vg.findViewById(R.id.textViewBaseApkPreview);
        final TextView input = vg.findViewById(R.id.editTextBaseApkFormat);
        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableBaseApk);
        final LinearLayout panel = vg.findViewById(R.id.layoutBaseApkPanel);
        enableBaseApk = ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_base_apk_enabled);
        enable.setChecked(enableBaseApk);
        panel.setVisibility(enableBaseApk ? View.VISIBLE : View.GONE);
        currentFormat = ConfigManager.getDefaultConfig().getString(rq_base_apk_format);
        if (currentFormat == null) currentFormat = DEFAULT_BASE_APK_FORMAT;
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String format = s.toString();
                currentFormat = format;
                String result = format
                        .replace("%n", "QNotified")
                        .replace("%p", Utils.PACKAGE_NAME_SELF)
                        .replace("%v", Utils.QN_VERSION_NAME)
                        .replace("%c", String.valueOf(Utils.QN_VERSION_CODE));
                if (!format.toLowerCase().contains(".apk")) {
                    result += "\n提示:你还没有输入.apk后缀哦";
                }
                preview.setText(result);
            }
        });
        input.setText(currentFormat);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableBaseApk = isChecked;
                panel.setVisibility(enableBaseApk ? View.VISIBLE : View.GONE);
            }
        });
        dialog.setView(vg);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                boolean done = false;
                if (!enableBaseApk) {
                    cfg.putBoolean(rq_base_apk_enabled, false);
                    done = true;
                } else {
                    if (currentFormat != null && currentFormat.length() > 0 && (currentFormat.contains("%n") || currentFormat.contains("%p"))) {
                        cfg.putBoolean(rq_base_apk_enabled, true);
                        cfg.putString(rq_base_apk_format, currentFormat);
                        done = true;
                    } else {
                        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "请输入一个有效的格式", Toast.LENGTH_SHORT);
                    }
                }
                if (done) {
                    try {
                        cfg.save();
                    } catch (IOException e) {
                        log(e);
                    }
                    dialog.dismiss();
                    invalidateStatus();
                    if (enableBaseApk) {
                        BaseApk hook = BaseApk.get();
                        if (!hook.isInited()) hook.init();
                    }
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_base_apk_enabled);
    }

    public static boolean IsEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_base_apk_enabled);
    }

    @Override
    public String getName() {
        return "群上传重命名base.apk";
    }

    @Nullable
    public static String getCurrentBaseApkFormat() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_base_apk_enabled)) {
            String val = cfg.getString(rq_base_apk_format);
            if (val == null) val = DEFAULT_BASE_APK_FORMAT;
            return val;
        }
        return null;
    }
}