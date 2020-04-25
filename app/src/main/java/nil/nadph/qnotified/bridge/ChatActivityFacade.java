/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.bridge;

import android.content.Context;
import android.os.Parcelable;
import com.tencent.mobileqq.app.QQAppInterface;
import nil.nadph.qnotified.util.DexKit;

import java.io.Externalizable;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Utils.log;

public class ChatActivityFacade {
    public static long[] sendMessage(QQAppInterface qqAppInterface, Context context, Parcelable sessionInfo, String msg,
                                     ArrayList<?> atInfo, Object sendMsgParams) {
        if (qqAppInterface == null) throw new NullPointerException("qqAppInterface == null");
        if (sessionInfo == null) throw new NullPointerException("sessionInfo == null");
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) continue;
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) continue;
            if (argt[1].equals(Context.class) && argt[2].equals(_SessionInfo())
                    && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                m = mi;
                m.setAccessible(true);
                SendMsgParams = argt[5];
                break;
            }
        }
        try {
            return (long[]) m.invoke(null, qqAppInterface, context, sessionInfo, msg, atInfo, sendMsgParams);
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long[] sendMessage(QQAppInterface qqAppInterface, Context context, Parcelable sessionInfo, String msg) {
        if (qqAppInterface == null) throw new NullPointerException("qqAppInterface == null");
        if (sessionInfo == null) throw new NullPointerException("sessionInfo == null");
        if (msg == null) throw new NullPointerException("msg == null");
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) continue;
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) continue;
            if (argt[1].equals(Context.class) && argt[2].equals(_SessionInfo())
                    && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                m = mi;
                m.setAccessible(true);
                SendMsgParams = argt[5];
                break;
            }
        }
        try {
            return (long[]) m.invoke(null, qqAppInterface, context, sessionInfo, msg, new ArrayList<>(), SendMsgParams.newInstance());
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long sendPttMessage(QQAppInterface qqAppInterface, Parcelable sessionInfo, String pttPath) {
        if (qqAppInterface == null) throw new NullPointerException("qqAppInterface == null");
        if (sessionInfo == null) throw new NullPointerException("sessionInfo == null");
        if (pttPath == null) throw new NullPointerException("pttPath == null");
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(long.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) continue;
                if (clz[0].equals(QQAppInterface.class) && clz[1].equals(_SessionInfo()) && clz[2].equals(String.class)) {
                    send = m;
                    break;
                }
            }
        }
        try {
            return (long) send.invoke(null, qqAppInterface, sessionInfo, pttPath);
        } catch (Exception e) {
            log(e);
            return 0;
        }
    }

    public static boolean sendArkAppMessage(QQAppInterface qqAppInterface, Parcelable sessionInfo, Object arkAppMsg) {
        if (qqAppInterface == null) throw new NullPointerException("qqAppInterface == null");
        if (sessionInfo == null) throw new NullPointerException("sessionInfo == null");
        if (arkAppMsg == null) throw new NullPointerException("arkAppMsg == null");
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(boolean.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) continue;
                if (clz[0].equals(QQAppInterface.class) && clz[1].equals(_SessionInfo()) && clz[2].isInstance(arkAppMsg)) {
                    send = m;
                    break;
                }
            }
        }
        try {
            return (boolean) send.invoke(null, qqAppInterface, sessionInfo, arkAppMsg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    public static void sendAbsStructMsg(QQAppInterface qqAppInterface, Parcelable sessionInfo, Externalizable absStructMsg) {
        if (qqAppInterface == null) throw new NullPointerException("qqAppInterface == null");
        if (sessionInfo == null) throw new NullPointerException("sessionInfo == null");
        if (absStructMsg == null) throw new NullPointerException("absStructMsg == null");
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(void.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) continue;
                if (clz[0].equals(QQAppInterface.class) && clz[1].equals(_SessionInfo()) && clz[2].isInstance(absStructMsg)) {
                    send = m;
                    break;
                }
            }
        }
        try {
            send.invoke(null, qqAppInterface, sessionInfo, absStructMsg);
        } catch (Exception e) {
            log(e);
        }
    }
}
