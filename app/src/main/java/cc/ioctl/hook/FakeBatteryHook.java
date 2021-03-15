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
package cc.ioctl.hook;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;
import static nil.nadph.qnotified.util.Utils.logi;
import static nil.nadph.qnotified.util.Utils.logw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Parcelable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Initiator;

@FunctionEntry
public class FakeBatteryHook extends CommonDelayableHook implements InvocationHandler,
    SyncUtils.BroadcastListener {

    public static final String qn_fake_bat_enable = "qn_fake_bat_enable";
    public static final FakeBatteryHook INSTANCE = new FakeBatteryHook();
    private static final String ACTION_UPDATE_BATTERY_STATUS = "nil.nadph.qnotified.ACTION_UPDATE_BATTERY_STATUS";
    private static final String _FLAG_MANUAL_CALL = "flag_manual_call";
    private static final Collection<Long> sLockedThreadsId = Collections
        .synchronizedCollection(new HashSet<Long>());
    private WeakReference<BroadcastReceiver> mBatteryLevelRecvRef = null;
    private WeakReference<BroadcastReceiver> mBatteryStatusRecvRef = null;
    private Object origRegistrar = null;
    private Object origStatus = null;
    private int lastFakeLevel = -1;
    private int lastFakeStatus = -1;

    FakeBatteryHook() {
        super(qn_fake_bat_enable, SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF);
    }

    private static void doPostReceiveEvent(final BroadcastReceiver recv, final Context ctx,
        final Intent intent) {
        SyncUtils.post(new Runnable() {
            @Override
            public void run() {
                SyncUtils.setTlsFlag(_FLAG_MANUAL_CALL);
                try {
                    recv.onReceive(ctx, intent);
                } catch (Throwable e) {
                    log(e);
                }
                SyncUtils.clearTlsFlag(_FLAG_MANUAL_CALL);
            }
        });
    }

    private static void BatteryProperty_setLong(Parcelable prop, long val) {
        if (prop == null) {
            return;
        }
        try {
            Field field;
            field = prop.getClass().getDeclaredField("mValueLong");
            field.setAccessible(true);
            field.set(prop, val);
        } catch (Throwable e) {
            log(e);
        }
    }

    @Override
    public boolean initOnce() {
        try {
            //for :MSF
            Method mGetSendBatteryStatus = null;
            for (Method m : load("com/tencent/mobileqq/msf/sdk/MsfSdkUtils").getMethods()) {
                if (m.getName().equals("getSendBatteryStatus") && m.getReturnType()
                    .equals(int.class)) {
                    mGetSendBatteryStatus = m;
                    break;
                }
            }
            XposedBridge.hookMethod(mGetSendBatteryStatus, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) {
                        return;
                    }
                    param.setResult(getFakeBatteryStatus());
                }
            });
            Class<?> cBatteryBroadcastReceiver = load(
                "com.tencent.mobileqq.app.BatteryBroadcastReceiver");
            if (cBatteryBroadcastReceiver != null) {
                XposedHelpers
                    .findAndHookMethod(cBatteryBroadcastReceiver, "onReceive", Context.class,
                        Intent.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param)
                                throws Throwable {
                                if (SyncUtils.hasTlsFlag(_FLAG_MANUAL_CALL)) {
                                    return;
                                }
                                Intent intent = (Intent) param.args[1];
                                String action = intent.getAction();
                                if (action.equals("android.intent.action.ACTION_POWER_CONNECTED")
                                    || action
                                    .equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                                    if (mBatteryStatusRecvRef == null
                                        || mBatteryStatusRecvRef.get() != param.thisObject) {
                                        mBatteryStatusRecvRef = new WeakReference<>(
                                            (BroadcastReceiver) param.thisObject);
                                    }
                                } else if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                                    if (mBatteryLevelRecvRef == null
                                        || mBatteryLevelRecvRef.get() != param.thisObject) {
                                        mBatteryLevelRecvRef = new WeakReference<>(
                                            (BroadcastReceiver) param.thisObject);
                                    }
                                }
                                if (!isEnabled()) {
                                    return;
                                }
                                if (action.equals("android.intent.action.ACTION_POWER_CONNECTED")
                                    || action
                                    .equals("android.intent.action.ACTION_POWER_DISCONNECTED")) {
                                    if (isFakeBatteryCharging()) {
                                        lastFakeStatus = BatteryManager.BATTERY_STATUS_CHARGING;
                                        intent.setAction(
                                            "android.intent.action.ACTION_POWER_CONNECTED");
                                    } else {
                                        lastFakeStatus = BatteryManager.BATTERY_STATUS_DISCHARGING;
                                        intent.setAction(
                                            "android.intent.action.ACTION_POWER_DISCONNECTED");
                                    }
                                } else if (action.equals("android.intent.action.BATTERY_CHANGED")) {
                                    intent.putExtra(BatteryManager.EXTRA_LEVEL,
                                        lastFakeLevel = getFakeBatteryCapacity());
                                    intent.putExtra(BatteryManager.EXTRA_SCALE, 100);
                                    if (isFakeBatteryCharging()) {
                                        intent.putExtra(BatteryManager.EXTRA_STATUS,
                                            BatteryManager.BATTERY_STATUS_CHARGING);
                                        intent.putExtra(BatteryManager.EXTRA_PLUGGED,
                                            BatteryManager.BATTERY_PLUGGED_AC);
                                    } else {
                                        intent.putExtra(BatteryManager.EXTRA_STATUS,
                                            BatteryManager.BATTERY_STATUS_DISCHARGING);
                                        intent.putExtra(BatteryManager.EXTRA_PLUGGED, 0);
                                    }
                                }
                            }
                        });
            }
            //@MainProcess
            //接下去是UI stuff, 给自己看的
            //本来还想用反射魔改Binder/ActivityThread$ApplicationThread实现Xposed-less拦截广播onReceive的,太肝了,就不搞了
            if (Build.VERSION.SDK_INT >= 21) {
                BatteryManager batmgr = (BatteryManager) HostInformationProviderKt.getHostInfo()
                    .getApplication().getSystemService(Context.BATTERY_SERVICE);
                if (batmgr == null) {
                    logi("Wtf, init FakeBatteryHook but BatteryManager is null!");
                    return false;
                }
                if (Build.VERSION.SDK_INT < 23) {
                    //make a call to init mBatteryStats, so we don't care about the result
                    batmgr.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                }
                Field fBatteryPropertiesRegistrar = BatteryManager.class
                    .getDeclaredField("mBatteryPropertiesRegistrar");
                fBatteryPropertiesRegistrar.setAccessible(true);
                origRegistrar = fBatteryPropertiesRegistrar.get(batmgr);
                Class<?> cIBatteryPropertiesRegistrar = fBatteryPropertiesRegistrar.getType();
                if (origRegistrar == null) {
                    loge("Error! mBatteryPropertiesRegistrar(original) got null");
                    return false;
                }
                Class<?> cIBatteryStatus = null;
                Field fBatteryStatus = null;
                try {
                    fBatteryStatus = BatteryManager.class.getDeclaredField("mBatteryStats");
                    fBatteryStatus.setAccessible(true);
                    origStatus = fBatteryStatus.get(batmgr);
                    cIBatteryStatus = fBatteryStatus.getType();
                    if (origStatus == null) {
                        logw("FakeBatteryHook/W Field mBatteryStats found, but instance got null");
                    }
                } catch (NoSuchFieldException e) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        logw("FakeBatteryHook/W Field mBatteryStats not found, but SDK_INT is "
                            + Build.VERSION.SDK_INT);
                    }
                }
                Object proxy;
                if (origStatus != null && cIBatteryStatus != null) {
                    proxy = Proxy.newProxyInstance(Initiator.getPluginClassLoader(),
                        new Class[]{cIBatteryPropertiesRegistrar, cIBatteryStatus}, this);
                    fBatteryPropertiesRegistrar.set(batmgr, proxy);
                    fBatteryStatus.set(batmgr, proxy);
                } else {
                    proxy = Proxy.newProxyInstance(Initiator.getPluginClassLoader(),
                        new Class[]{cIBatteryPropertiesRegistrar}, this);
                    fBatteryPropertiesRegistrar.set(batmgr, proxy);
                }
            }
            SyncUtils.addBroadcastListener(this);
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    private void scheduleReceiveBatteryLevel() {
        BroadcastReceiver recv;
        if (mBatteryLevelRecvRef == null || (recv = mBatteryLevelRecvRef.get()) == null) {
            if (mBatteryStatusRecvRef == null || (recv = mBatteryStatusRecvRef.get()) == null) {
                return;
            }
        }
        final Intent intent = new Intent("android.intent.action.BATTERY_CHANGED");
        intent.putExtra(BatteryManager.EXTRA_LEVEL, lastFakeLevel = getFakeBatteryCapacity());
        intent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        intent.putExtra(BatteryManager.EXTRA_PRESENT, true);
        intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, "Li-ion");
        if (isFakeBatteryCharging()) {
            intent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_CHARGING);
            intent.putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_AC);
        } else {
            intent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING);
            intent.putExtra(BatteryManager.EXTRA_PLUGGED, 0);
        }
        doPostReceiveEvent(recv, HostInformationProviderKt.getHostInfo().getApplication(), intent);
    }

    private void scheduleReceiveBatteryStatus() {
        BroadcastReceiver recv;
        if (mBatteryStatusRecvRef == null || (recv = mBatteryStatusRecvRef.get()) == null) {
            if (mBatteryLevelRecvRef == null || (recv = mBatteryLevelRecvRef.get()) == null) {
                return;
            }
        }
        String act = isFakeBatteryCharging() ? "android.intent.action.ACTION_POWER_CONNECTED"
            : "android.intent.action.ACTION_POWER_DISCONNECTED";
        final Intent intent = new Intent(act);
        intent.putExtra(BatteryManager.EXTRA_LEVEL, getFakeBatteryCapacity());
        intent.putExtra(BatteryManager.EXTRA_SCALE, 100);
        intent.putExtra(BatteryManager.EXTRA_PRESENT, true);
        intent.putExtra(BatteryManager.EXTRA_TECHNOLOGY, "Li-ion");
        if (isFakeBatteryCharging()) {
            intent.putExtra(BatteryManager.EXTRA_STATUS,
                lastFakeStatus = BatteryManager.BATTERY_STATUS_CHARGING);
            intent.putExtra(BatteryManager.EXTRA_PLUGGED, BatteryManager.BATTERY_PLUGGED_AC);
        } else {
            intent.putExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_DISCHARGING);
            intent.putExtra(BatteryManager.EXTRA_PLUGGED, 0);
        }
        doPostReceiveEvent(recv, HostInformationProviderKt.getHostInfo().getApplication(), intent);
    }

    @Override
    public boolean onReceive(Context context, Intent intent) {
        if (ACTION_UPDATE_BATTERY_STATUS.equals(intent.getAction())) {
            if (isInited() && isEnabled()) {
                if (lastFakeLevel != getFakeBatteryCapacity()) {
                    scheduleReceiveBatteryLevel();
                }
                if (lastFakeStatus == -1 ||
                    lastFakeStatus == BatteryManager.BATTERY_STATUS_DISCHARGING
                        == isFakeBatteryCharging()) {
                    scheduleReceiveBatteryStatus();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        try {
            if (isEnabled()) {
                if (method.getName().equals("getProperty") && args.length == 2) {
                    int id = (int) args[0];
                    Parcelable prop = (Parcelable) args[1];
                    if (id == BatteryManager.BATTERY_PROPERTY_STATUS) {
                        if (isFakeBatteryCharging()) {
                            BatteryProperty_setLong(prop, BatteryManager.BATTERY_STATUS_CHARGING);
                        } else {
                            BatteryProperty_setLong(prop,
                                BatteryManager.BATTERY_STATUS_DISCHARGING);
                        }
                        return 0;
                    } else if (id == BatteryManager.BATTERY_PROPERTY_CAPACITY) {
                        BatteryProperty_setLong(prop, getFakeBatteryCapacity());
                        return 0;
                    }
                } else if (method.getName().equals("isCharging") && (args == null
                    || args.length == 0)) {
                    return isFakeBatteryCharging();
                }
            }
        } catch (Exception e) {
            log(e);
        }
        try {
            String className = method.getDeclaringClass().getName();
            if (className.endsWith("IBatteryPropertiesRegistrar")) {
                return method.invoke(origRegistrar, args);
            } else if (className.endsWith("IBatteryStats")) {
                return method.invoke(origStatus, args);
            } else if (className.endsWith("Object")) {
                if (method.getName().equals("toString")) {
                    return "a.a.a.a$Stub$Proxy@" + Integer.toHexString(hashCode());
                } else if (method.getName().equals("equals")) {
                    return args[0] == proxy;
                } else if (method.getName().equals("hashCode")) {
                    return hashCode();
                }
                return null;
            } else {
                //WTF QAQ
                logi("Panic, unexpected method " + method);
                return null;
            }
        } catch (InvocationTargetException ite) {
            throw ite.getCause();
        }
    }

    public void setFakeSendBatteryStatus(int val) {
        try {
            ConfigManager cfg = ConfigManager.getDefaultConfig();
            cfg.putInt(ConfigItems.qn_fake_bat_expr, val);
            cfg.save();
            Intent intent = new Intent(ACTION_UPDATE_BATTERY_STATUS);
            SyncUtils.sendGenericBroadcast(intent);
        } catch (IOException e) {
            log(e);
        }
    }

    public int getFakeBatteryStatus() {
        int val = ConfigManager.getDefaultConfig()
            .getIntOrDefault(ConfigItems.qn_fake_bat_expr, -1);
        if (val < 0) {
            return 0;//safe value
        }
        return val;
    }

    public boolean isFakeBatteryCharging() {
        return (getFakeBatteryStatus() & 128) > 0;
    }

    public int getFakeBatteryCapacity() {
        return getFakeBatteryStatus() & 127;
    }
}
