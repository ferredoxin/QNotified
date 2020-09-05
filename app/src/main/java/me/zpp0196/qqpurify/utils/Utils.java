package me.zpp0196.qqpurify.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.pm.PackageInfoCompat;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import nil.nadph.qnotified.SyncUtils;

/**
 * Created by zpp0196 on 2018/3/11.
 */
@SuppressWarnings("WeakerAccess")
public class Utils {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getTextFromAssets(Context context, String fileName) {
        try (InputStream is = context.getAssets().open(fileName)) {
            int length = is.available();
            byte[] buffer = new byte[length];
            is.read(buffer);
            return new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }

    public static long getAppVersionCode(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            return PackageInfoCompat.getLongVersionCode(pi);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getAppVersionName(Context context, String packageName) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(packageName, 0);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "unknown";
        }
    }

    public static String getProcessName() {
        try {
            return SyncUtils.getProcessName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String initialCapital(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static List<String> jArray2SList(JSONArray array) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                Object value = array.get(i);
                if (value == null) {
                    continue;
                }
                list.add(String.valueOf(value));
            } catch (JSONException ignore) {
            }
        }
        return list;
    }

    public static boolean isCallingFrom(String className) {
        boolean result = false;
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.getClassName().contains(className)) {
                result = true;
            }
        }
        return result;
    }

    @SuppressWarnings("unused")
    public static void printViewTree(View view) {
        recursiveView("\t", view);
    }

    private static void recursiveView(String space, View view) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            printView(space, view);
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                recursiveView(space + "\t", viewGroup.getChildAt(i));
            }
        } else {
            printView(space, view);
        }
    }

    @SuppressLint("ResourceType")
    private static void printView(String space, View view) {
        StringBuilder sb = new StringBuilder(space);
        sb.append(view.getClass().getCanonicalName());
        if (view.getId() > 0) {
            sb.append(", id: 0x").append(Integer.toHexString(view.getId()));
        }
        if (view instanceof TextView) {
            sb.append(", text: ").append(((TextView) view).getText());
        }
        if (!TextUtils.isEmpty(view.getContentDescription())) {
            sb.append(", description: ").append(view.getContentDescription());
        }
        sb.append(", visibility: ").append(getVisibilityString(view));
        Log.v("ViewTree", "printView: " + sb.toString());
    }

    public static String getVisibilityString(View view) {
        int visibility = view.getVisibility();
        switch (visibility) {
            case View.VISIBLE:
                return "VISIBLE";
            case View.INVISIBLE:
                return "INVISIBLE";
            case View.GONE:
                return "GONE";
            default:
                return "";
        }
    }
}
