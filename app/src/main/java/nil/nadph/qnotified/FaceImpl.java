package nil.nadph.qnotified;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import static nil.nadph.qnotified.Initiator.load;
import android.annotation.*;

//import de.robv.android.xposed.*;


public class FaceImpl implements InvocationHandler {

    public static final int TYPE_USER = 1;
    public static final int TYPE_TROOP = 4;

    private HashMap<String, Bitmap> cachedUserFace;
    private HashMap<String, Bitmap> cachedTroopFace;
    private HashMap<String, WeakReference<ImageView>> registeredView;
    private Object faceMgr;
    private Object qqAppInterface;
    private Object mFaceDecoder;
    static private FaceImpl self;
    private static Class class_FaceDecoder;

    private FaceImpl() throws Throwable {
        qqAppInterface = Utils.getAppRuntime();
        class_FaceDecoder = load("com/tencent/mobileqq/util/FaceDecoder");
        if (class_FaceDecoder == null) {
            Class cl_rxMsg = load("com/tencent/mobileqq/receipt/ReceiptMessageReadMemberListFragment");
            Field fs[] = cl_rxMsg.getDeclaredFields();
            for (Field f : fs) {
                if (f.getType().equals(View.class)) continue;
                if (f.getType().equals(load("com/tencent/mobileqq/app/QQAppInterface"))) continue;
                class_FaceDecoder = f.getType();
            }
        }
        mFaceDecoder = class_FaceDecoder.getConstructor(load("com/tencent/common/app/AppInterface")).newInstance(qqAppInterface);
        Utils.invoke_virtual(mFaceDecoder, "a", createListener(), clz_DecodeTaskCompletionListener);
        cachedUserFace = new HashMap<>();
        cachedTroopFace = new HashMap<>();
        registeredView = new HashMap<>();
    }

    public static FaceImpl getInstance() throws Throwable {
        if (self == null) self = new FaceImpl();
        return self;
    }

    private static Class clz_DecodeTaskCompletionListener;

    private Object createListener() {
        clz_DecodeTaskCompletionListener = load("com/tencent/mobileqq/util/FaceDecoder$DecodeTaskCompletionListener");
        if (clz_DecodeTaskCompletionListener == null) {
            Class[] argt;
            Method[] ms = class_FaceDecoder.getDeclaredMethods();
            for (Method m : ms) {
                if (!m.getReturnType().equals(void.class)) continue;
                argt = m.getParameterTypes();
                if (argt.length != 1) continue;
                if (argt[0].equals(load("com/tencent/common/app/AppInterface"))) continue;
                clz_DecodeTaskCompletionListener = argt[0];
            }
        }
        return Proxy.newProxyInstance(clz_DecodeTaskCompletionListener.getClassLoader(), new Class[]{clz_DecodeTaskCompletionListener}, this);
    }

    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("onDecodeTaskCompleted")) {
            onDecodeTaskCompleted((int) args[0], (int) args[1], (String) args[2], (Bitmap) args[3]);
        }
        return null;
    }

    public void onDecodeTaskCompleted(int code, int type, String uin, final Bitmap bitmap) {
        //Utils.log(code+","+type+","+uin+","+bitmap);
        if (bitmap != null) {
            if (type == TYPE_USER) cachedUserFace.put(uin, bitmap);
            if (type == TYPE_TROOP) cachedTroopFace.put(uin, bitmap);
            WeakReference<ImageView> ref;
            if ((ref = registeredView.remove(type + " " + uin)) != null) {
                final ImageView v = ref.get();
                if (v != null) ((Activity) Utils.getContext(v)).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        v.setImageBitmap(bitmap);
                    }
                });
            }
        }
    }

    public @Nullable Bitmap getBitmapFromCache(int type, String uin) {
        if (type == TYPE_TROOP) return cachedTroopFace.get(uin);
        if (type == TYPE_USER) return cachedUserFace.get(uin);
        return null;
    }

    public boolean requestDecodeFace(int type, String uin) {
        try {
            return (boolean) Utils.invoke_virtual(mFaceDecoder, "a", uin, type, true, (byte) 0, String.class, int.class, boolean.class, byte.class, boolean.class);
        } catch (Exception e) {
            Utils.log(e);
            return false;
        }
    }

    public boolean registerView(int type, String uin, ImageView v) {
        boolean ret;
        if (ret = requestDecodeFace(type, uin)) registeredView.put(type + " " + uin, new WeakReference<>(v));
        return ret;
    }


}
