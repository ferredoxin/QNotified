package me.zpp0196.qqpurify.utils;

import android.app.Activity;
import android.content.Context;

import androidx.core.content.ContextCompat;

import java.io.IOException;

import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;

/**
 * Created by zpp0196 on 2019/5/18.
 */
public class ThemeUtils {

    public enum Themes {

        BLP(R.color.theme_color_blp, R.style.AppTheme_Blp, "哔哩粉"),
        GOL(R.color.theme_color_gol, R.style.AppTheme_Gol, "亮棕色"),
        CAG(R.color.theme_color_cag, R.style.AppTheme_Cag, "酷安绿"),
        FTB(R.color.theme_color_ftb, R.style.AppTheme_Ftb, "胖次蓝"),
        GHP(R.color.theme_color_ghp, R.style.AppTheme_Ghp, "亮紫色"),

        MAR(R.color.theme_color_mar, R.style.AppTheme_Mar, "姨妈红"),
        TPO(R.color.theme_color_tpo, R.style.AppTheme_TPO, "热带橙"),
        TLG(R.color.theme_color_tlg, R.style.AppTheme_Tlg, "水鸭青"),
        RYB(R.color.theme_color_ryb, R.style.AppTheme_Ryb, "皇室蓝"),
        GAP(R.color.theme_color_gap, R.style.AppTheme_Gap, "基佬紫");

        public int colorId;
        public int styleId;
        public String title;

        Themes(int colorId, int styleId, String title) {
            this.colorId = colorId;
            this.styleId = styleId;
            this.title = title;
        }
    }

    private static final Themes THEME_DEFAULT = Themes.FTB;
    private static Themes mTheme = THEME_DEFAULT;
    private static String mThemeTitle = "theme_title";

    public static void setColor(Activity activity, int color) {
        ThemeUtils.mTheme = color2Theme(activity, color);
        ConfigManager cfg = ConfigManager.getDefaultConfig();
        cfg.putString(mThemeTitle, mTheme.title);
        try {
            cfg.save();
        } catch (IOException ignored) {
        }
    }

    public static int getThemeColor(Context context) {
        return ContextCompat.getColor(context,mTheme.colorId);
        //return context.getResources().getColor(mTheme.colorId);
    }

    public static String getThemeTitle() {
        return mTheme.title;
    }

    public static int getStyleId(Context context) {
        String title = ConfigManager.getDefaultConfig().getStringOrDefault(mThemeTitle, mTheme.title);
        mTheme = title2Theme(title);
        return mTheme.styleId;
    }

    public static int[] getColors(Context context) {
        Themes[] themes = Themes.values();
        //Resources res = context.getResources();
        int[] colors = new int[themes.length];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = ContextCompat.getColor(context,themes[i].colorId);
            //colors[i] = res.getColor(themes[i].colorId);
        }
        return colors;
    }

    private static Themes color2Theme(Context context, int color) {
        Themes[] themes = Themes.values();
        for (Themes theme : themes) {
            if (ContextCompat.getColor(context,theme.colorId) == color) {
                return theme;
            }
        }
        return THEME_DEFAULT;
    }

    private static Themes title2Theme(String title) {
        Themes[] themes = Themes.values();
        for (Themes theme : themes) {
            if (theme.title.equals(title)) {
                return theme;
            }
        }
        return THEME_DEFAULT;
    }
}

