/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.bridge;

import android.content.Context;
import android.os.Parcelable;

import java.io.Externalizable;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

import me.singleneuron.qn_kernel.data.HostInfo;
import mqq.app.AppRuntime;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.ReflexUtil;
import nil.nadph.qnotified.util.Toasts;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.Utils.*;

public class ChatActivityFacade {

    public static long[] sendMessage(AppRuntime qqAppInterface, Context context,
                                     Parcelable sessionInfo, String msg,
                                     ArrayList<?> atInfo, Object sendMsgParams) {
        if (qqAppInterface == null) {
            throw new NullPointerException("qqAppInterface == null");
        }
        if (sessionInfo == null) {
            throw new NullPointerException("sessionInfo == null");
        }
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) {
                continue;
            }
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) {
                continue;
            }
            if (argt[1].equals(Context.class)
                && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                if (argt[2].equals(_SessionInfo()) || argt[2].equals(load("com/tencent/mobileqq/activity/aio/BaseSessionInfo"))) {
                    m = mi;
                    m.setAccessible(true);
                    SendMsgParams = argt[5];
                    break;
                }
            }
        }
        try {
            if (sendMsgParams == null) {
                sendMsgParams = SendMsgParams.newInstance();
            }
            return (long[]) m
                .invoke(null, qqAppInterface, context, sessionInfo, msg, atInfo, sendMsgParams);
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long[] sendMessage(AppRuntime qqAppInterface, Context context,
        Parcelable sessionInfo, String msg) {
        if (qqAppInterface == null) {
            throw new NullPointerException("qqAppInterface == null");
        }
        if (sessionInfo == null) {
            throw new NullPointerException("sessionInfo == null");
        }
        if (msg == null) {
            throw new NullPointerException("msg == null");
        }
        Class facade = DexKit.doFindClass(DexKit.C_FACADE);
        Class SendMsgParams = null;
        Method m = null;
        for (Method mi : facade.getDeclaredMethods()) {
            if (!mi.getReturnType().equals(long[].class)) {
                continue;
            }
            Class[] argt = mi.getParameterTypes();
            if (argt.length != 6) {
                continue;
            }
            if (argt[1].equals(Context.class)
                && argt[3].equals(String.class) && argt[4].equals(ArrayList.class)) {
                if (argt[2].equals(_SessionInfo()) || argt[2].equals(load("com/tencent/mobileqq/activity/aio/BaseSessionInfo"))) {
                    m = mi;
                    m.setAccessible(true);
                    SendMsgParams = argt[5];
                    break;
                }
            }
        }
        try {
            return (long[]) m
                .invoke(null, qqAppInterface, context, sessionInfo, msg, new ArrayList<>(),
                    SendMsgParams.newInstance());
        } catch (Exception e) {
            log(e);
            return null;
        }
    }

    public static long sendPttMessage(AppRuntime qqAppInterface, Parcelable sessionInfo,
        String pttPath) {
        if (qqAppInterface == null) {
            throw new NullPointerException("qqAppInterface == null");
        }
        if (sessionInfo == null) {
            throw new NullPointerException("sessionInfo == null");
        }
        if (pttPath == null) {
            throw new NullPointerException("pttPath == null");
        }
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(long.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) {
                    continue;
                }
                if (clz[0].equals(_QQAppInterface()) && clz[1].equals(_SessionInfo()) && clz[2]
                    .equals(String.class)) {
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

    public static boolean sendArkAppMessage(AppRuntime qqAppInterface, Parcelable sessionInfo,
        Object arkAppMsg) {
        if (qqAppInterface == null) {
            throw new NullPointerException("qqAppInterface == null");
        }
        if (sessionInfo == null) {
            throw new NullPointerException("sessionInfo == null");
        }
        if (arkAppMsg == null) {
            throw new NullPointerException("arkAppMsg == null");
        }
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(boolean.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) {
                    continue;
                }
                if (clz[0].equals(_QQAppInterface()) && clz[1].equals(_SessionInfo()) && clz[2]
                    .isInstance(arkAppMsg)) {
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

    public static void sendAbsStructMsg(AppRuntime qqAppInterface, Parcelable sessionInfo,
        Externalizable absStructMsg) {
        if (qqAppInterface == null) {
            throw new NullPointerException("qqAppInterface == null");
        }
        if (sessionInfo == null) {
            throw new NullPointerException("sessionInfo == null");
        }
        if (absStructMsg == null) {
            throw new NullPointerException("absStructMsg == null");
        }
        Method send = null;
        for (Method m : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
            if (m.getReturnType().equals(void.class)) {
                Class<?>[] clz = m.getParameterTypes();
                if (clz.length != 3) {
                    continue;
                }
                if (clz[0].equals(_QQAppInterface()) && clz[1].equals(_SessionInfo()) && clz[2]
                    .isInstance(absStructMsg)) {
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

    public static void repeatMessage(AppRuntime app, Parcelable session, Object msg) {
        if (app == null) {
            throw new NullPointerException("app == null");
        }
        if (session == null) {
            throw new NullPointerException("session == null");
        }
        if (msg == null) {
            throw new NullPointerException("msg == null");
        }
        String msgText;
        Class[] argt = null;
        Method m = null;
        switch (getShort$Name(msg)) {
            case "MessageForText":
            case "MessageForFoldMsg":
            case "MessageForLongTextMsg":
                msgText = (String) iget_object_or_null(msg, "msg");
                if (msgText.length() > 3000) {
                    Toasts.error(HostInfo.getHostInfo().getApplication(),
                        "暂不支持发送长消息");
                    return;
                }
                ArrayList<?> atInfo = null;
                try {
                    String extStr = (String) ReflexUtil.invoke_virtual(msg, "getExtInfoFromExtStr", "troop_at_info_list", String.class);
                    atInfo = (ArrayList) ReflexUtil.invoke_virtual(msg, "getTroopMemberInfoFromExtrJson", extStr, String.class);
                } catch (Exception e) {
                   // ignore
                }
                if (atInfo == null) {
                    sendMessage(app, HostInfo.getHostInfo().getApplication(), session,
                        msgText);
                } else {
                    sendMessage(app, HostInfo.getHostInfo().getApplication(), session,
                        msgText, atInfo, null);
                }
                break;
            case "MessageForPic":
                try {
                    for (Method mi : DexKit.doFindClass(DexKit.C_FACADE).getMethods()) {
                        if (!mi.getName().equals("a") && !mi.getName().equals("b")) {
                            continue;
                        }
                        argt = mi.getParameterTypes();
                        if (argt.length < 3) {
                            continue;
                        }
                        if (argt[0].equals(Initiator._QQAppInterface()) && argt[1]
                            .equals(_SessionInfo())
                            && argt[2].isAssignableFrom(msg.getClass()) && mi.getReturnType()
                            .equals(void.class)) {
                            m = mi;
                            break;
                        }
                    }
                    if (argt.length == 3) {
                        m.invoke(null, app, session, msg);
                    } else {
                        m.invoke(null, app, session, msg, 0);
                    }
                } catch (Exception e) {
                    Toasts.error(HostInfo.getHostInfo().getApplication(),
                        e.toString().replace("java.lang.", ""));
                    log(e);
                }
                break;
            case "MessageForPtt":
                try {
                    String url = (String) invoke_virtual(msg, "getLocalFilePath");
                    File file = new File(url);
                    if (!file.exists()) {
                        Toasts.error(HostInfo.getHostInfo().getApplication(),
                            "未找到语音文件");
                        return;
                    }
                    sendPttMessage(getQQAppInterface(), session, url);
                } catch (Exception e) {
                    Toasts.error(HostInfo.getHostInfo().getApplication(),
                        e.toString().replace("java.lang.", ""));
                    log(e);
                }
                break;
            default:
                Toasts.error(HostInfo.getHostInfo().getApplication(),
                    "Unsupported msg type: " + getShort$Name(msg));
        }
    }
}
