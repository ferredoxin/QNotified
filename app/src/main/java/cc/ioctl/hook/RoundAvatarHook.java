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

import static nil.nadph.qnotified.util.QQVersion.QQ_8_8_11;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.ferredoxin.ferredoxin_ui.base.UiSwitchPreference;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.annotation.UiItem;
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
@UiItem
public class RoundAvatarHook extends CommonDelayAbleHookBridge {

    public static final RoundAvatarHook INSTANCE = new RoundAvatarHook();
    private final UiSwitchPreference mUiSwitchPreference = this.new UiSwitchPreferenceItemFactory(
        "简洁模式圆头像", "From Rikka");

    RoundAvatarHook() {
        super(new DexDeobfStep(DexKit.C_SIMPLE_UI_UTIL));
    }

    @NonNull
    @Override
    public UiSwitchPreference getPreference() {
        return mUiSwitchPreference;
    }

    @Nullable
    @Override
    public String[] getPreferenceLocate() {
        return new String[]{"增强功能"};
    }

    @Override
    public boolean initOnce() {
        try {
            Method a = null, b = null;
            Class clz = DexKit.doFindClass(DexKit.C_SIMPLE_UI_UTIL);
            for (Method m : clz.getDeclaredMethods()) {
                if (!boolean.class.equals(m.getReturnType())) {
                    continue;
                }
                Class[] argt = m.getParameterTypes();
                if (argt.length != 1) {
                    continue;
                }
                if (String.class.equals(argt[0])) {
                    if (m.getName().equals("a")) {
                        a = m;
                    }
                    if (m.getName().equals("b")) {
                        b = m;
                    }
                }
            }
            XC_MethodHook hook = new XC_MethodHook(43) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) {
                        return;
                    }
                    param.setResult(false);
                }
            };
            if (b != null) {
                XposedBridge.hookMethod(b, hook);
            } else {
                XposedBridge.hookMethod(a, hook);
            }
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isValid() {
        Application app = HostInfo.getHostInfo().getApplication();
        return (app == null || !HostInfo.isTim()) &&
            HostInfo.getHostInfo().getVersionCode() < QQ_8_8_11;
    }
}
