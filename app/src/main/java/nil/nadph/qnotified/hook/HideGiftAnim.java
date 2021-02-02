/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
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

import android.app.Application;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.util.LicenseStatus;

import static nil.nadph.qnotified.util.Initiator._TroopGiftAnimationController;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;

public class HideGiftAnim extends CommonDelayableHook {
    private static final HideGiftAnim self = new HideGiftAnim();

    HideGiftAnim() {
        super("qn_hide_gift_animation");
    }

    public static HideGiftAnim get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            Class clz = _TroopGiftAnimationController();
            XposedHelpers.findAndHookMethod(clz, "a", load("com/tencent/mobileqq/data/MessageForDeliverGiftTips"), new XC_MethodHook(39) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    param.setResult(null);
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isValid() {
        Application app = HostInformationProviderKt.getHostInformationProvider().getApplicationContext();
        return app == null || !HostInformationProviderKt.getHostInformationProvider().isTim();
    }
}
