/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.dialog;

import static nil.nadph.qnotified.util.Utils.log;

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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.rymmmmm.hook.CustomMsgTimeFormat;
import java.io.IOException;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Toasts;

public class RikkaCustomDeviceModelDialog extends RikkaDialog.RikkaConfigItem {

    private static final String DEFAULT_DEVICE_MANUFACTURER = "小米";
    private static final String DEFAULT_DEVICE_MODEL = "小米10 Pro";

    private static final String rq_custom_device_manufacturer = "rq_custom_device_manufacturer";
    private static final String rq_custom_device_model = "rq_custom_device_model";

    private static final String rq_custom_device_model_enabled = "rq_custom_device_model_enabled";

    @Nullable
    private AlertDialog dialog;
    @Nullable
    private LinearLayout vg;

    private String currentDeviceManufacturer;
    private String currentDeviceModel;
    private boolean enableCustomDeviceModel;

    public RikkaCustomDeviceModelDialog(@NonNull RikkaDialog d) {
        super(d);
    }

    public static boolean IsEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_custom_device_model_enabled);
    }

    @Nullable
    public static String getCurrentDeviceModel() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_custom_device_model_enabled)) {
            String val = cfg.getString(rq_custom_device_model);
            if (val == null) {
                val = DEFAULT_DEVICE_MODEL;
            }
            return val;
        }
        return null;
    }

    @Nullable
    public static String getCurrentDeviceManufacturer() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_custom_device_model_enabled)) {
            String val = cfg.getString(rq_custom_device_manufacturer);
            if (val == null) {
                val = DEFAULT_DEVICE_MANUFACTURER;
            }
            return val;
        }
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("自定义机型")
            .setNegativeButton("取消", null)
            .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx)
            .inflate(R.layout.rikka_custom_device_model_dialog, null);

        final TextView previewManufacturer = vg
            .findViewById(R.id.textViewCustomDeviceManufacturerPreview);
        final TextView previewModel = vg.findViewById(R.id.textViewDeviceModelPreview);

        final TextView inputManufacturer = vg.findViewById(R.id.editTextCustomDeviceManufacturer);
        final TextView inputModel = vg.findViewById(R.id.editTextCustomDeviceModel);

        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableCustomDeviceModel);

        final LinearLayout panel = vg.findViewById(R.id.layoutCustomDeviceModelPreview);

        enableCustomDeviceModel = ConfigManager.getDefaultConfig()
            .getBooleanOrFalse(rq_custom_device_model_enabled);
        enable.setChecked(enableCustomDeviceModel);

        panel.setVisibility(enableCustomDeviceModel ? View.VISIBLE : View.GONE);

        currentDeviceManufacturer = ConfigManager.getDefaultConfig()
            .getString(rq_custom_device_manufacturer);
        currentDeviceModel = ConfigManager.getDefaultConfig().getString(rq_custom_device_model);

        if (currentDeviceManufacturer == null) {
            currentDeviceManufacturer = DEFAULT_DEVICE_MANUFACTURER;
        }
        if (currentDeviceModel == null) {
            currentDeviceModel = DEFAULT_DEVICE_MODEL;
        }

        inputManufacturer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentDeviceManufacturer = s.toString();
                previewManufacturer.setText(currentDeviceManufacturer);
            }
        });
        inputManufacturer.setText(currentDeviceManufacturer);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableCustomDeviceModel = isChecked;
                panel.setVisibility(enableCustomDeviceModel ? View.VISIBLE : View.GONE);
            }
        });

        inputModel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                currentDeviceModel = s.toString();
                previewModel.setText(currentDeviceModel);
            }
        });
        inputModel.setText(currentDeviceModel);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableCustomDeviceModel = isChecked;
                panel.setVisibility(enableCustomDeviceModel ? View.VISIBLE : View.GONE);
            }
        });

        dialog.setView(vg);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    if (!enableCustomDeviceModel) {
                        cfg.putBoolean(rq_custom_device_model_enabled, false);
                    } else if (currentDeviceManufacturer.length() == 0
                        || currentDeviceModel.length() == 0) {
                        Toasts.error(ctx, "厂商或机型不能为空!");
                        return;
                    } else {
                        cfg.putBoolean(rq_custom_device_model_enabled, true);
                        cfg.putString(rq_custom_device_manufacturer, currentDeviceManufacturer);
                        cfg.putString(rq_custom_device_model, currentDeviceModel);
                    }
                    try {
                        cfg.save();
                        Toasts.success(ctx,
                            "重启" + HostInfo.getHostInfo().getHostName() + "生效!");
                    } catch (IOException e) {
                        log(e);
                    }
                    dialog.dismiss();
                    invalidateStatus();
                    if (enableCustomDeviceModel) {
                        CustomMsgTimeFormat hook = CustomMsgTimeFormat.INSTANCE;
                        if (!hook.isInited()) {
                            hook.init();
                        }
                    }
                }
            });
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_custom_device_model_enabled);
    }

    @Override
    public String getName() {
        return "自定义机型[需要重启" + HostInfo.getHostInfo().getHostName() + "]";
    }
}
