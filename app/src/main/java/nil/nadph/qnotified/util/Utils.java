package nil.nadph.qnotified.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
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
import dalvik.system.DexFile;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;

@SuppressWarnings({"unchecked", "rawtypes"})
@SuppressLint("SimpleDateFormat")
public class Utils {

    public static final String QN_VERSION_NAME = "0.7.1-beta";
    public static final int QN_VERSION_CODE = 29;
    public static final boolean __REMOVE_PREVIOUS_CACHE = true;
    public static final String PACKAGE_NAME_QQ = "com.tencent.mobileqq";
    public static final String PACKAGE_NAME_QQ_INTERNATIONAL = "com.tencent.mobileqqi";
    public static final String PACKAGE_NAME_QQ_LITE = "com.tencent.qqlite";
    public static final String PACKAGE_NAME_TIM = "com.tencent.tim";
    public static final String PACKAGE_NAME_SELF = "nil.nadph.qnotified";
    public static final String PACKAGE_NAME_XPOSED_INSTALLER = "de.robv.android.xposed.installer";
    public static final int TOAST_TYPE_INFO = 0;
    public static final int TOAST_TYPE_ERROR = 1;
    public static final int TOAST_TYPE_SUCCESS = 2;
    public static boolean DEBUG = true;
    public static boolean ENABLE_DUMP_LOG = false;
    private static Handler mHandler;

    private Utils() {
        throw new AssertionError("No instance for you!");
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Nullable
    public static String getActiveModuleVersion() {
        Math.sqrt(1);
        Math.random();
        Math.expm1(0.001);
        //Just make the function longer,so that it will get hooked by Epic
        return null;
    }

    public static void runOnUiThread(Runnable r) {
        if (mHandler == null) mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(r);
    }

    public static Application getApplication() {
        Field f;
        try {
            Class clz = load("com/tencent/common/app/BaseApplicationImpl");
            f = hasField(clz, "sApplication");
            if (f == null) return (Application) sget_object(clz, "a", clz);
            else return (Application) f.get(null);
        } catch (Exception e) {
            log(e);
            //noinspection UnnecessaryInitCause
            throw (AssertionError) new AssertionError("FATAL: Utils.getApplication() failure!").initCause(e);
        }
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

    public static Object getTroopManager() throws Exception {
        Object mTroopManager = invoke_virtual(getQQAppInterface(), "getManager", 51, int.class);
        if (!mTroopManager.getClass().getName().contains("TroopManager"))
            mTroopManager = invoke_virtual(getQQAppInterface(), "getManager", 52, int.class);
        return mTroopManager;
    }

    public static PackageInfo getHostInfo(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (Throwable e) {
            Log.e("Utils", "Can not get PackageInfo!");
            throw new AssertionError("Can not get PackageInfo!");
        }
    }

	/*public static Object invoke_virtual(Object obj,String method,Object...args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException{
	 Class clazz=obj.getClass();
	 Method m=findMethodByArgs(clazz,method,args);
	 m.setAccessible(true);
	 return m.invoke(obj,args);
	 }*/

    public static int getHostVersionCode() {
        PackageInfo pi = getHostInfo(getApplication());
        return pi.versionCode;
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
            log(e);
        }
    }

    public static View.OnClickListener getOnClickListener(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return getOnClickListenerV14(v);
        } else {
            return getOnClickListenerV(v);
        }
    }

    public static Object invoke_virtual(Object obj, String name, Object... argsTypesAndReturnType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length)
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        int i, ii;
        Method[] m;
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
        Method[] m;
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
        Method[] m;
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
        Object ret;
        boolean needPatch = false;
        try {
            ret = XposedBridge.invokeOriginalMethod(method, obj, argv);
            return ret;
        } catch (IllegalStateException e) {
            //For SandHook-EdXp: Method not hooked.
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
        //here needPatch is always true
        ret = method.invoke(obj, argv);
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
        Method[] m;
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

    public static Object new_instance(Class clazz, Object... argsAndTypes) throws InvocationTargetException, InstantiationException, NoSuchMethodException {
        int argc = argsAndTypes.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        int i;
        Constructor m;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsAndTypes[argc + i];
            argv[i] = argsAndTypes[i];
        }
        m = clazz.getDeclaredConstructor(argt);
        m.setAccessible(true);
        try {
            return m.newInstance(argv);
        } catch (IllegalAccessException e) {
            log(e);
            //should NOT happen
            throw new RuntimeException(e);
        }
    }

    public static Object getQQAppInterface() {
        return getAppRuntime();
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

    public static Object getMobileQQService() {
        return iget_object_or_null(getQQAppInterface(), "a", load("com/tencent/mobileqq/service/MobileQQService"));
    }

    public static Object createSessionInfo(String uin, int uinType) {
        Class clz = _SessionInfo();
        if (clz == null) throw new NoClassDefFoundError("SessionInfo");
        try {
            Object obj = new_instance(clz);
            iput_object(obj, "a", String.class, uin);
            iput_object(obj, "a", int.class, uinType);
            return obj;
        } catch (Exception e) {
            log(e);
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

    //Used for APIs lower than ICS (API 14)
    private static View.OnClickListener getOnClickListenerV(View view) {
        View.OnClickListener retrievedListener = null;
        String viewStr = "android.view.View";
        Field field;
        try {
            //noinspection JavaReflectionMemberAccess
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
    @SuppressWarnings("JavaReflectionMemberAccess")
    @SuppressLint("PrivateApi")
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

    public static String getCurrentNickname() {
        try {
            return (String) invoke_virtual(getQQAppInterface(), "getCurrentNickname");
        } catch (Throwable e) {
            log(e);
        }
        return null;
    }

    public static Object getAppRuntime() {
        Object baseApplicationImpl = getApplication();
        try {
            Method m;
            m = hasMethod(baseApplicationImpl, "getRuntime");
            if (m == null) return invoke_virtual(baseApplicationImpl, "a", load("mqq/app/AppRuntime"));
            else return m.invoke(baseApplicationImpl);
        } catch (Exception e) {
            throw new AssertionError(e);
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
            //log("bh(" + type + ")=" + ret);
            return invoke_virtual(getQQAppInterface(), "a", type, int.class, cl_bh);
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
        if (clazz != null && name.length() > 0) {
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

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static void log(String str) {
        Log.i("QNdump", str);
        if (DEBUG) try {
            XposedBridge.log(str);
        } catch (NoClassDefFoundError e) {
            Log.i("Xposed", str);
            Log.i("EdXposed-Bridge", str);
        }
        if (ENABLE_DUMP_LOG) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/qn_log.txt";
            File f = new File(path);
            try {
                if (!f.exists()) f.createNewFile();
                appendToFile(path, "[" + System.currentTimeMillis() + "-" + android.os.Process.myPid() + "] " + str + "\n");
            } catch (IOException e) {
            }
        }
    }

    public static void log(Throwable th) {
        if (th == null) return;
        BugCollector.onThrowable(th);
        log(Log.getStackTraceString(th));
    }

    public static void checkLogFlag() {
        try {
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.qn_log_flag");
            if (f.exists()) ENABLE_DUMP_LOG = true;
        } catch (Exception ignored) {
        }
    }

    /**
     * 追加文件：使用FileWriter
     * 不能{@link #log(Throwable)},防止死递归
     *
     * @param fileName
     * @param content
     */
    public static void appendToFile(String fileName, String content) {
        FileWriter writer = null;
        try {
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

    public static String csvenc(String s) {
        if (!s.contains("\"") && !s.contains(" ") && !s.contains(",") && !s.contains("\r") && !s.contains("\n") && !s.contains("\t")) {
            return s;
        }
        return "\"" + s.replace("\"", "\"\"") + "\"";
    }

    private static Method method_Toast_show;
    private static Method method_Toast_makeText;
    private static Class clazz_QQToast;

    public static Toast showToast(Context ctx, int type, CharSequence str, int length) {
        try {
            if (clazz_QQToast == null) {
                clazz_QQToast = load("com/tencent/mobileqq/widget/QQToast");
            }
            if (clazz_QQToast == null) {
                Class clz = load("com/tencent/mobileqq/activity/aio/doodle/DoodleLayout");
                assert clz != null;
                Field[] fs = clz.getDeclaredFields();
                for (Field f : fs) {
                    if (View.class.isAssignableFrom(f.getType())) continue;
                    if (f.getType().isPrimitive()) continue;
                    if (f.getType().isInterface()) continue;
                    clazz_QQToast = f.getType();
                }
            }
            if (method_Toast_show == null) {
                Method[] ms = clazz_QQToast.getMethods();
                for (Method m : ms) {
                    if (Toast.class.equals(m.getReturnType()) && m.getParameterTypes().length == 0) {
                        method_Toast_show = m;
                        break;
                    }
                }
            }
            if (method_Toast_makeText == null) {
                method_Toast_makeText = clazz_QQToast.getMethod("a", Context.class, int.class, CharSequence.class, int.class);
            }
            Object qqToast = method_Toast_makeText.invoke(null, ctx, type, str, length);
            return (Toast) method_Toast_show.invoke(qqToast);
        } catch (Exception e) {
            log(e);
            Toast t = Toast.makeText(ctx, str, length);
            t.show();
            return t;
        }
    }

    public static Toast showToastShort(Context ctx, CharSequence str) {
        return showToast(ctx, 0, str, 0);
    }

    @Deprecated
    public static void showErrorToastAnywhere(final String text) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            Utils.showToast(getApplication(), TOAST_TYPE_ERROR, text, Toast.LENGTH_SHORT);
        } else {
            SyncUtils.post(new Runnable() {
                @Override
                public void run() {
                    Utils.showToast(getApplication(), TOAST_TYPE_ERROR, text, Toast.LENGTH_SHORT);
                }
            });
        }
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

    @SuppressWarnings("deprecation")
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

    public static String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("\u3000");
                return source;
            }
            return source;
        }
        return null;
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

    public static boolean isNiceUser() {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            if (cfg.getBooleanOrDefault(ConfigItems.cfg_nice_user, false)) {
                return true;
            }
            if (doEvalNiceUser()) {
                try {
                    if (SyncUtils.isMainProcess()) {
                        cfg.getAllConfig().put(ConfigItems.cfg_nice_user, true);
                        cfg.save();
                    }
                } catch (Throwable e1) {
                    log(e1);
                }
                return true;
            }
            return false;
        } catch (Throwable e2) {
            log(e2);
            return true;
        }
    }

    private static boolean doEvalNiceUser() {
        if (!isExp()) return true;
        long uin = getLongAccountUin();
        String nick = getCurrentNickname();
        if (nick != null && nick.length() > 0 && isBadNick(nick)) return false;
        if (Utils.isTim(getApplication())) return true;
        if (uin > 2_0000_0000L && uin < 30_0000_0000L) {
            return true;
        }
        Class vip = DexKit.tryLoadOrNull(DexKit.C_VIP_UTILS);
        try {
            if (vip != null) {
                return "0".equals(invoke_static(vip, "a", getQQAppInterface(), uin + "", load("com/tencent/common/app/AppInterface"), String.class, String.class));
            }
        } catch (Exception e) {
            log(e);
        }
        return true;
    }

    private static boolean isSymbol(char c) {
        if (c == '\u3000') return true;
        if (c < '0') return true;
        if (c > '9' && c < 'A') return true;
        if (c > 'Z' && c < 'a') return true;
        return (c <= 0xD7FF);
    }

    /**
     * 特征
     * A/a+sp/'/^   && lenth>2
     * 丶ゞ
     * 中文.len()<5 && endsWith '.'
     * char[1]  'emoji'
     * char[1] 全半角单符号
     * IDSP/3000"　"
     */
    private static boolean isBadNick(String nick) {
        if (nick == null) throw new NullPointerException("nick == null");
        if (nick.length() == 0) throw new IllegalArgumentException("nick length == 0");
        nick = filterEmoji(nick);
        if (nick.contains("\u4e36") || nick.contains("\u309e") || nick.contains("双封") || nick.contains("群发")
                || nick.contains("代发") || nick.contains("赚") || nick.contains("换群") || nick.contains("加我")
                || nick.contains("加盟") || nick.contains("中介") || nick.contains("兼职") || nick.contains("客服")
                || nick.contains("招聘")
                || nick.matches(".*[\u53f8\u6b7b][\u9a6c\u5417\u5988\u3000].*"))
            return true;
        if (nick.equalsIgnoreCase("A")) return true;
        if (nick.length() < 2) {
            return isSymbol(nick.charAt(0));
        }
        if (nick.matches(".*[Aa]['`. ,\u2018\u2019\u201a\u201b^_\u309e].*")) {
            return true;
        }
        if (nick.endsWith(".")) {
            char c = nick.charAt(nick.length() - 2);
            return c > 0xff;
        }
        return false;
    }

    /**
     * 仅仅为群发器而使用本模块的用户往往有两个鲜明的特征
     * 1.使用某个虚拟框架
     * 2.显而易见的昵称,见{@link #isBadNick(String)}
     * 仍然提供本模块的全部功能
     * 只是隐藏我的联系方式
     * 未必是完全正确的方法, but just do it.
     **/
    private static boolean isExp() {
        try {
            Object pathList = iget_object_or_null(XposedBridge.class.getClassLoader(), "pathList");
            Object[] dexElements = (Object[]) iget_object_or_null(pathList, "dexElements");
            for (Object entry : dexElements) {
                DexFile dexFile = (DexFile) iget_object_or_null(entry, "dexFile");
                Enumeration<String> entries = dexFile.entries();
                while (entries.hasMoreElements()) {
                    String className = entries.nextElement();
                    if (className.matches(".+?(epic|weishu).+")) {
                        return true;
                    }
                }
            }
        } catch (Throwable e) {
            if (!(e instanceof NullPointerException) &&
                    !(e instanceof NoClassDefFoundError)) {
                log(e);
            }
        }
        return false;
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

    public static int sign(double d) {
        return Double.compare(d, 0d);
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

    public static boolean isAlphaVersion() {
        return QN_VERSION_NAME.contains("-") || QN_VERSION_NAME.contains("es") || QN_VERSION_NAME.contains("a");
    }

    //FIXME: this may not work properly after obfuscation
    public static boolean isRecursion() {
        StackTraceElement[] stacks = new Exception().getStackTrace();
        int count = 0;
        String cname = stacks[1].getClassName();
        String mname = stacks[1].getMethodName();
        for (int i = 2; i < stacks.length; i++) {
            if (stacks[i].getClassName().equals(cname) && stacks[i].getMethodName().equals(mname)) {
                return true;
            }
        }
        return false;
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

    public static class DummyCallback implements DialogInterface.OnClickListener {
        public DummyCallback() {
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
        }

    }

    public static String en_toStr(Object obj) {
        if (obj == null) return null;
        String str;
        if (obj instanceof CharSequence) str = Utils.en(obj.toString());
        else str = "" + obj;
        return str;
    }

    public static String nomorethan100(Object obj) {
        if (obj == null) return null;
        String str;
        if (obj instanceof CharSequence) str = "\"" + obj + "\"";
        else str = "" + obj;
        if (str.length() > 110) return str.substring(0, 100);
        return str;
    }

    public static Activity getCurrentActivity() {
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    return (Activity) activityField.get(activityRecord);
                }
            }
        } catch (Exception e) {
            log(e);
        }
        return null;
    }

    public static class ContactDescriptor {
        public String uin;
        public int uinType;
        @Nullable
        public String nick;

        public String getId() {
            StringBuilder msg = new StringBuilder();
            if (uin.length() < 10) {
                for (int i = 0; i < 10 - uin.length(); i++) {
                    msg.append("0");
                }
            }
            return msg + uin + uinType;
        }

    }

    public static void onStubClassInitialize() {
        Throwable th = new Throwable("WTF: stub class was initialized!!!");
        log(th);
    }

    @Nullable
    public static Object defaultShadowClone(Object orig) {
        if (orig == null) return null;
        Class cl = orig.getClass();
        Object clone;
        try {
            clone = cl.newInstance();
            while (cl != null && !cl.equals(Object.class)) {
                for (Field f : cl.getDeclaredFields()) {
                    f.setAccessible(true);
                    f.set(clone, f.get(orig));
                }
                cl = cl.getSuperclass();
            }
            return clone;
        } catch (Exception e) {
            log(e);
            return null;
        }
    }
}