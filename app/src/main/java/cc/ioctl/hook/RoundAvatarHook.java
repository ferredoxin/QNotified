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

import static nil.nadph.qnotified.util.Utils.log;

import android.app.Application;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
public class RoundAvatarHook extends CommonDelayableHook {

    public static final RoundAvatarHook INSTANCE = new RoundAvatarHook();

    RoundAvatarHook() {
        super("qn_round_avatar", new DexDeobfStep(DexKit.C_SIMPLE_UI_UTIL));
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
        Application app = HostInformationProviderKt.getHostInfo().getApplication();
        return app == null || !HostInformationProviderKt.getHostInfo().isTim();
    }
}
