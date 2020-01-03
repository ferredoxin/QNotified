package nil.nadph.qnotified.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import org.xmlpull.v1.XmlPullParser;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class QThemeKit {

    static private String cachedThemeId;

    static public ColorStateList skin_gray3;
    static public ColorStateList skin_black;
    static public ColorStateList skin_red;
    static public ColorStateList skin_blue;
    static public Drawable qq_setting_item_bg_nor;
    static public Drawable qq_setting_item_bg_pre;
    static public Drawable skin_list_item_normal = null, skin_list_item_unread = null, skin_list_item_pressed = null, skin_icon_arrow_right_normal = null, skin_background = null,
            list_checkbox_selected_nopress, list_checkbox_selected, list_checkbox_multi, list_checkbox;
    /*skin_group_list_item_pressed_theme_version2*/
    //static public Drawable skin_tips_newmessage;

    static private Map<String, Drawable> cachedDrawable = new HashMap<>();

    public static <SkinnableNinePatchDrawable extends Drawable> void initTheme(Context ctx) throws Throwable {
        String themeId = (String) invoke_static(load("com/tencent/mobileqq/theme/ThemeUtil"), "getUserCurrentThemeId", null, load("mqq/app/AppRuntime"));
        //ThemeUtil$ThemeInfo themeInfo=(ThemeUtil$ThemeInfo)invoke_static(load("com/tencent/mobileqq/theme/ThemeUtil"),"getThemeInfo",ctx,themeId,Context.class,String.class);
        //load("com/tencent/mobileqq/theme/ThemeUtil$ThemeInfo").getField(
		/*ClazzExplorer ce=ClazzExplorer.get();
		 ce.rootEle=ce.currEle=themeInfo;
		 ce.track.removeAllElements();
		 ce.init(ctx);//(Activity)ce.getCurrentActivity());//*/
        if (themeId.equals(cachedThemeId)) return;
        skin_gray3
                = skin_black
                = skin_red
                = skin_blue = null;
        qq_setting_item_bg_nor
                = qq_setting_item_bg_pre = null;
        skin_list_item_normal = skin_list_item_unread = skin_list_item_pressed
                = skin_background = null;//=skin_tips_newmessage=null;
        list_checkbox_selected_nopress = list_checkbox_selected = list_checkbox_multi = list_checkbox = null;
        initThemeByArsc(ctx);
        if (isColorQQThemeActive()) {
            loadResInDir(Environment.getExternalStorageDirectory() + "/QQColor/theme", ctx);
        }
        loadResInDir(locateThemeDir(themeId, ctx), ctx);
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

    public static void loadResInDir(String dir, Context mContext) {
        if (dir == null) {
            //log("Unable to locate theme dir!");
            //damn!
        } else {
            String path = null;
            if (skin_list_item_normal == null) {
                path = findDrawableResource(dir, "skin_list_item_normal.9.png", mContext);
                if (path == null)
                    path = findDrawableResource(dir, "skin_list_item_normal_theme_version2.9.png", mContext);
                if (path != null) skin_list_item_normal = loadDrawable(path, mContext);
            }
            if (skin_list_item_pressed == null) {
                path = findDrawableResource(dir, "skin_list_item_pressed.9.png", mContext);
                if (path == null)
                    path = findDrawableResource(dir, "skin_list_item_pressed_theme_version2.9.png", mContext);
                if (path != null) skin_list_item_pressed = loadDrawable(path, mContext);
            }
            if (skin_icon_arrow_right_normal == null) {
                path = findDrawableResource(dir, "skin_icon_arrow_right_normal.png", mContext);
                if (path == null)
                    path = findDrawableResource(dir, "skin_icon_arrow_right_normal_theme_version2.png", mContext);
                if (path != null) skin_icon_arrow_right_normal = loadDrawable(path, mContext);
            }
            if (skin_background == null) {
                path = findDrawableResource(dir, "skin_background.png", mContext);
                if (path == null) path = findDrawableResource(dir, "skin_background_theme_version2.png", mContext);
                if (path != null) skin_background = loadDrawable(path, mContext);
            }

            if (list_checkbox_selected_nopress == null) {
                path = findDrawableResource(dir, "list_checkbox_selected_nopress.png", mContext);
                if (path != null) list_checkbox_selected_nopress = loadDrawable(path, mContext);
            }

            if (list_checkbox_multi == null) {
                path = findDrawableResource(dir, "list_checkbox_multi.png", mContext);
                if (path != null) list_checkbox_multi = loadDrawable(path, mContext);
            }
            if (list_checkbox_selected == null) {
                path = findDrawableResource(dir, "list_checkbox_selected.png", mContext);
                if (path != null) list_checkbox_selected = loadDrawable(path, mContext);
            }
            if (list_checkbox == null) {
                path = findDrawableResource(dir, "list_checkbox.png", mContext);
                if (path != null) list_checkbox = loadDrawable(path, mContext);
            }

            //path=findDrawableResource(dir,"skin_tips_newmessage.9.png");
            //if(path!=null)skin_tips_newmessage=loadDrawable(path);
            path = dir + "/color/";
            if (skin_black == null) skin_black = getStateColorInXml(path + "skin_black.xml");
            if (skin_red == null) skin_red = getStateColorInXml(path + "skin_red.xml");
            if (skin_gray3 == null) skin_gray3 = getStateColorInXml(path + "skin_gray3.xml");
            if (qq_setting_item_bg_nor == null) {
                try {
                    byte[] buf = readAllOrNull(path + "qq_setting_item_bg_nor.xml");
                    if (buf != null) {
                        qq_setting_item_bg_nor = axml2Drawable(buf);
                    }
                } catch (Exception e) {
                    log(e);
                }
            }
            if (qq_setting_item_bg_pre == null) {
                try {
                    byte[] buf = readAllOrNull(path + "qq_setting_item_bg_pre.xml");
                    if (buf != null) {
                        qq_setting_item_bg_pre = axml2Drawable(buf);
                    }
                } catch (Exception e) {
                    log(e);
                }
            }
        }

		/*Resources res=ctx.getResources();
		 String pkg="com.tencent.mobileqq";
		 //skin_tips_newmessage.se
		 log("inner:"+skin_tips_newmessage.getIntrinsicHeight());
		 skin_tips_newmessage=res.getDrawable(0x7F0220E7);
		 byte[] bt=(byte[])iget_object(iget_object(iget_object(skin_tips_newmessage,"b"),"r"),"mBuffer");
		 /*try{
		 log("blen="+bt.length);
		 FileOutputStream fout=new FileOutputStream("/sdcard/qq_dump_buffer.o");
		 fout.write(bt,0,bt.length);
		 fout.flush();
		 fout.close();
		 fout=new FileOutputStream("/sdcard/qq_dump_ninebuffer.o");
		 bt=(byte[])iget_object(iget_object(iget_object(skin_tips_newmessage,"b"),"r"),"mNinePatchChunk");

		 fout.write(bt,0,bt.length);
		 fout.flush();
		 fout.close();
		 //log("wtf="+BitmapFactory.decodeByteArray(bt,0,bt.length).getHeight());
		 //log("Den="+bt.getDensity());
		 }catch(Exception e){
		 log(e.toString());
		 }
		 log("official:"+skin_tips_newmessage.getIntrinsicHeight());
		 //ClazzExplorer ce=ClazzExplorer.get();
		 //ce.init((Activity)ctx);
		 //ce.currEle=ce.rootEle=skin_tips_newmessage;

		 skin_list_normal=res.getDrawable(res.getIdentifier("skin_list_item_normal","drawable",pkg));
		 skin_list_pressed=res.getDrawable(res.getIdentifier("skin_list_item_pressed","drawable",pkg));
		 skin_tips_newmessage=res.getDrawable(res.getIdentifier("skin_tips_newmessage","drawable",pkg));
		 skin_black=res.getColorStateList(res.getIdentifier("skin_black","color",pkg));
		 skin_red=res.getColorStateList(res.getIdentifier("skin_red","color",pkg));
		 skin_gray3=res.getColorStateList(res.getIdentifier("skin_gray3","color",pkg));
		 skin_text_white=res.getColorStateList(res.getIdentifier("skin_black_item","color",pkg));
		 qq_setting_item_bg_nor=res.getColorStateList(res.getIdentifier("qq_setting_item_bg_nor","color",pkg));
		 qq_setting_item_bg_pre=res.getColorStateList(res.getIdentifier("qq_setting_item_bg_pre","color",pkg));
		 //*/
    }

    @SuppressLint({"PrivateApi", "DiscouragedPrivateApi"})
    private static XmlPullParser getParser(byte[] data) {
        try {
            Class clazz = Class.forName("android.content.res.XmlBlock");
            Constructor constructor = clazz.getDeclaredConstructor(byte[].class);
            constructor.setAccessible(true);
            Object block = constructor.newInstance(data);
            Method m = clazz.getDeclaredMethod("newParser");
            m.setAccessible(true);
            return (XmlPullParser) m.invoke(block);
        } catch (Throwable e) {
            log(e);
            return null;
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

    @Nullable
    private static Drawable axml2Drawable(byte[] file) {
        XmlPullParser parser = getParser(file);
        if (parser == null) return null;
        log(new RuntimeException("Stub!"));
        return null;
    }

    public static boolean isColorQQThemeActive() {
        //if (true) return false;
        try {
            if (!getApplication().getApplicationInfo().packageName.equals(PACKAGE_NAME_QQ)) return false;
            File f = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/QQColor/setting.xml");
            if (!f.exists())
                f = new File(Environment.getDataDirectory() + "/data/me.qiwu.colorqq/shared_prefs/me.qiwu.colorqq.xml");
            XSharedPreferences sp = new XSharedPreferences(f);
            sp.makeWorldReadable();
            sp.reload();
            return sp.getBoolean("theme_use_theme", false) && !sp.getBoolean("module_stophook", false);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private static void initThemeByArsc(Context ctx) {
        if (ctx == null) return;
        Field[] fields = QThemeKit.class.getDeclaredFields();
        Resources arsc = ctx.getResources();
        String name;
        boolean success;
        for (Field f : fields) {
            name = f.getName();
            if (!f.getName().contains("_")) continue;
            success = false;
            int id = arsc.getIdentifier(name, "color", ctx.getPackageName());
            if (id != 0) {
                try {
                    ColorStateList ret = arsc.getColorStateList(id);
                    f.set(null, ret);
                    success = true;
                } catch (Exception e) {
                    log(e);
                }
            }
            if (!success) {
                id = arsc.getIdentifier(name, "drawable", ctx.getPackageName());
                if (id != 0) {
                    try {
                        Drawable ret = arsc.getDrawable(id);
                        f.set(null, ret);
                        success = true;
                    } catch (Exception e) {
                        log(e);
                    }
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
        return QThemeKit.class.getClassLoader().getResourceAsStream("assets/" + name);
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


    private static String locateThemeDir(String themeId, Context ctx) {
        File themesDir = ctx.getDir("mobileqq_theme", Context.MODE_PRIVATE);
        //if(!themesDir.isDirectory())return null;
        File[] themes = themesDir.listFiles();
        for (int i = 0; i < themes.length; i++) {
            //log(themes[i].getName());
            if (themes[i].getName().startsWith(themeId + "_") && themes[i].isDirectory()) {
                return themes[i].getAbsolutePath();
            }
        }
        return null;
    }

    @Deprecated
    public static ColorStateList getStateColorInXml(String file) {
        //log("load xml:" + file);
        byte[] byteSrc = null;
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = fis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            byteSrc = bos.toByteArray();
            fis.close();
            bos.close();
            //}catch(Exception e){
            //Utils.log("parse xml error:"+e.toString());
            if (byteSrc == null) throw new IOException(file);
            BinaryXmlParser.XmlNode selector = BinaryXmlParser.parseXml(byteSrc);
            int[] colors = new int[selector.elements.size()];
            int[][] states = new int[selector.elements.size()][];
            BinaryXmlParser.XmlNode item;
            for (int i = 0; i < selector.elements.size(); i++) {
                item = selector.elements.get(i);
                colors[i] = (Integer) item.attributes.get("color").data;
                int si = item.attributes.size() - 1;
                IntArray ss = new IntArray();
                Map.Entry<String, BinaryXmlParser.XmlNode.Res> entry;
                Iterator<Map.Entry<String, BinaryXmlParser.XmlNode.Res>> it = item.attributes.entrySet().iterator();
                int id = 0;
                while (it.hasNext()) {
                    entry = it.next();
                    if (!entry.getKey().startsWith("state_")) continue;
                    id = (int) Utils.sget_object(android.R.attr.class, entry.getKey());
                    ss.add(id * ((entry.getValue().data) == 0 ? -1 : 1));
                }
                states[i] = ss.toArray();
            }
            return new ColorStateList(states, colors);
        } catch (Exception e) {
            if (!(e instanceof FileNotFoundException)) log(e);
            try {
                fis.close();
                bos.close();
            } catch (Exception e2) {
            }
        }
        return null;
    }//*/

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

