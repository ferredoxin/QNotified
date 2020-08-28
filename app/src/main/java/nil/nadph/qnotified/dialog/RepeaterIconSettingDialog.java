package nil.nadph.qnotified.dialog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.DebugDrawable;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

public class RepeaterIconSettingDialog implements View.OnClickListener, DialogInterface.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener {

    private final Context ctx;
    private final AlertDialog dialog;
    private String targetIconPath;
    private Button saveBtn;
    private final Button loadBtn;
    private final Button browseBtn;
    private final Button restoreDefBtn;
    private Bitmap currentIcon;
    private BitmapDrawable currentIconDrawable;
    private final EditText pathInput;
    private final ImageView prevImgView;
    private final CheckBox specDpi;
    private final RadioGroup dpiGroup;
    private final LinearLayout linearLayoutDpi;
    private boolean useDefault;
    private final TextView textViewWarning;
    private final int physicalDpi;

    private static Bitmap sCachedRepeaterIcon;

    public static final String qn_repeat_icon_data = "qn_repeat_icon_data";
    public static final String qn_repeat_icon_dpi = "qn_repeat_icon_dpi";
    public static final String qn_repeat_last_file = "qn_repeat_last_file";

    public RepeaterIconSettingDialog(Context context) {
        dialog = (AlertDialog) CustomDialog.createFailsafe(context).setTitle("自定义+1图标").setPositiveButton("保存", this)
                .setNegativeButton("取消", null).setCancelable(true).create();
        ctx = dialog.getContext();
        dialog.setCanceledOnTouchOutside(false);
        @SuppressLint("InflateParams") View v = LayoutInflater.from(ctx).inflate(R.layout.select_repeater_icon_dialog, null);
        loadBtn = v.findViewById(R.id.selectRepeaterIcon_buttonLoadFile);
        loadBtn.setOnClickListener(this);
        browseBtn = v.findViewById(R.id.selectRepeaterIcon_buttonBrowseImg);
        browseBtn.setOnClickListener(this);
        restoreDefBtn = v.findViewById(R.id.selectRepeaterIcon_buttonRestoreDefaultIcon);
        restoreDefBtn.setOnClickListener(this);
        prevImgView = v.findViewById(R.id.selectRepeaterIcon_imageViewPreview);
        prevImgView.setPadding(1, 1, 1, 1);
        //prevImgView.setBackgroundDrawable(new DebugDrawable(ctx));
        ViewCompat.setBackground(prevImgView, new DebugDrawable(ctx));
        specDpi = v.findViewById(R.id.selectRepeaterIcon_checkBoxSpecifyDpi);
        specDpi.setOnCheckedChangeListener(this);
        dpiGroup = v.findViewById(R.id.selectRepeaterIcon_RadioGroupDpiList);
        dpiGroup.setOnCheckedChangeListener(this);
        pathInput = v.findViewById(R.id.selectRepeaterIcon_editTextIconLocation);
        linearLayoutDpi = v.findViewById(R.id.selectRepeaterIcon_linearLayoutDpi);
        textViewWarning = v.findViewById(R.id.selectRepeaterIcon_textViewWarnMsg);
        physicalDpi = ctx.getResources().getDisplayMetrics().densityDpi;
        dialog.setView(v);
    }

    public static Bitmap getRepeaterIcon() {
        if (sCachedRepeaterIcon != null) {
            return sCachedRepeaterIcon;
        }
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        byte[] data = (byte[]) cfg.getAllConfig().get(qn_repeat_icon_data);
        int dpi = cfg.getIntOrDefault(qn_repeat_icon_dpi, 0);
        if (data != null) {
            Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (bm != null) {
                if (dpi > 0) {
                    bm.setDensity(dpi);
                }
                sCachedRepeaterIcon = bm;
            }
        }
        if (sCachedRepeaterIcon == null) {
            InputStream in = ResUtils.openAsset("repeat.png");
            if (in != null) {
                sCachedRepeaterIcon = BitmapFactory.decodeStream(in);
                try {
                    in.close();
                } catch (IOException ignored) {
                }
                sCachedRepeaterIcon.setDensity(320);
            } else {
                Utils.loge("getRepeaterIcon/E ResUtils.openAsset(\"repeat.png\") == null");
            }
        }
        return sCachedRepeaterIcon;
    }

    public AlertDialog show() {
        dialog.show();
        saveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        saveBtn.setOnClickListener(this);
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        String lastPath = cfg.getString(qn_repeat_last_file);
        byte[] data = (byte[]) cfg.getAllConfig().get(qn_repeat_icon_data);
        int dpi = cfg.getIntOrDefault(qn_repeat_icon_dpi, 0);
        if (lastPath != null) {
            pathInput.setText(lastPath);
        }
        if (data != null) {
            currentIcon = BitmapFactory.decodeByteArray(data, 0, data.length);
            if (currentIcon != null) {
                useDefault = false;
                if (dpi > 0) {
                    currentIcon.setDensity(dpi);
                }
            }
        }
        if (currentIcon == null) {
            currentIcon = BitmapFactory.decodeStream(ResUtils.openAsset("repeat.png"));
            currentIcon.setDensity(320);
            useDefault = true;
            linearLayoutDpi.setVisibility(View.GONE);
        } else {
            linearLayoutDpi.setVisibility(View.VISIBLE);
            int id = getSelectedIdByDpi(dpi);
            if (id == 0) {
                specDpi.setChecked(false);
                dpiGroup.setVisibility(View.GONE);
            } else {
                dpiGroup.check(id);
            }
        }
        currentIconDrawable = new BitmapDrawable(ctx.getResources(), currentIcon);
        prevImgView.setImageDrawable(currentIconDrawable);
        return dialog;
    }

    private int getCurrentSelectedDpi() {
        if (!specDpi.isChecked()) return 0;
        int id = dpiGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.selectRepeaterIcon_RadioButtonXxxhdpi:
                return 640;
            case R.id.selectRepeaterIcon_RadioButtonXxhdpi:
                return 480;
            case R.id.selectRepeaterIcon_RadioButtonXhdpi:
                return 320;
            case R.id.selectRepeaterIcon_RadioButtonHdpi:
                return 240;
            case R.id.selectRepeaterIcon_RadioButtonMdpi:
                return 160;
            case R.id.selectRepeaterIcon_RadioButtonLdpi:
                return 120;
            default:
                return 0;
        }
    }

    private static int getSelectedIdByDpi(int dpi) {
        switch (dpi) {
            case 640:
                return R.id.selectRepeaterIcon_RadioButtonXxxhdpi;
            case 480:
                return R.id.selectRepeaterIcon_RadioButtonXxhdpi;
            case 320:
                return R.id.selectRepeaterIcon_RadioButtonXhdpi;
            case 240:
                return R.id.selectRepeaterIcon_RadioButtonHdpi;
            case 160:
                return R.id.selectRepeaterIcon_RadioButtonMdpi;
            case 120:
                return R.id.selectRepeaterIcon_RadioButtonLdpi;
            default:
                return 0;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View v) {
        if (v == loadBtn) {
            String path = pathInput.getText().toString();
            if (path.length() == 0) {
                Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "请输入图片路径", Toast.LENGTH_SHORT);
                return;
            }
            File file = new File(path);
            if (!file.exists()) {
                Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "找不到文件", Toast.LENGTH_SHORT);
                return;
            }
            Bitmap bm = BitmapFactory.decodeFile(path);
            if (bm == null) {
                Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, "不支持此文件(格式)", Toast.LENGTH_SHORT);
                return;
            }
            long fileSize = file.length();
            if (fileSize > 4 * 1024) {
                textViewWarning.setText(String.format("该图片文件体积较大(%dbytes),可能导致卡顿", fileSize));
                textViewWarning.setVisibility(View.VISIBLE);
            } else {
                textViewWarning.setVisibility(View.GONE);
            }
            currentIcon = bm;
            targetIconPath = path;
            currentIcon.setDensity(getCurrentSelectedDpi());
            currentIconDrawable = new BitmapDrawable(ctx.getResources(), currentIcon);
            prevImgView.setImageDrawable(currentIconDrawable);
            useDefault = false;
            linearLayoutDpi.setVisibility(View.VISIBLE);
        } else if (v == restoreDefBtn) {
            currentIcon = null;
            useDefault = true;
            targetIconPath = null;
            linearLayoutDpi.setVisibility(View.GONE);
            textViewWarning.setVisibility(View.GONE);
            prevImgView.setImageDrawable(ResUtils.loadDrawableFromAsset("repeat.png", ctx));
        } else if (v == saveBtn) {
            if (targetIconPath != null) {
                try {
                    int dpi = getCurrentSelectedDpi();
                    FileInputStream fin = new FileInputStream(targetIconPath);
                    ByteArrayOutputStream bout = new ByteArrayOutputStream();
                    byte[] buf = new byte[2048];
                    int len;
                    while ((len = fin.read(buf)) != -1) {
                        bout.write(buf, 0, len);
                    }
                    fin.close();
                    byte[] arr = bout.toByteArray();
                    ConfigManager cfg = ConfigManager.getDefaultConfig();
                    cfg.getAllConfig().put(qn_repeat_icon_data, arr);
                    cfg.putInt(qn_repeat_icon_dpi, dpi);
                    cfg.putString(qn_repeat_last_file, targetIconPath);
                    cfg.save();
                    sCachedRepeaterIcon = currentIcon;
                    dialog.dismiss();
                } catch (IOException e) {
                    Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, e.toString(), 0);
                }
            } else {
                if (useDefault) {
                    try {
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        cfg.getAllConfig().remove(qn_repeat_icon_data);
                        cfg.remove(qn_repeat_icon_dpi);
                        cfg.remove(qn_repeat_last_file);
                        cfg.save();
                        dialog.dismiss();
                        sCachedRepeaterIcon = null;
                    } catch (IOException e) {
                        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, e.toString(), 0);
                    }
                } else {
                    try {
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        cfg.putInt(qn_repeat_icon_dpi, getCurrentSelectedDpi());
                        cfg.save();
                        dialog.dismiss();
                        sCachedRepeaterIcon = null;
                    } catch (IOException e) {
                        Utils.showToast(ctx, Utils.TOAST_TYPE_ERROR, e.toString(), 0);
                    }
                }
            }
        } else if (v == browseBtn) {
            Utils.showToastShort(ctx, "暂不支持...请手动复制文件路径到文本框并加载");
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (currentIcon != null) {
            currentIcon.setDensity(getCurrentSelectedDpi());
            if (currentIconDrawable != null) {
                currentIconDrawable = new BitmapDrawable(ctx.getResources(), currentIcon);
                prevImgView.setImageDrawable(currentIconDrawable);
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == specDpi) {
            dpiGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            currentIcon.setDensity(getCurrentSelectedDpi());
            if (currentIconDrawable != null) {
                currentIconDrawable = new BitmapDrawable(ctx.getResources(), currentIcon);
                prevImgView.setImageDrawable(currentIconDrawable);
            }
        }
    }

    public static void createAndShowDialog(Context ctx) {
        new RepeaterIconSettingDialog(ctx).show();
    }

    public static View.OnClickListener OnClickListener_createDialog(final Context ctx) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RepeaterIconSettingDialog(ctx).show();
            }
        };
    }
}
