/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2022 dmca@ioctl.cc
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

import static nil.nadph.qnotified.util.Initiator._MessageRecord;
import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.log;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.data.HostInfo;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.QQVersion;

@FunctionEntry
public class MuteAtAllAndRedPacket extends CommonDelayableHook {

    public static final MuteAtAllAndRedPacket INSTANCE = new MuteAtAllAndRedPacket();

    private MuteAtAllAndRedPacket() {
        super("__NOT_USED__", SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF);
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> cl_MessageInfo = load("com/tencent/mobileqq/troop/data/MessageInfo");
            if (cl_MessageInfo == null) {
                Class<?> c = _MessageRecord();
                cl_MessageInfo = c.getDeclaredField("mMessageInfo").getType();
            }
            /* @author qiwu */
            final int at_all_type =
                (HostInfo.requireMinQQVersion(QQVersion.QQ_7_8_0)) ? 13 : 12;
            for (Method m : cl_MessageInfo.getDeclaredMethods()) {
                if (m.getReturnType().equals(int.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 3) {
                        if (argt[0].equals(_QQAppInterface()) && argt[1].equals(boolean.class)
                            && argt[2].equals(String.class)) {
                            XposedBridge.hookMethod(m, new XC_MethodHook(60) {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param)
                                    throws Throwable {
                                    if (LicenseStatus.sDisableCommonHooks) {
                                        return;
                                    }
                                    int ret = (int) param.getResult();
                                    String troopuin = (String) param.args[2];
                                    if (ret != at_all_type) {
                                        return;
                                    }
                                    String muted = "," + ExfriendManager.getCurrent().getConfig()
                                        .getString(ConfigItems.qn_muted_at_all) + ",";
                                    if (muted.contains("," + troopuin + ",")) {
                                        param.setResult(0);
                                    }
                                }
                            });
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log(e);
        }
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.data.MessageForQQWalletMsg"),
                "doParse", new XC_MethodHook(200) {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        boolean mute = false;
                        int istroop = (Integer) iget_object_or_null(param.thisObject, "istroop");
                        if (istroop != 1) {
                            return;
                        }
                        String troopuin = (String) iget_object_or_null(param.thisObject,
                            "frienduin");
                        String muted = "," + ExfriendManager.getCurrent().getConfig()
                            .getString(ConfigItems.qn_muted_red_packet) + ",";
                        if (muted.contains("," + troopuin + ",")) {
                            mute = true;
                        }
                        if (mute) {
                            XposedHelpers.setObjectField(param.thisObject, "isread", true);
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
    public boolean checkPreconditions() {
        return true;
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
