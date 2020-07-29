package nil.nadph.qnotified.dialog;

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
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.SettingsActivity;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.rikka.CustomMsgTimeFormat;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.showToast;

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

    @SuppressLint("InflateParams")
    @Override
    public void onClick(View v) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("自定义启动图").setNegativeButton("取消", null)
                .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        final Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.rikka_select_splash_dialog, null);
        final TextView input = vg.findViewById(R.id.selectSplash_editTextPicLocation);
        final CheckBox enable = vg.findViewById(R.id.checkBoxEnableCustomStartupPic);
        final RelativeLayout panel = vg.findViewById(R.id.layoutSplashPanel);
        enableSplash = ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
        enable.setChecked(enableSplash);
        panel.setVisibility(enableSplash ? View.VISIBLE : View.GONE);
        currentPath = ConfigManager.getDefaultConfig().getString(rq_splash_path);
        if (currentPath == null) currentPath = DEFAULT_SPLASH_PATH;
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigManager cfg = ConfigManager.getDefaultConfig();
                if (!enableSplash) {
                    cfg.putBoolean(rq_splash_enabled, false);
                } else {
                    currentPath = input.getText().toString();
                    if (currentPath.length() == 0) {
                        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "请输入图片路径", Toast.LENGTH_SHORT);
                        return;
                    }
                    File file = new File(currentPath);
                    if (!file.exists() || !file.isFile()) {
                        showToast(ctx, TOAST_TYPE_ERROR, "路径不存在或者有误", Toast.LENGTH_SHORT);
                        return;
                    }
                    if (!file.canRead()) {
                        showToast(ctx, TOAST_TYPE_ERROR, "无法读取图片 请检查权限", Toast.LENGTH_SHORT);
                        return;
                    }
                    Bitmap bitmap = BitmapFactory.decodeFile(currentPath);
                    if (bitmap == null) {
                        showToast(ctx, TOAST_TYPE_ERROR, "无法加载图片 请检图片是否损坏", Toast.LENGTH_SHORT);
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
                    CustomMsgTimeFormat hook = CustomMsgTimeFormat.get();
                    if (!hook.isInited()) hook.init();
                }
            }
        });
    }

    @Override
    public boolean isEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
    }

    public static boolean IsEnabled() {
        return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_splash_enabled);
    }

    @Override
    public String getName() {
        return "自定义启动图";
    }

    @Nullable
    public static String getCurrentSplashPath() {
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        if (cfg.getBooleanOrFalse(rq_splash_enabled)) {
            String val = cfg.getString(rq_splash_path);
            if (val == null) val = DEFAULT_SPLASH_PATH;
            return val;
        }
        return null;
    }
}