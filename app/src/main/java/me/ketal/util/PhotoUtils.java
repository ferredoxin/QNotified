package me.ketal.util;

import android.app.Activity;
import android.content.Intent;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.invoke_static;
import static nil.nadph.qnotified.util.Utils.invoke_static_any;
import static nil.nadph.qnotified.util.Utils.log;

public class PhotoUtils {
    static Class clz_photo_utils = load("com.tencent.mobileqq.activity.photo.PhotoUtils");

    public static void startPhotoListEdit(Intent intent, Activity activity, String className, int clipWidth, int clipHeight, int targetWidth, int targetHeight, String targetPath) {
        try {
            invoke_static_any(clz_photo_utils,
                intent, activity, className, clipWidth, clipHeight, targetWidth, targetHeight, targetPath,
                Intent.class, Activity.class, String.class, int.class, int.class, int.class, int.class, String.class);
        } catch (Exception e) {
            log(e);
        }
    }

    public static void startPhotoEdit(Intent intent, Activity activity, String className, int clipWidth, int clipHeight, int targetWidth, int targetHeight, String sourcePath, String targetPath) {
        try {
            invoke_static_any(clz_photo_utils,
                intent, activity, className, clipWidth, clipHeight, targetWidth, targetHeight, sourcePath, targetPath,
                Intent.class, Activity.class, String.class, int.class, int.class, int.class, int.class, String.class, String.class);
        } catch (Exception e) {
            log(e);
        }
    }
}
