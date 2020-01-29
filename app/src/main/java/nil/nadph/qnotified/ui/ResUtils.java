package nil.nadph.qnotified.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.ArscKit;
import nil.nadph.qnotified.util.Nullable;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class ResUtils {

    static public ColorStateList skin_gray3;
    static public ColorStateList skin_black;
    static public ColorStateList skin_red;
    static public ColorStateList skin_blue;
    static public Drawable qq_setting_item_bg_nor;
    static public Drawable qq_setting_item_bg_pre;
    static public Drawable bg_texture;
    static public Drawable skin_list_item_normal = null, skin_list_item_unread = null, skin_list_item_pressed = null;
    static public Drawable list_checkbox_selected_nopress, list_checkbox_selected, list_checkbox_multi, list_checkbox;
    static public Drawable skin_icon_arrow_right_normal = null, skin_background = null;
    static private String cachedThemeId;

    /*skin_group_list_item_pressed_theme_version2*/
    //static public Drawable skin_tips_newmessage;
    static private Map<String, Drawable> cachedDrawable = new HashMap<>();

    public static void initTheme(Context ctx) throws Throwable {
        try {
            String themeId = (String) invoke_static(load("com/tencent/mobileqq/theme/ThemeUtil"), "getUserCurrentThemeId", null, load("mqq/app/AppRuntime"));
            if (themeId.equals(cachedThemeId)) return;
        } catch (Exception e) {
            log(e);
        }
        skin_gray3
                = skin_black
                = skin_red
                = skin_blue = null;
        qq_setting_item_bg_nor
                = qq_setting_item_bg_pre = null;
        skin_list_item_normal = skin_list_item_unread = skin_list_item_pressed
                = skin_background = null;//=skin_tips_newmessage=null;
        list_checkbox_selected_nopress = list_checkbox_selected = list_checkbox_multi = list_checkbox = null;
        loadThemeByArsc(ctx, true);
        initByFallback(ctx);
    }

    private static void initByFallback(Context ctx) {
        //if(skin_tips_newmessage==null)skin_tips_newmessage= loadDrawableFromAsset("skin_tips_newmessage.9.png");
        if (skin_list_item_normal == null)
            skin_list_item_normal = loadDrawableFromAsset("skin_list_item_normal.9.png", ctx);
        if (skin_list_item_pressed == null)
            skin_list_item_pressed = loadDrawableFromAsset("skin_list_item_pressed.9.png", ctx);
        if (list_checkbox_selected_nopress == null)
            list_checkbox_selected_nopress = loadDrawableFromAsset("list_checkbox_selected_nopress.png", ctx);
        if (list_checkbox_selected == null)
            list_checkbox_selected = loadDrawableFromAsset("list_checkbox_selected.png", ctx);
        if (list_checkbox_multi == null) list_checkbox_multi = loadDrawableFromAsset("list_checkbox_multi.png", ctx);
        if (list_checkbox == null) list_checkbox = loadDrawableFromAsset("list_checkbox.png", ctx);
        if (skin_icon_arrow_right_normal == null)
            skin_icon_arrow_right_normal = loadDrawableFromAsset("skin_icon_arrow_right_normal.png", ctx);
        if (skin_black == null) skin_black = ColorStateList.valueOf(0xFF000000);
        if (skin_red == null) skin_red = ColorStateList.valueOf(Color.argb(255, 255, 70, 41));
        if (skin_gray3 == null) skin_gray3 = ColorStateList.valueOf(Color.argb(255, 128, 128, 128));
        if (skin_blue == null) skin_blue = ColorStateList.valueOf(Color.argb(255, 0, 182, 249));
        if (qq_setting_item_bg_nor == null)
            qq_setting_item_bg_nor = new ColorDrawable(Color.argb(255, 249, 249, 251));
        if (qq_setting_item_bg_pre == null)//
            qq_setting_item_bg_pre = new ColorDrawable(Color.argb(255, 192, 192, 192));
        if (skin_background == null) skin_background = new ColorDrawable(Color.argb(255, 240, 240, 240));
    }

    public static byte[] readAllOrNull(String s) {
        try {
            return readAll(s);
        } catch (IOException e) {
            if (!(e instanceof FileNotFoundException)) log(e);
            return null;
        }
    }

    public static byte[] readAll(String path) throws IOException {
        byte[] buf = new byte[1024];
        File f = new File(path);
        FileInputStream fin = new FileInputStream(f);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = fin.read(buf)) > 0) {
            baos.write(buf, 0, i);
        }
        fin.close();
        return baos.toByteArray();
    }

    public static void loadThemeByArsc(Context ctx, boolean load) {
        if (ctx == null) return;
        Field[] fields = ResUtils.class.getDeclaredFields();
        Resources arsc = ctx.getResources();
        String name;
        boolean success;
        for (Field f : fields) {
            name = f.getName();
            if (!f.getName().contains("_")) continue;
            success = false;
            Class clz = f.getType();
            int id;
            if (clz.equals(Drawable.class)) {
                id = ArscKit.getIdentifier(ctx, "drawable", name, true);
                if (load) {
                    if (id != 0) {
                        try {
                            Drawable ret = arsc.getDrawable(id);
                            f.set(null, ret);
                            success = true;
                        } catch (Exception e) {
                            log(e);
                        }
                    }
                } else {
                    success = id != 0;
                }
            } else if (clz.equals(ColorStateList.class)) {
                id = ArscKit.getIdentifier(ctx, "color", name, true);
                if (load) {
                    if (id != 0) {
                        try {
                            ColorStateList ret = arsc.getColorStateList(id);
                            f.set(null, ret);
                            success = true;
                        } catch (Exception e) {
                            log(e);
                        }
                    }
                } else {
                    success = id != 0;
                }
            }
            if (!success) {
                if (DEBUG) log("Missing res: " + name);
            }
        }
    }

    public static StateListDrawable getListItemBackground() {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, skin_list_item_pressed);
        //sd.addState(new int[]{android.R.attr.state_focused},skin_list_pressed);  
        sd.addState(new int[]{android.R.attr.state_selected}, skin_list_item_pressed);
        sd.addState(new int[]{}, skin_list_item_normal);
        return sd;
    }

    public static StateListDrawable getCheckBoxBackground() {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_checked, -android.R.attr.state_enabled}, list_checkbox_selected_nopress);
        sd.addState(new int[]{-android.R.attr.state_checked, -android.R.attr.state_enabled}, list_checkbox_multi);
        sd.addState(new int[]{android.R.attr.state_checked}, list_checkbox_selected);
        sd.addState(new int[]{}, list_checkbox);
        return sd;
    }

    public static InputStream openAsset(String name) {
        return ResUtils.class.getClassLoader().getResourceAsStream("assets/" + name);
    }

    public static String findDrawableResource(String dir, String name, Context mContext) {
        DisplayMetrics metric = new DisplayMetrics();
        ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        int height = metric.heightPixels; // 屏幕高度（像素）
        float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        String dpistr = "nodpi";//wtf
        if (densityDpi < 140) dpistr = "ldpi";//120
        else if (densityDpi < 200) dpistr = "mdpi";//160
        else if (densityDpi < 260) dpistr = "hdpi";//240
        else if (densityDpi < 300) dpistr = "xhdpi";//320
        else dpistr = "xxhdpi";//480
        String path = dir + "/drawable-" + dpistr + "/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-xhdpi/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-xxhdpi/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-hdpi/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-mdpi/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-ldpi/" + name;
        if (new File(path).exists()) return path;
        path = dir + "/drawable-nodpi/" + name;
        if (new File(path).exists()) return path;
        return null;
    }

    public static Drawable loadDrawableFromAsset(String name, Context mContext) {
        if (mContext != null)
            return loadDrawableFromAsset(name, mContext.getResources(), mContext);
        else return loadDrawableFromAsset(name, null, null);
    }

    public static Drawable loadDrawableFromStream(InputStream in, String name, @Nullable Resources res) {
        Drawable ret;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
			/*
			 // 取得想要缩放的matrix参数
			 Matrix matrix = new Matrix();
			 matrix.postScale(1.32f, 1.32f);
			 // 得到新的图片
			 Bitmap bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix,
			 true);
			 *
			 log("den="+res.getDisplayMetrics().density);
			 log("sden="+res.getDisplayMetrics().scaledDensity);
			 log("denDpi="+res.getDisplayMetrics().densityDpi);
			 //log("den="+res.getDisplayMetrics());*/
            bitmap.setDensity(320);// qq has xhdpi
            //log(name+"BiHeight:"+bitmap.getHeight());
            byte[] chunk = bitmap.getNinePatchChunk();
            //log("Res == "+res);
            if (NinePatch.isNinePatchChunk(chunk)) {
                Class clz = load("com/tencent/theme/SkinnableNinePatchDrawable");
                ret = (Drawable) XposedHelpers.findConstructorBestMatch(clz, Resources.class, Bitmap.class, byte[].class, Rect.class, String.class)
                        .newInstance(res, bitmap, chunk, new Rect(), name);
            } else {
                ret = new BitmapDrawable(res, bitmap);
            }
            //log(name+"DrHiMin="+ret.getMinimumHeight());
            return ret.mutate();
        } catch (Exception e) {
            log(e);
        }
        return null;
    }

    public static Drawable loadDrawableFromAsset(String name, @Nullable Resources res, Context mContext) {
        Drawable ret;
        if ((ret = cachedDrawable.get(name)) != null) return ret;
        try {
            if (res == null && mContext != null) res = mContext.getResources();
            //log(res + "is not null");
            InputStream fin = openAsset(name);
            ret = loadDrawableFromStream(fin, name, res);
            cachedDrawable.put(name, ret);
            return ret;
        } catch (Exception e) {
            log(e);
        }
        return null;
    }

    public static Drawable loadDrawable(String path, Context mContext) {
        Drawable ret;
        if ((ret = cachedDrawable.get(path)) != null) return ret;
        try {
            Resources res = mContext.getResources();
            FileInputStream fin = new FileInputStream(path);
            ret = loadDrawableFromStream(fin, path, res);
            cachedDrawable.put(path, ret);
            return ret;
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    /* unsigned byte to int */
    public static int ub2i(byte b) {
        return b < 0 ? (b + 256) : b;
    }

    public static ColorStateList cloneColor(ColorStateList color) {
        if (!color.getClass().equals(ColorStateList.class)) return color;
        int[] mColors = (int[]) iget_object_or_null(color, "mColors");
        int[][] mStateSpecs = (int[][]) iget_object_or_null(color, "mStateSpecs");
        return new ColorStateList(mStateSpecs, mColors);
    }
}

