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
package nil.nadph.qnotified.startup;

import android.app.Application;
import android.content.Context;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Natives;

import static nil.nadph.qnotified.startup.LogUtil.log;
import static nil.nadph.qnotified.util.Utils.checkLogFlag;
import static nil.nadph.qnotified.util.Utils.getBuildTimestamp;

public class StartupRoutine {

    private StartupRoutine() {
        throw new AssertionError("No instance for you!");
    }

    /**
     * From now on, kotlin, androidx or third party libraries may be accessed
     * without crashing the ART.
     *
     * @param ctx         Application context for host
     * @param step        Step instance
     * @param lpwReserved null, not used
     * @param bReserved   false, not used
     */
    public static void execPostStartupInit(Context ctx, Object step, String lpwReserved, boolean bReserved) {
        HostInformationProviderKt.init((Application) ctx);
        Initiator.init(ctx.getClassLoader());
        checkLogFlag();
        try {
            Natives.load(ctx);
        } catch (Throwable e3) {
            log(e3);
        }
        if (getBuildTimestamp() < 0) {
            return;
        }
        MainHook.getInstance().performHook(ctx, step);
    }
}
