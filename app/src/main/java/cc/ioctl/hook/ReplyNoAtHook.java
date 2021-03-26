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

import java.lang.reflect.Method;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static me.ketal.util.PlayQQVersion.PlayQQ_8_2_9;
import static me.ketal.util.TIMVersion.TIM_3_1_1;
import static me.singleneuron.util.QQVersion.QQ_8_1_3;
import static me.singleneuron.util.QQVersion.QQ_8_6_0;
import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Utils.log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.DexMethodDescriptor;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.LicenseStatus;

@FunctionEntry
public class ReplyNoAtHook extends CommonDelayableHook {

    public static final ReplyNoAtHook INSTANCE = new ReplyNoAtHook();

    private ReplyNoAtHook() {
        super("qn_disable_auto_at");
    }

    /**
     * 813 1246 k 815 1258 l 818 1276 l 820 1296 l 826 1320 m 827 1328 m ... 836 1406 n ^ 848 1492
     * createAtMsg
     */
    @Override
    public boolean initOnce() {
        try {
            String method = ConfigTable.INSTANCE.getConfig(ReplyNoAtHook.class.getSimpleName());
            if (method == null) {
                return false;
            }
            if (HostInformationProviderKt.requireMinQQVersion(QQ_8_6_0)) {
                Method m = new DexMethodDescriptor("Lcom/tencent/mobileqq/activity/aio/rebuild/input/InputUIUtils;->a(Lcom/tencent/mobileqq/activity/aio/core/AIOContext;Lcom/tencent/mobileqq/activity/aio/BaseSessionInfo;Z)V").getMethodInstance(Initiator.getHostClassLoader());
                XposedBridge.hookMethod(m, new XC_MethodHook(49) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        if (!isEnabled()) {
                            return;
                        }
                        boolean p0 = (boolean) param.args[2];
                        if (!p0) {
                            param.setResult(null);
                        }
                    }
                });
                return true;
            }
            findAndHookMethod(_BaseChatPie(), method, boolean.class, new XC_MethodHook(49) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
                    boolean p0 = (boolean) param.args[0];
                    if (!p0) {
                        param.setResult(null);
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
    public boolean isValid() {
        return HostInformationProviderKt.requireMinVersion(QQ_8_1_3, TIM_3_1_1, PlayQQ_8_2_9);
    }
}
