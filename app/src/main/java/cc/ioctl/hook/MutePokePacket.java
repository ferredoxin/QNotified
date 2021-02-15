/*
 * QNotified - An Xposed module for QQ/TIM
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
package cc.ioctl.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.log;

public class MutePokePacket extends CommonDelayableHook {
    private static final MutePokePacket self = new MutePokePacket();

    private MutePokePacket() {
        super("qn_mute_poke");
    }

    public static MutePokePacket get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.data.MessageForPoke"), "doParse", new XC_MethodHook(200) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks || !isEnabled()) return;
                    XposedHelpers.setObjectField(param.thisObject, "isPlayed", true);
                }
            });
            return true;

        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
