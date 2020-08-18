package nil.nadph.qnotified.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.rikka.CustomMsgTimeFormat;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

public class RikkaCustomMsgTimeFormatDialog extends RikkaDialog.RikkaConfigItem {
    private static final String DEFAULT_MSG_TIME_FORMAT = "yyyy年MM月dd日 HH:mm:ss";

    private static final String rq_msg_time_format = "rq_msg_time_format";
    private static final String rq_msg_time_enabled = "rq_msg_time_enabled";

    @Nullable
    private AlertDialog dialog;
    @Nullable
    private LinearLayout vg;

    private String currentFormat;
    private boolean enableMsgTimeFormat;
    private boolean currentFormatValid = false;

    public RikkaCustomMsgTimeFormatDialog(@NonNull RikkaDialog d) {
        super(d);
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("自定义时间格式").setNegativeButton("取消", null)
                .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.rikka_msg_time_formart_dialog, null);
        final TextView preview = vg.findViewById(R.id.textViewMsgTimeFormatPreview);
        final TextView invalid = vg.findViewById(R.id.textViewInvalidMsgTimeFormat);
        final TextView input = vg.findViewById(R.id.editTextMsgTimeFormat);
        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableMsgTimeFormat);
        final LinearLayout panel = vg.findViewById(R.id.layoutMsgTimeFormatPanel);
        enableMsgTimeFormat = ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_msg_time_enabled);
        enable.setChecked(enableMsgTimeFormat);
        panel.setVisibility(enableMsgTimeFormat ? View.VISIBLE : View.GONE);
        currentFormat = ConfigManager.getDefaultConfig().getString(rq_msg_time_format);
        if (currentFormat == null) currentFormat = DEFAULT_MSG_TIME_FORMAT;
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            @SuppressLint("SimpleDateFormat")
            public void afterTextChanged(Editable s) {
                String format = s.toString();
                currentFormat = format;
                try {
                    SimpleDateFormat dsf = new SimpleDateFormat(format);
                    String result = dsf.format(new Date());
                    currentFormatValid = true;
                    invalid.setVisibility(View.GONE);
                    preview.setVisibility(View.VISIBLE);
                    preview.setText(result);
                } catch (Exception e) {
                    currentFormatValid = false;
                    preview.setVisibility(View.GONE);
                    invalid.setVisibility(View.VISIBLE);
                }
            }
        });
        input.setText(currentFormat);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableMsgTimeFormat = isChecked;
                panel.setVisibility(enableMsgTimeFormat ? View.VISIBLE : View.GONE);
            }
        });
        dialog.setView(vg);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                boolean done = false;
                if (!enableMsgTimeFormat) {
                    cfg.putBoolean(rq_msg_time_enabled, false);
                    done = true;
                } else {
                    if (currentFormatValid) {
                        cfg.putBoolean(rq_msg_time_enabled, true);
                        cfg.putString(rq_msg_time_format, currentFormat);
                        done = true;
                    } else {
                        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "请输入一个有效的时间格式", Toast.LENGTH_SHORT);
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
                    if (enableMsgTimeFormat) {
                        CustomMsgTimeFormat hook = CustomMsgTimeFormat.get();
                        if (!hook.isInited()) hook.init();
                    }
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_msg_time_enabled);
    }

    public static boolean IsEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_msg_time_enabled);
    }

    @Override
    public String getName() {
        return "聊天页自定义时间格式";
    }

    @Nullable
    public static String getCurrentMsgTimeFormat() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_msg_time_enabled)) {
            String val = cfg.getString(rq_msg_time_format);
            if (val == null) val = DEFAULT_MSG_TIME_FORMAT;
            return val;
        }
        return null;
    }

    @Nullable
    public static String getTimeFormat() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        String val = cfg.getString(rq_msg_time_format);
        if (val == null) val = DEFAULT_MSG_TIME_FORMAT;
        return val;
    }
}