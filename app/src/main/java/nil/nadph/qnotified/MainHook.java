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
package nil.nadph.qnotified;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator._AllInOne;
import static nil.nadph.qnotified.util.Initiator._FriendProfileCardActivity;
import static nil.nadph.qnotified.util.Initiator._StartupDirector;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.getFirstNSFByType;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.new_instance;
import static nil.nadph.qnotified.util.Utils.getLongAccountUin;
import static nil.nadph.qnotified.util.Utils.isAlphaVersion;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import cc.ioctl.hook.GagInfoDisclosure;
import cc.ioctl.hook.MuteAtAllAndRedPacket;
import cc.ioctl.hook.MuteQZoneThumbsUp;
import cc.ioctl.hook.RevokeMsgHook;
import com.rymmmmm.hook.CustomSplash;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import me.kyuubiran.hook.RemoveCameraButton;
import me.kyuubiran.hook.RemoveRedDot;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.lifecycle.JumpActivityEntryHook;
import nil.nadph.qnotified.lifecycle.Parasitics;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.MainProcess;
import nil.nadph.qnotified.util.Utils;

/*TitleKit:Lcom/tencent/mobileqq/widget/navbar/NavBarCommon*/

@SuppressWarnings("rawtypes")
public class MainHook {

    private static MainHook SELF;

    boolean third_stage_inited = false;

    private MainHook() {
    }

    public static MainHook getInstance() {
        if (SELF == null) {
            SELF = new MainHook();
        }
        return SELF;
    }

    public static void openProfileCard(Context ctx, long uin) {
        try {
            Utils.logd("class="+ _AllInOne());
            Parcelable allInOne = (Parcelable) new_instance(
                _AllInOne(), "" + uin, 35,
                String.class, int.class);
            Intent intent = new Intent(ctx, _FriendProfileCardActivity());
            intent.putExtra("AllInOne", allInOne);
            ctx.startActivity(intent);
        } catch (Exception e) {
            log(e);
        }
    }

    /**
     * A屏黑主题,自用
     */
    public static void deepDarkTheme() {
        if (!SyncUtils.isMainProcess()) {
            return;
        }
        if (getLongAccountUin() != 1041703712) {
            return;
        }
        try {
            Class clz = load("com/tencent/mobileqq/activity/FriendProfileCardActivity");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0).setBackgroundColor(0xFF000000);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        View frame = ctx.findViewById(android.R.id.content);
                                        frame.setBackgroundColor(0xFF000000);
                                        View dk0 = ctx.findViewById(ctx.getResources()
                                            .getIdentifier("dk0", "id", ctx.getPackageName()));
                                        if (dk0 != null) {
                                            dk0.setBackgroundColor(0x00000000);
                                        }
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
            clz = load("com.tencent.mobileqq.activity.ChatSettingForTroop");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0).setBackgroundColor(0xFF000000);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            ctx.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FrameLayout frame = ctx.findViewById(android.R.id.content);
                                        frame.getChildAt(0).setBackgroundColor(0xFF000000);
                                        ViewGroup list = ctx.findViewById(ctx.getResources()
                                            .getIdentifier("common_xlistview", "id",
                                                ctx.getPackageName()));
                                        list.getChildAt(0).setBackgroundColor(0x00000000);
                                    } catch (Exception e) {
                                        log(e);
                                    }
                                }
                            });
                        }
                    }).start();
                }
            });
            clz = load("com.tencent.mobileqq.activity.TroopMemberListActivity");
            findAndHookMethod(clz, "doOnCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity ctx = (Activity) param.thisObject;
                    FrameLayout frame = ctx.findViewById(android.R.id.content);
                    frame.getChildAt(0)/*.getChildAt(0)*/.setBackgroundColor(0xFF000000);
                }
            });
        } catch (Exception e) {
            log(e);
        }
    }

    private static void injectLifecycleForProcess(Context ctx) {
        if (SyncUtils.isMainProcess()) {
            Parasitics.injectModuleResources(ctx.getApplicationContext().getResources());
        }
        if (SyncUtils.isTargetProcess(SyncUtils.PROC_MAIN | SyncUtils.PROC_PEAK)) {
            Parasitics.initForStubActivity(ctx);
        }
    }

    /**
     * dummy method, for development and test only
     */
    public static void onAppStartupForMain() {
        if (!isAlphaVersion()) {
            return;
        }
        deepDarkTheme();
    }

    public void performHook(Context ctx, Object step) {
        SyncUtils.initBroadcast(ctx);
        try {
            Class<?> _NewRuntime = Initiator.load("com.tencent.mobileqq.startup.step.NewRuntime");
            Method[] methods = _NewRuntime.getDeclaredMethods();
            Method doStep = null;
            if (methods.length == 1) {
                doStep = methods[0];
            } else {
                for (Method m : methods) {
                    if (Modifier.isProtected(m.getModifiers()) || m.getName().equals("doStep")) {
                        doStep = m;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(doStep, new XC_MethodHook(52) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    // fix error in :video, and QZone启动失败
                    Utils.$access$set$sAppRuntimeInit(true);
                }
            });
        } catch (Throwable e) {
            loge("NewRuntime/E hook failed: " + e);
            Utils.$access$set$sAppRuntimeInit(true);
        }
        injectLifecycleForProcess(ctx);
        BaseDelayableHook.allowEarlyInit(RevokeMsgHook.INSTANCE);
        BaseDelayableHook.allowEarlyInit(MuteQZoneThumbsUp.INSTANCE);
        BaseDelayableHook.allowEarlyInit(MuteAtAllAndRedPacket.INSTANCE);
        BaseDelayableHook.allowEarlyInit(GagInfoDisclosure.INSTANCE);
        BaseDelayableHook.allowEarlyInit(CustomSplash.INSTANCE);
        BaseDelayableHook.allowEarlyInit(RemoveCameraButton.INSTANCE);
        BaseDelayableHook.allowEarlyInit(RemoveRedDot.INSTANCE);
        if (SyncUtils.isMainProcess()) {
            ConfigItems.removePreviousCacheIfNecessary();
            injectStartupHookForMain(ctx);
            Class loadData = load("com/tencent/mobileqq/startup/step/LoadData");
            Method doStep = null;
            for (Method method : loadData.getDeclaredMethods()) {
                if (method.getReturnType().equals(boolean.class)
                    && method.getParameterTypes().length == 0) {
                    doStep = method;
                    break;
                }
            }
            XposedBridge.hookMethod(doStep, new XC_MethodHook(51) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (third_stage_inited) {
                        return;
                    }
                    Class director = _StartupDirector();
                    Object dir = iget_object_or_null(param.thisObject, "mDirector", director);
                    if (dir == null) {
                        dir = iget_object_or_null(param.thisObject, "a", director);
                    }
                    if (dir == null) {
                        dir = getFirstNSFByType(param.thisObject, director);
                    }
                    if (SyncUtils.isMainProcess()) {
                        ResUtils.loadThemeByArsc(
                            HostInformationProviderKt.getHostInfo().getApplication(), false);
                    }
                    InjectDelayableHooks.step(dir);
                    onAppStartupForMain();
                    third_stage_inited = true;
                }
            });
        } else {
            if (LicenseStatus.hasUserAcceptEula()) {
                Class director = _StartupDirector();
                Object dir = iget_object_or_null(step, "mDirector", director);
                if (dir == null) {
                    dir = iget_object_or_null(step, "a", director);
                }
                if (dir == null) {
                    dir = getFirstNSFByType(step, director);
                }
                InjectDelayableHooks.step(dir);
            }
        }
    }

    @MainProcess
    private void injectStartupHookForMain(Context ctx) {
        JumpActivityEntryHook.initForJumpActivityEntry(ctx);
    }

}
