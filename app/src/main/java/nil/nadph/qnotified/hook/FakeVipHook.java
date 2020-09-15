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
package nil.nadph.qnotified.hook;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;

public class FakeVipHook extends BaseDelayableHook {

    private static final FakeVipHook self = new FakeVipHook();
    private boolean inited = false;

    private FakeVipHook() {
    }

    public static FakeVipHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = DexKit.doFindClass(DexKit.C_VIP_UTILS);
            Method getPrivilegeFlags = null;
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getReturnType().equals(int.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 2 && argt[0].equals(load("mqq/app/AppRuntime")) && argt[1].equals(String.class)) {
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
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_VIP_UTILS)};
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
