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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.rymmmmm.hook.CustomMsgTimeFormat;
import java.io.File;
import java.io.IOException;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.Toasts;

public class RikkaCustomSplash extends RikkaDialog.RikkaConfigItem {

    private static final String DEFAULT_SPLASH_PATH = "";

    private static final String rq_splash_path = "rq_splash_path";
    private static final String rq_splash_enabled = "rq_splash_enabled";

    @Nullable
    private AlertDialog dialog;
    @Nullable
    private LinearLayout vg;

    private String currentPath;
    private boolean enableSplash;

    public RikkaCustomSplash(@NonNull RikkaDialog d) {
        super(d);
    }

    public static boolean IsEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
    }

    @Nullable
    public static String getCurrentSplashPath() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_splash_enabled)) {
            String val = cfg.getString(rq_splash_path);
            if (val == null) {
                val = DEFAULT_SPLASH_PATH;
            }
            return val;
        }
        return null;
    }

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("自定义启动图")
            .setNegativeButton("取消", null)
            .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx)
            .inflate(R.layout.rikka_select_splash_dialog, null);
        final TextView input = vg.findViewById(R.id.selectSplash_editTextPicLocation);
        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableCustomStartupPic);
        final RelativeLayout panel = vg.findViewById(R.id.layoutSplashPanel);
        enableSplash = ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
        enable.setChecked(enableSplash);
        panel.setVisibility(enableSplash ? View.VISIBLE : View.GONE);
        currentPath = ConfigManager.getDefaultConfig().getString(rq_splash_path);
        if (currentPath == null) {
            currentPath = DEFAULT_SPLASH_PATH;
        }
        input.setText(currentPath);
        enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                enableSplash = isChecked;
                panel.setVisibility(enableSplash ? View.VISIBLE : View.GONE);
            }
        });
        dialog.setView(vg);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    if (!enableSplash) {
                        cfg.putBoolean(rq_splash_enabled, false);
                    } else {
                        currentPath = input.getText().toString();
                        if (currentPath.length() == 0) {
                            Toasts.error(ctx, "请输入图片路径");
                            return;
                        }
                        File file = new File(currentPath);
                        if (!file.exists() || !file.isFile()) {
                            Toasts.error(ctx, "路径不存在或者有误");
                            return;
                        }
                        if (!file.canRead()) {
                            Toasts.error(ctx, "无法读取图片 请检查权限");
                            return;
                        }
                        Bitmap bitmap = BitmapFactory.decodeFile(currentPath);
                        if (bitmap == null) {
                            Toasts.error(ctx, "无法加载图片 请检图片是否损坏");
                            return;
                        }
                        cfg.putBoolean(rq_splash_enabled, true);
                        cfg.putString(rq_splash_path, currentPath);
                    }
                    try {
                        cfg.save();
                    } catch (IOException e) {
                        log(e);
                    }
                    dialog.dismiss();
                    invalidateStatus();
                    if (enableSplash) {
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
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
    }

    @Override
    public String getName() {
        return "自定义启动图";
    }
}
