/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.util.ArscKit;
import nil.nadph.qnotified.util.Nullable;
import nil.nadph.qnotified.util.Utils;

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
    static public ColorStateList skin_tips;
    static public Drawable bg_texture;
    static public Drawable skin_list_item_normal = null, skin_list_item_unread = null, skin_list_item_pressed = null;
    static public Drawable list_checkbox_selected_nopress, list_checkbox_selected, list_checkbox_multi, list_checkbox;
    static public Drawable skin_icon_arrow_right_normal = null, skin_background = null;
    static public ColorStateList skin_color_button_blue;
    static public Drawable skin_common_btn_blue_pressed, skin_common_btn_blue_unpressed;

    static private boolean inited = false;
    static private String cachedThemeId;
    static private final Map<String, Drawable> cachedDrawable = new HashMap<>();

    public static void requireResourcesNonNull(Context ctx) {
        if (ctx == null) ctx = Utils.getApplication();
        if (!inited) initTheme(ctx);
    }

    public static void initTheme(Context ctx) {
        try {
            String themeId = (String) invoke_static(load("com/tencent/mobileqq/theme/ThemeUtil"), "getUserCurrentThemeId", null, load("mqq/app/AppRuntime"));
            if (themeId.equals(cachedThemeId)) return;
            cachedThemeId = themeId;
        } catch (Exception e) {
            log(e);
        }
        skin_gray3
                = skin_black
                = skin_red
                = skin_blue = skin_tips = null;
        skin_list_item_normal = skin_list_item_unread = skin_list_item_pressed
                = skin_background = null;
        list_checkbox_selected_nopress = list_checkbox_selected = list_checkbox_multi = list_checkbox = null;
        loadThemeByArsc(ctx, true);
        initByFallback(ctx);
        inited = true;
    }

    private static void initByFallback(Context ctx) {
        if (skin_list_item_normal == null)
            skin_list_item_normal = loadDrawableFromAsset("skin_list_item_normal.9.png", ctx);
        if (skin_list_item_pressed == null)
            skin_list_item_pressed = loadDrawableFromAsset("skin_list_item_pressed.9.png", ctx);
        if (list_checkbox_selected_nopress == null)
            list_checkbox_selected_nopress = loadDrawableFromAsset("list_checkbox_selected_nopress.png", ctx);
        if (list_checkbox_selected == null)
            list_checkbox_selected = loadDrawableFromAsset("list_checkbox_selected.png", ctx);
        if (list_checkbox_multi == null)
            list_checkbox_multi = loadDrawableFromAsset("list_checkbox_multi.png", ctx);
        if (list_checkbox == null) list_checkbox = loadDrawableFromAsset("list_checkbox.png", ctx);
        if (skin_icon_arrow_right_normal == null)
            skin_icon_arrow_right_normal = loadDrawableFromAsset("skin_icon_arrow_right_normal.png", ctx);
        if (skin_black == null) skin_black = ColorStateList.valueOf(0xFF000000);
        if (skin_tips == null) skin_tips = ColorStateList.valueOf(0xFFFFFFFF);
        if (skin_red == null) skin_red = ColorStateList.valueOf(Color.argb(255, 255, 70, 41));
        if (skin_gray3 == null) skin_gray3 = ColorStateList.valueOf(Color.argb(255, 128, 128, 128));
        if (skin_blue == null) skin_blue = ColorStateList.valueOf(Color.argb(255, 0, 182, 249));
        if (skin_background == null)
            skin_background = new ColorDrawable(Color.argb(255, 240, 240, 240));
        if (skin_common_btn_blue_unpressed == null || skin_common_btn_blue_pressed == null || skin_color_button_blue == null) {
            skin_common_btn_blue_pressed = new ColorDrawable(Color.argb(255, 16, 80, 210));
            skin_common_btn_blue_unpressed = new ColorDrawable(Color.argb(255, 20, 100, 255));
            skin_color_button_blue = ColorStateList.valueOf(Color.argb(255, 255, 255, 255));
        }
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

    public static byte[] readAll(InputStream in) throws IOException {
        byte[] buf = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int i;
        while ((i = in.read(buf)) > 0) {
            baos.write(buf, 0, i);
        }
        in.close();
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
            Class<?> clz = f.getType();
            int id;
            if (clz.equals(Drawable.class)) {
                id = ArscKit.getIdentifier(ctx, "drawable", name, true);
                if (load) {
                    if (id != 0) {
                        try {
                            Drawable ret = ContextCompat.getDrawable(ctx,id);
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
                            ColorStateList ret = ContextCompat.getColorStateList(ctx,id);
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
                logd("Missing res: " + name);
            }
        }
    }

    public static StateListDrawable getDialogClickableItemBackground() {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, new HcbBackgroundDrawable(0x40808080));
        sd.addState(new int[]{}, new DummyDrawable());
        return sd;
    }

    public static StateListDrawable getCommonBtnBlueBackground() {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, skin_common_btn_blue_pressed);
        sd.addState(new int[]{}, skin_common_btn_blue_unpressed);
        return sd;
    }

    public static void applyStyleCommonBtnBlue(Button btn) {
        //btn.setBackgroundDrawable(getCommonBtnBlueBackground());
        ViewCompat.setBackground(btn,getCommonBtnBlueBackground());
        btn.setTextColor(skin_color_button_blue);
        btn.setTextSize(17);
        btn.setMinHeight(dip2px(btn.getContext(), 42));
    }

    public static StateListDrawable getListItemBackground() {
        StateListDrawable sd = new StateListDrawable();
        sd.addState(new int[]{android.R.attr.state_pressed}, skin_list_item_pressed);
        //sd.addState(new int[]{android.R.attr.state_focused}, skin_list_item_unread);
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

    public static Drawable loadDrawableFromAsset(String name, Context mContext) {
        if (mContext != null)
            return loadDrawableFromAsset(name, mContext.getResources(), mContext);
        else return loadDrawableFromAsset(name, null, null);
    }

    public static Drawable loadDrawableFromStream(InputStream in, String name, @Nullable Resources res) {
        Drawable ret;
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(in);
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
        } catch (FileNotFoundException ignored) {
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

    public static boolean isInNightMode() {
        try {
            String themeId = (String) invoke_static(load("com/tencent/mobileqq/theme/ThemeUtil"), "getUserCurrentThemeId", getAppRuntime(), load("mqq/app/AppRuntime"));
            return "1103".endsWith(themeId) || "2920".endsWith(themeId);
        } catch (Exception e) {
            if (isTim(getApplication())) {
                return false;
            }
            log(e);
            return false;
        }
    }
}

