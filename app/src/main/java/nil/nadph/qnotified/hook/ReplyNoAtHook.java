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

import de.robv.android.xposed.XC_MethodHook;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.LicenseStatus;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static me.ketal.util.TIMVersion.TIM_3_1_1;
import static me.singleneuron.util.QQVersion.QQ_8_1_3;
import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Utils.*;

public class ReplyNoAtHook extends CommonDelayableHook {
    private static final ReplyNoAtHook self = new ReplyNoAtHook();

    private ReplyNoAtHook() {
        super("qn_disable_auto_at", SyncUtils.PROC_MAIN, false);
    }

    public static ReplyNoAtHook get() {
        return self;
    }

    /**
     * 813 1246 k
     * 815 1258 l
     * 818 1276 l
     * 820 1296 l
     * 826 1320 m
     * 827 1328 m
     * ...
     * 836 1406 n ^
     * 848 1492 createAtMsg
     */
    @Override
    public boolean initOnce() {
        try {
            String method = ConfigTable.INSTANCE.getConfig(ReplyNoAtHook.class.getSimpleName());
            /*int ver = getHostVersionCode32();
            if (ver >= 1630) {
                method = "l";
            } else if (ver >= 1492) {
                method = "createAtMsg";
            } else if (ver >= 1406) {
                method = "n";
            } else if (ver > 1296) {
                method = "m";
            } else if (ver > 1246) {
                method = "l";
            } else if (ver >= 1246) {
                method = "k";
            }*/
            if (method == null) return false;
            findAndHookMethod(_BaseChatPie(), method, boolean.class, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    boolean p0 = (boolean) param.args[0];
                    if (!p0) param.setResult(null);
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
        if (isTim() && getHostVersionCode() >= TIM_3_1_1)
            return true;
        else return !isTim() && getHostVersionCode() >= QQ_8_1_3;
    }
}
