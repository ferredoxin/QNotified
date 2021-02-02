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
package nil.nadph.qnotified.bridge;

import java.lang.reflect.Modifier;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.ReflexUtil.*;
import static nil.nadph.qnotified.util.Utils.*;

public class QQMessageFacade {

    public static Object get() {
        try {
            return invoke_virtual_any(Utils.getQQAppInterface(), Initiator._QQMessageFacade());
        } catch (Exception e) {
            loge("QQMessageFacade.get() failed!");
            log(e);
            return null;
        }
    }

    public static Object getMessageManager(int istroop) {
        try {
            return invoke_virtual_declared_modifier_any(get(), Modifier.PUBLIC, 0, istroop, int.class, Initiator._BaseMessageManager());
        } catch (Exception e) {
            loge("QQMessageFacade.getMessageManager() failed!");
            log(e);
            return null;
        }
    }

    public static void revokeMessage(Object msg) throws Exception {
        if (msg == null) throw new NullPointerException("msg == null");
        int istroop = (int) iget_object_or_null(msg, "istroop");
        Object mgr = getMessageManager(istroop);
        try {
            Object msg2 = invoke_static_any(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), msg, Initiator._MessageRecord(), Initiator._MessageRecord());
            long t = (long) iget_object_or_null(msg2, "time");
            t -= 1 + 10f * Math.random();
            iput_object(msg2, "time", t);
            Object msgCache = invoke_virtual_any(getQQAppInterface(), DexKit.doFindClass(DexKit.C_MessageCache));
            String methodName = "b"; //Default method name for QQ
            if (HostInformationProviderKt.getHostInformationProvider().isTim()) {
                methodName = ConfigTable.INSTANCE.getConfig(QQMessageFacade.class.getSimpleName());
            }
            invoke_virtual(msgCache, methodName, true, boolean.class, void.class);
            invoke_virtual_declared_fixed_modifier_ordinal(mgr, Modifier.PUBLIC, 0, Initiator._BaseMessageManager(), 2, 4, true, msg2, Initiator._MessageRecord(), void.class);
        } catch (Exception e) {
            loge("revokeMessage failed: " + msg);
            log(e);
            throw e;
        }
    }
}
