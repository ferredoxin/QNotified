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
package nil.nadph.qnotified.bridge;

import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_static_any;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_any;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_declared_fixed_modifier_ordinal;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_declared_modifier_any;
import static nil.nadph.qnotified.util.ReflexUtil.iput_object;
import static nil.nadph.qnotified.util.Utils.getQQAppInterface;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

import java.lang.reflect.Modifier;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import me.singleneuron.util.QQVersion;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

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
            if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_6_0)) {
                return invoke_virtual_declared_fixed_modifier_ordinal(get(), Modifier.PUBLIC, 0,
                    Initiator._BaseQQMessageFacade(), 0, 1, true, istroop,
                    int.class, Initiator._BaseMessageManager());
            }
            return invoke_virtual_declared_modifier_any(get(), Modifier.PUBLIC, 0, istroop,
                int.class, Initiator._BaseMessageManager());
        } catch (Exception e) {
            loge("QQMessageFacade.getMessageManager() failed!");
            log(e);
            return null;
        }
    }

    public static void revokeMessage(Object msg) throws Exception {
        if (msg == null) {
            throw new NullPointerException("msg == null");
        }
        int istroop = (int) iget_object_or_null(msg, "istroop");
        Object mgr = getMessageManager(istroop);
        try {
            Object msg2 = invoke_static_any(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), msg,
                Initiator._MessageRecord(), Initiator._MessageRecord());
            long t = (long) iget_object_or_null(msg2, "time");
            t -= 1 + 10f * Math.random();
            iput_object(msg2, "time", t);
            Object msgCache = invoke_virtual_any(getQQAppInterface(),
                DexKit.doFindClass(DexKit.C_MessageCache));
            String methodName = "b"; //Default method name for QQ
            if (HostInformationProviderKt.getHostInfo().isTim()) {
                methodName = ConfigTable.INSTANCE.getConfig(QQMessageFacade.class.getSimpleName());
            }
            invoke_virtual(msgCache, methodName, true, boolean.class, void.class);
            if (HostInformationProviderKt.requireMinQQVersion(QQVersion.QQ_8_6_0)) {
                invoke_virtual_declared_fixed_modifier_ordinal(mgr, Modifier.PUBLIC, 0,
                    Initiator._BaseMessageManager(), 4, 7, true, msg2, Initiator._MessageRecord(),
                    void.class);
            } else {
                invoke_virtual_declared_fixed_modifier_ordinal(mgr, Modifier.PUBLIC, 0,
                    Initiator._BaseMessageManager(), 2, 4, true, msg2, Initiator._MessageRecord(),
                    void.class);
            }
        } catch (Exception e) {
            loge("revokeMessage failed: " + msg);
            log(e);
            throw e;
        }
    }
}
