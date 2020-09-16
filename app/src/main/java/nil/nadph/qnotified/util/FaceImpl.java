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
package nil.nadph.qnotified.util;

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

import nil.nadph.qnotified.ui.ResUtils;

import static nil.nadph.qnotified.util.Initiator.load;

@SuppressWarnings("rawtypes")
public class FaceImpl implements InvocationHandler {

    public static final int TYPE_USER = 1;
    public static final int TYPE_TROOP = 4;
    static private WeakReference<FaceImpl> self;
    private static Class class_FaceDecoder;
    private static Class clz_DecodeTaskCompletionListener;
    private final HashMap<String, Bitmap> cachedUserFace;
    private final HashMap<String, Bitmap> cachedTroopFace;
    private final HashMap<String, WeakReference<ImageView>> registeredView;
    private final Object mFaceDecoder;

    private FaceImpl() throws Throwable {
        //private Object faceMgr;
        Object qqAppInterface = Utils.getAppRuntime();
        class_FaceDecoder = load("com/tencent/mobileqq/util/FaceDecoder");
        if (class_FaceDecoder == null) {
            class_FaceDecoder = load("com/tencent/mobileqq/app/face/FaceDecoder");
        }
        if (class_FaceDecoder == null) {
            Class cl_rxMsg = load("com/tencent/mobileqq/receipt/ReceiptMessageReadMemberListFragment");
            Field[] fs = cl_rxMsg.getDeclaredFields();
            for (Field f : fs) {
                if (f.getType().equals(View.class)) continue;
                if (f.getType().equals(load("com/tencent/mobileqq/app/QQAppInterface"))) continue;
                class_FaceDecoder = f.getType();
            }
        }
        mFaceDecoder = class_FaceDecoder.getConstructor(load("com/tencent/common/app/AppInterface")).newInstance(qqAppInterface);
        Utils.invoke_virtual_any(mFaceDecoder, createListener(), clz_DecodeTaskCompletionListener);
        cachedUserFace = new HashMap<>();
        cachedTroopFace = new HashMap<>();
        registeredView = new HashMap<>();
    }

    public static FaceImpl getInstance() throws Throwable {
        FaceImpl ret = null;
        if (self != null) ret = self.get();
        if (ret == null) {
            ret = new FaceImpl();
            self = new WeakReference(ret);
        }
        return ret;
    }

    private Object createListener() {
        clz_DecodeTaskCompletionListener = load("com/tencent/mobileqq/util/FaceDecoder$DecodeTaskCompletionListener");
        if (clz_DecodeTaskCompletionListener == null) {
            clz_DecodeTaskCompletionListener = load("com/tencent/mobileqq/app/face/FaceDecoder$DecodeTaskCompletionListener");
        }
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
        Class[] argt = method.getParameterTypes();
        if (argt.length != 4) return null;
        if (argt[0].equals(int.class) && argt[1].equals(int.class) && argt[2].equals(String.class) && argt[3].equals(Bitmap.class)) {
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

    public @Nullable
    Bitmap getBitmapFromCache(int type, String uin) {
        if (type == TYPE_TROOP) return cachedTroopFace.get(uin);
        if (type == TYPE_USER) return cachedUserFace.get(uin);
        return null;
    }

    public boolean requestDecodeFace(int type, String uin) {
        try {
            return (boolean) Utils.invoke_virtual_any(mFaceDecoder, uin, type, true, (byte) 0, String.class, int.class, boolean.class, byte.class, boolean.class);
        } catch (Exception e) {
            Utils.log(e);
            return false;
        }
    }

    public boolean registerView(int type, String uin, ImageView v) {
        boolean ret;
        if (ret = requestDecodeFace(type, uin))
            registeredView.put(type + " " + uin, new WeakReference<>(v));
        return ret;
    }

    public boolean setImageOrRegister(Utils.ContactDescriptor cd, ImageView imgview) {
        return setImageOrRegister(cd.uinType == 1 ? TYPE_TROOP : TYPE_USER, cd.uin, imgview);
    }

    public boolean setImageOrRegister(int type, String uin, ImageView imgview) {
        Bitmap bm = getBitmapFromCache(type, uin);
        if (bm == null) {
            imgview.setImageDrawable(ResUtils.loadDrawableFromAsset("face.png", imgview.getContext()));
            return registerView(type, uin, imgview);
        } else {
            imgview.setImageBitmap(bm);
            return true;
        }
    }
}
