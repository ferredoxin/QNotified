package nil.nadph.qnotified.dialog;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

import com.rymmmmm.hook.*;

import java.io.*;

import nil.nadph.qnotified.*;
import nil.nadph.qnotified.config.*;
import nil.nadph.qnotified.ui.*;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.*;

import static nil.nadph.qnotified.util.Utils.*;

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
        dialog = (AlertDialog) CustomDialog.createFailsafe(v.getContext()).setTitle("自定义启动图").setNegativeButton("取消", null)
            .setPositiveButton("保存", null).create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        Context ctx = dialog.getContext();
        vg = (LinearLayout) LayoutInflater.from(ctx).inflate(R.layout.rikka_select_splash_dialog, null);
        TextView input = vg.findViewById(R.id.selectSplash_editTextPicLocation);
        CheckBox enable = vg.findViewById(R.id.checkBoxEnableCustomStartupPic);
        RelativeLayout panel = vg.findViewById(R.id.layoutSplashPanel);
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
