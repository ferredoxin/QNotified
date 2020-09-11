package nil.nadph.qnotified.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import java.io.IOException;

import static nil.nadph.qnotified.util.Utils.log;

public class RikkaColorPickDialog extends RikkaDialog.RikkaConfigItem {
    private static final String rq_dialog_border_color = "rq_dialog_border_color";
    private static final String rq_dialog_border_color_enabled = "rq_dialog_border_color_enabled";

    @Nullable
    private AlertDialog dialog;
    @Nullable
    private LinearLayout vg;

    private int currentColor;
    private boolean currentColorValid = false;
    private boolean enableColor;

    public RikkaColorPickDialog(@NonNull RikkaDialog d) {
        super(d);
    }

    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("花Q主题").setNegativeButton("取消", null)
                .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.rikka_theme_pick_color_dialog, null);
        final TextView invalid = vg.findViewById(R.id.textViewBorderInvalidColor);
        final TextView input = vg.findViewById(R.id.editTextBorderColor);
        final View preview = vg.findViewById(R.id.viewBorderColorPreview);
        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableBorderColor);
        final LinearLayout panel = vg.findViewById(R.id.layoutBorderColorPanel);
        boolean currEnable = enableColor = ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_dialog_border_color_enabled);
        enable.setChecked(currEnable);
        panel.setVisibility(currEnable ? View.VISIBLE : View.GONE);
        if (currEnable) {
            int color = currentColor = getCurrentRikkaBorderColor();
            invalid.setVisibility(View.GONE);
            preview.setVisibility(View.VISIBLE);
            preview.setBackgroundColor(color);
            input.setText("#" + String.format("%08x", color));
        } else {
            currentColorValid = false;
        }
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String colorStr = s.toString();
                    colorStr = colorStr.replace("色", "");
                    colorStr = colorStr.replace("红", "red").replace("绿", "green").replace("黄", "yellow").replace("蓝", "blue")
                            .replace("黑", "black").replace("灰", "gray").replace("白", "white").replace("紫", "purple");
                    currentColor = Color.parseColor(colorStr);
                    currentColorValid = true;
                    preview.setVisibility(View.VISIBLE);
                    invalid.setVisibility(View.GONE);
                    preview.setBackgroundColor(currentColor);
                } catch (Exception e) {
                    currentColorValid = false;
                    preview.setVisibility(View.GONE);
                    invalid.setVisibility(View.VISIBLE);
                }
            }
        });
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableColor = isChecked;
                panel.setVisibility(enableColor ? View.VISIBLE : View.GONE);
            }
        });
        dialog.setView(vg);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentColorValid || !enableColor) {
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    if (enableColor) {
                        cfg.putInt(rq_dialog_border_color, currentColor);
                    }
                    cfg.putBoolean(rq_dialog_border_color_enabled, enableColor);
                    try {
                        cfg.save();
                    } catch (IOException e) {
                        log(e);
                    }
                    rikkaDialog.itemOnDrawable.setStroke(Utils.dip2px(ctx, 2), getCurrentRikkaBorderColor());
                    dialog.dismiss();
                    invalidateStatus();
                } else {
                    Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "颜色无效", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_dialog_border_color_enabled);
    }

    @Override
    public String getName() {
        return "主题颜色";
    }

    public static int getCurrentRikkaBorderColor() {
        final int DEFAULT_COLOR = 0xFFFF8000;
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_dialog_border_color_enabled)) {
            return cfg.getIntOrDefault(rq_dialog_border_color, DEFAULT_COLOR);
        }
        return DEFAULT_COLOR;
    }
}