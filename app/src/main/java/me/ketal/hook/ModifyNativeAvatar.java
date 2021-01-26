package me.ketal.hook;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.lang.reflect.Proxy;

import me.ketal.util.MD5;
import me.ketal.util.PhotoUtils;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.activity.SettingsActivity;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.Toasts;

import static me.singleneuron.util.QQVersion.QQ_8_2_8;
import static nil.nadph.qnotified.util.Initiator.getHostClassLoader;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.copy;
import static nil.nadph.qnotified.util.Utils.findMethodByTypes_1;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.getHostVersionCode;
import static nil.nadph.qnotified.util.Utils.getQQAppInterface;
import static nil.nadph.qnotified.util.Utils.invoke_virtual;
import static nil.nadph.qnotified.util.Utils.log;

public class ModifyNativeAvatar {
    public static final String MODIFY_NATIVE_AVATAR = "ketal_modify_native_avatar";
    public static final int FLAG_CHOOSE_PIC = 10001;

    public static View.OnClickListener clickTheButton() {
        return v -> {
            try {
                Class clz_actionsheet_helper = load("com.tencent.widget.ActionSheetHelper");
                Class clz_actionsheet_onclick = load("com.tencent.widget.ActionSheet$OnButtonClickListener");
                Object dialog = findMethodByTypes_1(clz_actionsheet_helper, Dialog.class, Context.class, View.class, int.class, ViewGroup.LayoutParams.class)
                    .invoke(null, v.getContext(), null, -1, null);
                invoke_virtual(dialog, "addButton", "从相册选择图片", CharSequence.class);
                //invoke_virtual(dialog, "addButton", "拍照", CharSequence.class);
                if (isEnable())
                    invoke_virtual(dialog, "addButton", "恢复原始头像", CharSequence.class);
                invoke_virtual(dialog, "addCancelButton", "取消", CharSequence.class);
                Object listener = Proxy.newProxyInstance(getHostClassLoader(),
                    new Class[]{clz_actionsheet_onclick},
                    (proxy, method, args) -> {
                        if (method.getName().equals("OnClick")) {
                            try {
                                int index = (int) args[1];
                                switch (index) {
                                    case 0:
                                        SelectByAlbum((Activity) v.getContext());
                                        break;
                                    case 1:
                                        cancelModify();
                                        break;
                                    default:
                                        break;
                                }
                                invoke_virtual(dialog, "dismiss");
                            } catch (Exception e) {
                                log(e);
                            }
                        }
                        return null;
                    });
                invoke_virtual(dialog, "setOnButtonClickListener", listener, clz_actionsheet_onclick);
                invoke_virtual(dialog, "show");
            } catch (Exception e) {
                log(e);
            }
        };
    }

    public static boolean setAvatar(String path) {
        try {
            String targetPath = getAvatarCachePath(getQQAppInterface().getAccount());
            copy(new File(path), new File(targetPath));
            new File(path).delete();
            setEnable(true);
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    public static void cancelModify() {
        new File(getAvatarCachePath(getQQAppInterface().getAccount())).delete();
        setEnable(false);
        Toasts.info(getApplication(), "恢复完毕，重启生效");
    }

    private static void SelectByAlbum(Activity activity) {
        Intent intent = new Intent();
        int b2 = b(activity);
        intent.putExtra("PhotoConst.PHOTO_LIST_SHOW_PREVIEW", true);
        intent.putExtra("PhotoConst.EDIT_MASK_SHAPE_TYPE", 0);//圆形
        intent.putExtra("PhotoConst.PHOTOLIST_KEY_FILTER_GIF_VIDEO", true);
        intent.putExtra("fromWhereClick", FLAG_CHOOSE_PIC);
        String path = Environment.getExternalStorageDirectory().getPath() + "/Android/data/"
            + activity.getPackageName() + "/Tencent/MobileQQ/portrait/temp/" + System.currentTimeMillis() + "_portrait.tmp";
        PhotoUtils.startPhotoListEdit(intent, activity, SettingsActivity.class.getName(), b2, b2, 140, 140, path);
        activity.finish();
    }

    public static int b(Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return (int) ((((float) (Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels) - 10)) * 0.8f) + 0.5f);
    }

    private static String getAvatarCachePath(String uin) {
        String md5 = MD5.toMD5(MD5.toMD5(MD5.toMD5(uin) + uin) + uin);
        String sdPath = Environment.getExternalStorageDirectory().getPath();
        if (getHostVersionCode() >= QQ_8_2_8)
            return sdPath + "/Android/data/" + getApplication().getPackageName()
                + "/Tencent/MobileQQ/head/_hd/" + md5 + ".jpg_";
        return sdPath + "/tencent/MobileQQ/head/_hd/" + md5 + ".jpg_";
    }

    public static boolean isEnable() {
        return ConfigManager.getDefaultConfig().getBooleanOrDefault(MODIFY_NATIVE_AVATAR, false);
    }

    public static void setEnable(boolean on) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(MODIFY_NATIVE_AVATAR, on);
            mgr.save();
        } catch (Exception e) {
            log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(getApplication(), e + "");
            } else {
                SyncUtils.post(() -> Toasts.error(getApplication(), e + ""));
            }
        }
    }
}
