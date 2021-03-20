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
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;

@FunctionEntry
public class MutePokePacket extends CommonDelayableHook {

    public static final MutePokePacket INSTANCE = new MutePokePacket();

    private MutePokePacket() {
        super("qn_mute_poke");
    }

    @Override
    public boolean initOnce() {
        try {
            XposedHelpers
                .findAndHookMethod(load("com.tencent.mobileqq.data.MessageForPoke"), "doParse",
                    new XC_MethodHook(200) {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            if (LicenseStatus.sDisableCommonHooks || !isEnabled()) {
                                return;
                            }
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
