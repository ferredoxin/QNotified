package nil.nadph.qnotified.util;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XposedBridge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import static nil.nadph.qnotified.util.Initiator.load;

@SuppressWarnings("unchecked")
public class Utils {

    private Utils() {
        throw new AssertionError("No instance for you!");
    }

    public static final String qn_hide_msg_list_miniapp = "qn_hide_msg_list_miniapp",
            qn_hide_ex_entry_group = "qn_hide_ex_entry_group",
            qn_del_op_silence = "qn_del_op_silence",
            qn_enable_transparent = "qn_enable_transparent",
            qn_enable_ptt_forward = "qn_enable_ptt_forward",
            qn_sticker_as_pic = "qn_sticker_as_pic",
            qn_flash_as_pic = "qn_flash_as_pic",
            qn_send_card_msg = "qn_send_card_msg",
            qn_muted_at_all = "qn_muted_at_all",
            qn_muted_red_packet = "qn_muted_red_packet",
            qn_hide_gift_animation = "qn_hide_gift_animation",
            qn_sign_in_as_text = "qn_sign_in_as_text",
            qn_mute_talk_back = "qn_mute_talk_back",
            bug_repeater = "bug_repeater",
            qn_gallery_bg = "qn_gallery_bg",
            qqhelper_fav_more_emo = "qqhelper_fav_more_emo",
            qn_anti_revoke_msg = "qn_anti_revoke_msg";

    public static boolean DEBUG = true;
    public static boolean V_TOAST = true;
    public static final String QN_VERSION_NAME = "0.3.1test";
    public static final int QN_VERSION_CODE = 13;

    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi";
    public static final String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";

    public static final int TOAST_TYPE_INFO = 0;
    public static final int TOAST_TYPE_ERROR = 1;
    public static final int TOAST_TYPE_SUCCESS = 2;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static String getActiveModuleVersion() {
        Math.sqrt(1);
        Math.random();
        Math.expm1(0.001);
        //Let's make the function longer,so that it will work in VirtualXposed
        return null;
    }

    private static Handler mHandler;

    public static void runOnUiThread(Runnable r) {
        if (mHandler == null) mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(r);
    }

	/* Use Utils.getApplication() Instead *
	 @Deprecated()
	 @SuppressWarnings ("all")
	 public static Context getSystemContext() {
	 return (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread", new Object[0]), "getSystemContext", new Object[0]);
	 }*/

    public static Application getApplication() {
        Field f = null;
        try {
            Class clz = load("com/tencent/common/app/BaseApplicationImpl");
            f = hasField(clz, "sApplication");
            if (f == null) return (Application) sget_object(clz, "a", clz);
            else return (Application) f.get(null);
        } catch (Exception e) {
            log(e);
        }
        return null;
    }

    public static boolean isCallingFrom(String classname) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.toString().contains(classname)) {
                return true;
            }
        }
        return false;
    }

    public static PackageInfo getHostInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Throwable e) {
            Log.e("Utils", "Can not get PackageInfo!");
            return null;
        }
    }

    public static long getLongAccountUin() {
        try {
            return (long) invoke_virtual(getAppRuntime(), "getLongAccountUin");
        } catch (Exception e) {
            log(e);
        }
        return -1;
    }

    public static void ref_setText(View obj, CharSequence str) {
        try {
            Method m = obj.getClass().getMethod("setText", CharSequence.class);
            m.setAccessible(true);
            m.invoke(obj, str);
        } catch (Exception e) {
            log(e.toString());
        }
    }

    public static View.OnClickListener getOnClickListener(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getOnClickListenerV14(v);
        } else {
            return getOnClickListenerV(v);
        }
    }

	/*public static Object invoke_virtual(Object obj,String method,Object...args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException{
	 Class clazz=obj.getClass();
	 Method m=findMethodByArgs(clazz,method,args);
	 m.setAccessible(true);
	 return m.invoke(obj,args);
	 }*/

    public static Object invoke_virtual(Object obj, String name, Object... argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length)
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        int i, ii;
        Method m[] = null;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) continue loop;
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) continue;
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) throw new NoSuchMethodException(name + " in " + obj.getClass().getName());
        method.setAccessible(true);
        return method.invoke(obj, argv);
    }

    public static Method hasMethod(Object obj, String name, Object... argsTypesAndReturnType) throws IllegalArgumentException {
        Class clazz;
        if (obj == null) throw new NullPointerException("obj/clazz == null");
        if (obj instanceof Class) clazz = (Class) obj;
        else clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length)
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        int i, ii;
        Method m[] = null;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) continue loop;
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) continue;
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method != null) method.setAccessible(true);
        return method;
    }


    public static Object invoke_virtual_original(Object obj, String name, Object... argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length)
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        int i, ii;
        Method m[] = null;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) continue loop;
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) continue;
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) throw new NoSuchMethodException(name + " in " + obj.getClass().getName());
        method.setAccessible(true);
        Object ret = null;
        boolean needPatch = false;
        try {
            ret = XposedBridge.invokeOriginalMethod(method, obj, argv);
        } catch (IllegalStateException e) {
            //For S-EdXp: Method not hooked.
            needPatch = true;
        } catch (InvocationTargetException e) {
            //For TaiChi
            Throwable cause = e.getCause();
            if (cause instanceof NullPointerException) {
                String tr = android.util.Log.getStackTraceString(cause);
                if (tr.indexOf("ExposedBridge.invokeOriginalMethod") != 0 || tr.indexOf("ExposedBridge.invokeOriginalMethod") != 0)
                    needPatch = true;
            }
            if (!needPatch) throw e;
        }
        if (needPatch) ret = method.invoke(obj, argv);
        return ret;
    }

    public static Object invoke_static(Class staticClass, String name, Object... argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = staticClass;
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length)
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        int i, ii;
        Method m[] = null;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) continue loop;
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) continue;
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) throw new NoSuchMethodException(name);
        method.setAccessible(true);
        return method.invoke(null, argv);
    }

    public static Object new_instance(Class clazz, Object... argsAndTypes) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        int argc = argsAndTypes.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        int i;
        Constructor m = null;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsAndTypes[argc + i];
            argv[i] = argsAndTypes[i];
        }
        m = clazz.getDeclaredConstructor(argt);
        m.setAccessible(true);
        return m.newInstance(argv);
    }

    public static Object getQQAppInterface() {
        Object o = getAppRuntime();
        Log.i("QNdump", o.getClass().getCanonicalName());
        return o;
    }

    public static Object getMobileQQService() {
        return iget_object_or_null(getQQAppInterface(), "a", load("com/tencent/mobileqq/service/MobileQQService"));
    }

    public static Object createSessionInfo(String uin, int uinType) {
        Class clz = load("com/tencent/mobileqq/activity/aio/SessionInfo");
        if (clz == null) throw new NoClassDefFoundError("SessionInfo");
        try {
            Object obj = new_instance(clz);
            iput_object(obj, "a", String.class, uin);
            iput_object(obj, "a", int.class, uinType);
            return obj;
        } catch (InstantiationException e) {
        } catch (InvocationTargetException e) {
        } catch (SecurityException e) {
        } catch (IllegalAccessException e) {
        } catch (IllegalArgumentException e) {
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    public static String get_RGB(int color) {
        int r = 0xff & (color >> 16);
        int g = 0xff & (color >> 8);
        int b = 0xff & color;
        return "#" + byteStr(r) + byteStr(g) + byteStr(b);
    }

    public static String byteStr(int i) {
        String ret = Integer.toHexString(i);
        if (ret.length() == 1) return "0" + ret;
        else return ret;
    }

	/*
	 public static Method findMethodByArgs(Class mclazz,String name,Object...argv)throws NoSuchMethodException{
	 Method ret=null;
	 Method[] m;?

	 int i=0,ii=0;
	 Class clazz=mclazz;
	 Class argt[];
	 do{
	 m=clazz.getDeclaredMethods();
	 loop:for(i=0;i<m.length;i++){
	 if(m[i].getName().equals(name)){
	 argt=m[i].getParameterTypes();
	 if(argt.length==argv.length){
	 for(ii=0;ii<argt.length;ii++){
	 if(
	 argv[ii]==null&&argt[ii].isPrimitive())continue loop;
	 if(
	 }
	 }
	 }
	 }
	 }while(!Object.class.equals(clazz=clazz.getSuperclass()));
	 throw new NoSuchMethodException(name+"@"+mclazz);
	 }*/

    //Used for APIs lower than ICS (API 14)
    private static View.OnClickListener getOnClickListenerV(View view) {
        View.OnClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        Field field;
        try {
            field = Class.forName(viewStr).getDeclaredField("mOnClickListener");
            retrievedListener = (View.OnClickListener) field.get(view);
        } catch (NoSuchFieldException ex) {
            log("Reflection: No Such Field.");
        } catch (IllegalAccessException ex) {
            log("Reflection: Illegal Access.");
        } catch (ClassNotFoundException ex) {
            log("Reflection: Class Not Found.");
        }
        return retrievedListener;
    }

    //Used for new ListenerInfo class structure used beginning with API 14 (ICS)
    private static View.OnClickListener getOnClickListenerV14(View view) {
        View.OnClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        String lInfoStr = "android.view.View$ListenerInfo";

        try {
            Field listenerField = Class.forName(viewStr).getDeclaredField("mListenerInfo");
            Object listenerInfo = null;

            if (listenerField != null) {
                listenerField.setAccessible(true);
                listenerInfo = listenerField.get(view);
            }

            Field clickListenerField = Class.forName(lInfoStr).getDeclaredField("mOnClickListener");

            if (clickListenerField != null && listenerInfo != null) {
                retrievedListener = (View.OnClickListener) clickListenerField.get(listenerInfo);
            }
        } catch (NoSuchFieldException ex) {
            log("Reflection: No Such Field.");
        } catch (IllegalAccessException ex) {
            log("Reflection: Illegal Access.");
        } catch (ClassNotFoundException ex) {
            log("Reflection: Class Not Found.");
        }

        return retrievedListener;
    }

    public static <T> T _obj_clone(T obj) {
        try {
            Class<T> clazz = (Class<T>) obj.getClass();
            T ret = clazz.newInstance();
            Field[] f;
            int i;
            while (!Object.class.equals(clazz)) {
                f = clazz.getDeclaredFields();
                for (i = 0; i < f.length; i++) {
                    f[i].setAccessible(true);
                    f[i].set(ret, f[i].get(obj));
                }
                clazz = (Class<T>) clazz.getSuperclass();
            }
            return ret;
        } catch (Throwable e) {
            log("CLONE : " + e.toString());
        }
        return null;
    }

    public static <T extends View> T _view_clone(T obj) {
        try {
            Class<T> clazz = (Class<T>) obj.getClass();
            T ret = clazz.getConstructor(Context.class).newInstance(obj.getContext());
            Field[] f;
            int i;
            while (!Object.class.equals(clazz)) {
                f = clazz.getDeclaredFields();
                for (i = 0; i < f.length; i++) {
                    f[i].setAccessible(true);
                    f[i].set(ret, f[i].get(obj));
                }
                clazz = (Class<T>) clazz.getSuperclass();
            }
            return ret;
        } catch (Throwable e) {
            log("CLONE : " + e.toString());
        }
        return null;
    }

    public static Object sget_object(Class clazz, String name) {
        return sget_object(clazz, name, null);
    }

    public static Object sget_object(Class clazz, String name, Class type) {
        try {
            Field f = findField(clazz, type, name);
            f.setAccessible(true);
            return f.get(null);
        } catch (Exception e) {
            log(e);
        }
        return null;
    }


    public static Object iget_object_or_null(Object obj, String name) {
        return iget_object_or_null(obj, name, null);
    }

    public static Object iget_object_or_null(Object obj, String name, Class type) {
        Class clazz = obj.getClass();
        try {
            Field f = findField(clazz, type, name);
            f.setAccessible(true);
            return f.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

    public static void iput_object(Object obj, String name, Object value) {
        iput_object(obj, name, null, value);
    }

    public static void iput_object(Object obj, String name, Class type, Object value) {
        Class clazz = obj.getClass();
        try {
            Field f = findField(clazz, type, name);
            f.setAccessible(true);
            f.set(obj, value);
        } catch (Exception e) {
            log(e);
        }
    }

    public static void sput_object(Class clz, String name, Object value) {
        sput_object(clz, name, null, value);
    }

    public static void sput_object(Class clazz, String name, Class type, Object value) {
        try {
            Field f = findField(clazz, type, name);
            f.setAccessible(true);
            f.set(null, value);
        } catch (Exception e) {
            log(e);
        }
    }

    public static Object getAppRuntime() {
        Object baseApplicationImpl = getApplication();
        try {
            Method m;
            m = hasMethod(baseApplicationImpl, "getRuntime");
            if (m == null) return invoke_virtual(baseApplicationImpl, "a", load("mqq/app/AppRuntime"));
            else return m.invoke(baseApplicationImpl);
        } catch (Exception e) {
            //log("getRuntime:" + e.toString());
            return null;
        }
    }

    public static String getAccount() {
        Object rt = getAppRuntime();
        try {
            return (String) invoke_virtual(rt, "getAccount");
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static Object getFriendListHandler() {
        return getBusinessHandler(1);
    }


    public static Object getBusinessHandler(int type) {
        try {
            Class cl_bh = load("com/tencent/mobileqq/app/BusinessHandler");
            if (cl_bh == null) {
                Class cl_flh = load("com/tencent/mobileqq/app/FriendListHandler");
                assert cl_flh != null;
                cl_bh = cl_flh.getSuperclass();
            }
            Object ret = invoke_virtual(getQQAppInterface(), "a", type, int.class, cl_bh);
            //log("bh(" + type + ")=" + ret);
            return ret;
        } catch (Exception e) {
            log(e);
            return null;
        }

    }

    /**
     * NSF: Neither Static nor Final
     *
     * @param obj  thisObj
     * @param type Field type
     * @return the FIRST(as declared seq in dex) field value meeting the type
     */
    @Deprecated
    public static Object getFirstNSFByType(Object obj, Class type) {
        if (obj == null) throw new NullPointerException("obj == null");
        if (type == null) throw new NullPointerException("type == null");
        Class clz = obj.getClass();
        while (clz != null && !clz.equals(Object.class)) {
            for (Field f : clz.getDeclaredFields()) {
                if (!f.getType().equals(type)) continue;
                int m = f.getModifiers();
                if (Modifier.isStatic(m) || Modifier.isFinal(m)) continue;
                f.setAccessible(true);
                try {
                    return f.get(obj);
                } catch (IllegalAccessException ignored) {
                    //should not happen
                }
            }
            clz = clz.getSuperclass();
        }
        return null;
    }

    public static <T> T getObject(Class clazz, Class<?> type, String name, Object obj) {
        try {
            Field field = findField(clazz, type, name);
            return field == null ? null : (T) field.get(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static Field hasField(Object obj, String name) {
        return hasField(obj, name, null);
    }

    public static Field hasField(Object obj, String name, Class type) {
        if (obj == null) throw new NullPointerException("obj/class == null");
        Class clazz;
        if (obj instanceof Class) clazz = (Class) obj;
        else clazz = obj.getClass();
        return findField(clazz, type, name);
    }

    public static Field findField(Class<?> clazz, Class<?> type, String name) {
        if (clazz != null && !name.isEmpty()) {
            Class<?> clz = clazz;
            do {
                for (Field field : clz.getDeclaredFields()) {
                    if ((type == null || field.getType().equals(type)) && field.getName()
                            .equals(name)) {
                        field.setAccessible(true);
                        return field;
                    }
                }
            } while ((clz = clz.getSuperclass()) != null);
            //log(String.format("Can't find the field of type: %s and name: %s in class: %s!",type==null?"[any]":type.getName(),name,clazz.getName()));
        }
        return null;
    }

    public static void log(String str) {
        Log.i("QNdump", str);
        if (DEBUG) try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.i("Xposed", str);
            Log.i("EdXposed-Bridge", str);

        }
        if (V_TOAST) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qn_log.txt";
            File f = new File(path);
            try {
                if (!f.exists()) f.createNewFile();
                method2(path, "[" + System.currentTimeMillis() + "-" + android.os.Process.myPid() + "] " + str + "\n");
            } catch (IOException e) {
            }
        }
    }

    public static void log(Throwable th) {
        log(Log.getStackTraceString(th));
    }


    public static void checkLogFlag() {
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.qn_log_flag");
            if (f.exists()) V_TOAST = true;
        } catch (Exception ignored) {
        }
    }

    /**
     * 追加文件：使用FileWriter
     *
     * @param fileName
     * @param content
     */
    public static void method2(String fileName, String content) {
        FileWriter writer = null;
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件     
            writer = new FileWriter(fileName, true);
            writer.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String en(String str) {
        if (str == null) return "null";
        return "\"" + str.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\\n").replace("\r", "\\\r") + "\"";
    }

    public static String de(String str) {
        if (str == null) return null;
        if (str.equals("null")) return null;
        if (str.startsWith("\"")) str = str.substring(1);
        if (str.endsWith("\"") && !str.endsWith("\\\"")) str = str.substring(0, str.length() - 1);
        return str.replace("\\\"", "\"").replace("\\\n", "\n")
                .replace("\\\r", "\r").replace("\\\\", "\\");
    }

    private static Method method_Toast_show;
    private static Method method_Toast_makeText;
    private static Class clazz_QQToast;


    public static Toast showToast(Context ctx, int type, CharSequence str, int length) throws Throwable {
        if (clazz_QQToast == null) {
            clazz_QQToast = load("com/tencent/mobileqq/widget/QQToast");
        }
        if (clazz_QQToast == null) {
            Class clz = load("com/tencent/mobileqq/activity/aio/doodle/DoodleLayout");
            assert clz != null;
            Field[] fs = clz.getDeclaredFields();
            for (int i = 0; i < fs.length; i++) {
                if (View.class.isAssignableFrom(fs[i].getType())) continue;
                if (fs[i].getType().isPrimitive()) continue;
                if (fs[i].getType().isInterface()) continue;
                clazz_QQToast = fs[i].getType();
            }
        }
        if (method_Toast_show == null) {
            Method[] ms = clazz_QQToast.getMethods();
            for (int i = 0; i < ms.length; i++) {
                if (Toast.class.equals(ms[i].getReturnType()) && ms[i].getParameterTypes().length == 0) {
                    method_Toast_show = ms[i];
                    break;
                }
            }
        }
        if (method_Toast_makeText == null) {
            method_Toast_makeText = clazz_QQToast.getMethod("a", Context.class, int.class, CharSequence.class, int.class);
        }
        Object qqToast = method_Toast_makeText.invoke(null, ctx, type, str, length);
        return (Toast) method_Toast_show.invoke(qqToast);
    }

    public static Toast showToastShort(Context ctx, CharSequence str) throws Throwable {
        return showToast(ctx, 0, str, 0);
    }

    public static void dumpTrace() {
        Throwable t = new Throwable("Trace dump");
        log(t);
    }

    public static int getLineNo() {
        return Thread.currentThread().getStackTrace()[3].getLineNumber();
    }

    public static int getLineNo(int depth) {
        return Thread.currentThread().getStackTrace()[3 + depth].getLineNumber();
    }

    public static String getRelTimeStrSec(long time_sec) {
        return getRelTimeStrMs(time_sec * 1000);
    }

    public static String getRelTimeStrMs(long time_ms) {
        SimpleDateFormat format;
        long curr = System.currentTimeMillis();
        Date now = new Date(curr);
        Date t = new Date(time_ms);
        if (t.getYear() != now.getYear()) {
            format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            return format.format(t);
        }
        if (t.getMonth() == now.getMonth() && t.getDay() == now.getDay()) {
            format = new SimpleDateFormat("HH:mm:ss");
            return format.format(t);
        }
        if ((curr - time_ms) / 1000f / 3600f / 24f < 6.0f) {
            format = new SimpleDateFormat(" HH:mm");
            return "星期" + new String[]{"日", "一", "二", "三", "四", "五", "六"}[t.getDay()] + format.format(t);
        }
        format = new SimpleDateFormat("MM-dd HH:mm");
        return format.format(t);
    }

    public static String getIntervalDspMs(long ms1, long ms2) {
        Date t1 = new Date(Math.min(ms1, ms2));
        Date t2 = new Date(Math.max(ms1, ms2));
        Date tn = new Date();
        SimpleDateFormat format;
        String ret;
        switch (difTimeMs(t1, tn)) {
            case 4:
            case 3:
            case 2:
                format = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case 1:
            case 0:
                format = new SimpleDateFormat("HH:mm");
                break;
            default:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }
        ret = format.format(t1);
        switch (difTimeMs(t1, t2)) {
            case 4:
            case 3:
            case 2:
                format = new SimpleDateFormat("MM-dd HH:mm");
                break;
            case 1:
            case 0:
                format = new SimpleDateFormat("HH:mm");
                break;
            default:
                format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                break;
        }
        ret = ret + " 至 " + format.format(t2);
        return ret;
    }

    /**
     * same: t0 d1 w2 m3 y4
     */
    private static int difTimeMs(Date t1, Date t2) {
        if (t1.getYear() != t2.getYear()) return 5;
        if (t1.getMonth() != t2.getMonth()) return 4;
        if (t1.getDate() != t2.getDate()) return 3;
        if (t1.equals(t2)) return 0;
        return 1;
    }

    /**
     * 通过view暴力获取getContext()(Android不支持view.getContext()了)
     *
     * @param view 要获取context的view
     * @return 返回一个activity
     */
    public static Context getContext(View view) {
        Context ctx = null;
        if (view.getContext().getClass().getName().contains("com.android.internal.policy.DecorContext")) {
            try {
                Field field = view.getContext().getClass().getDeclaredField("mPhoneWindow");
                field.setAccessible(true);
                Object obj = field.get(view.getContext());
                java.lang.reflect.Method m1 = obj.getClass().getMethod("getContext");
                ctx = (Context) (m1.invoke(obj));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ctx = view.getContext();
        }
        return ctx;
    }

    public static <T> T dump(T obj) {
        log("dump:" + obj);
        return obj;
    }

    private static Class clz_DialogUtil;
    private static Class clz_CustomDialog;

    public static Dialog createDialog(Context ctx) {
        if (clz_DialogUtil == null) {
            clz_DialogUtil = DexKit.doFindClass(DexKit.C_DIALOG_UTIL);
        }
        if (clz_CustomDialog == null) {
            clz_CustomDialog = load("com/tencent/mobileqq/utils/QQCustomDialog");
            if (clz_CustomDialog == null) {
                Class clz_Lite = load("com/dataline/activities/LiteActivity");
                Field[] fs = clz_Lite.getDeclaredFields();
                for (Field f : fs) {
                    if (Modifier.isPrivate(f.getModifiers()) && Dialog.class.equals(f.getType().getSuperclass())) {
                        clz_CustomDialog = f.getType();
                        break;
                    }
                }
            }
        }
        try {
            Dialog qQCustomDialog = (Dialog) invoke_static(clz_DialogUtil, "a", ctx, 230, Context.class, int.class, clz_CustomDialog);
            return qQCustomDialog;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static String getShort$Name(Object obj) {
        String name;
        if (obj == null) return "null";
        if (obj instanceof String) {
            name = ((String) obj).replace("/", ".");
        } else if (obj instanceof Class) {
            name = ((Class) obj).getName();
        } else if (obj instanceof Field) {
            name = ((Field) obj).getType().getName();
        } else name = obj.getClass().getName();
        if (!name.contains(".")) return name;
        int p = name.lastIndexOf('.');
        return name.substring(p + 1);
    }

    public static class DummyCallback implements DialogInterface.OnClickListener {
        public DummyCallback() {
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }

    }

    public static int sign(double d) {
        if (d == 0d) return 0;
        if (d > 0d) return 1;
        return -1;
    }

    public static Method getSuperMethod(Class clazz, String name, Class... params) {
        return getSuperMethod(clazz, null, name, params);
    }

    /**
     * used for invokeSuper
     */
    public static Method getSuperMethod(Class clazz, Class returnType, String name, Class... params) {
        Method ret = null;
        Method[] ms;
        clazz = clazz.getSuperclass();
        do {
            ms = clazz.getDeclaredMethods();
            a:
            for (Method m : ms) {
                if (!m.getName().equals(name)) continue;
                if (Modifier.isPrivate(m.getModifiers())) continue;
                //Private not overridden
                if (returnType != null && !returnType.equals(m.getReturnType())) continue;
                Class[] mp = m.getParameterTypes();
                if (mp.length != params.length) continue;
                for (int i = 0; i < mp.length; i++) {
                    if (!mp[i].equals(params[i])) continue a;
                }
                ret = m;
                return ret;
            }
        } while ((clazz = clazz.getSuperclass()) != null && !Object.class.equals(clazz));
        return ret;
    }

    public static boolean isTim(Context ctx) {
        return ctx.getPackageName().equals(PACKAGE_NAME_TIM);
    }

    public static ContactDescriptor parseResultRec(Object a) {
        ContactDescriptor cd = new ContactDescriptor();
        cd.uin = (String) iget_object_or_null(a, "a", String.class);
        cd.nick = (String) iget_object_or_null(a, "b", String.class);
        cd.uinType = (int) iget_object_or_null(a, "b", int.class);
        return cd;
    }

    public static class ContactDescriptor {
        public String uin;
        public int uinType;
        @Nullable
        public String nick;
    }


    /**
     * 根据手机的分辨率从 dip 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int dip2sp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density /
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }


    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
