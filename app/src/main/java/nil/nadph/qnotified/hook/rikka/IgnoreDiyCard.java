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
package nil.nadph.qnotified.hook.rikka;

import android.content.Intent;
import android.os.Looper;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.NonNull;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

//屏蔽Diy卡片
public class IgnoreDiyCard extends BaseDelayableHook {
    public static final String rq_ignore_diy_card = "rq_ignore_diy_card";
    private static final IgnoreDiyCard self = new IgnoreDiyCard();
    private boolean isInit = false;

    private IgnoreDiyCard() {
    }

    @NonNull
    public static IgnoreDiyCard get() {
        return self;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            for (Method m : load("com.tencent.mobileqq.activity.FriendProfileCardActivity").getDeclaredMethods()) {
                Class<?>[] argt = m.getParameterTypes();
                if (Utils.getHostVersionCode32() <= 1406) {
                    if (m.getName().equals("a") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(void.class)) {
                        if (argt.length != 2) continue;
                        if (argt[1] != boolean.class) continue;
                        if (argt[0].getSuperclass() != Object.class) continue;
                    } else continue;
                } else {
                    if (m.getName().equals("b") && !Modifier.isStatic(m.getModifiers()) && m.getReturnType().equals(void.class)) {
                        if (argt.length != 1) continue;
                        if (argt[0].getSuperclass() == Intent.class) continue;
                        if (argt[0].getSuperclass() != Object.class) continue;
                    } else continue;
                }
                XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) return;
                        if (!isEnabled()) return;
                        Class<?> _ProfileCardInfo = ((Method) param.method).getParameterTypes()[0];
                        Object info = Utils.iget_object_or_null(param.thisObject, "a", _ProfileCardInfo);
                        if (info != null) {
                            Class<?> _Card = load("com.tencent.mobileqq.data.Card");
                            Object card = Utils.iget_object_or_null(info, "a", _Card);
                            if (card != null) {
                                Field f = _Card.getField("lCurrentStyleId");
                                if (f.getLong(card) == 22 || f.getLong(card) == 21) {
                                    f.setLong(card, 0);
                                }
                            } else {
                                loge("IgnoreDiyCard/W but info.<Card> == null");
                            }
                        } else {
                            loge("IgnoreDiyCard/W but info == null");
                        }
                    }
                });
            }
            isInit = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(rq_ignore_diy_card, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(rq_ignore_diy_card);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}

