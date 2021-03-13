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

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
public class FakeVipHook extends CommonDelayableHook {

    public static final FakeVipHook INSTANCE = new FakeVipHook();

    private FakeVipHook() {
        super("__NOT_USED__", new DexDeobfStep(DexKit.C_VIP_UTILS));
    }

    @Override
    public boolean initOnce() {
        try {
            Class clz = DexKit.doFindClass(DexKit.C_VIP_UTILS);
            Method getPrivilegeFlags = null;
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getReturnType().equals(int.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 2 && argt[0].equals(load("mqq/app/AppRuntime")) && argt[1]
                        .equals(String.class)) {
                        getPrivilegeFlags = m;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(getPrivilegeFlags, new XC_MethodHook(-52) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    int ret;
                    //null is self
                    if (param.args[1] == null) {
                        ret = (int) param.getResult();
                        param.setResult(4 | ret);//svip
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }
}
