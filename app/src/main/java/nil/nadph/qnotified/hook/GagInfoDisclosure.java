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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.ContactUtils;
import nil.nadph.qnotified.bridge.GreyTipBuilder;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.bridge.GreyTipBuilder.MSG_TYPE_TROOP_GAP_GRAY_TIPS;
import static nil.nadph.qnotified.util.ReflexUtil.findMethodByTypes_1;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_declared_ordinal_modifier;
import static nil.nadph.qnotified.util.Utils.log;

public class GagInfoDisclosure extends CommonDelayableHook {
    private static final GagInfoDisclosure self = new GagInfoDisclosure();

    GagInfoDisclosure() {
        // TODO: 2020/6/12 Figure out whether MSF is really needed
        super("qn_disclose_gag_info", SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF, new DexDeobfStep(DexKit.C_MSG_REC_FAC));
    }

    public static GagInfoDisclosure get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> clzGagMgr = Initiator._TroopGagMgr();
            Method m1 = findMethodByTypes_1(clzGagMgr, void.class, String.class, long.class, long.class, int.class, String.class, String.class, boolean.class);
            XposedBridge.hookMethod(m1, new XC_MethodHook(48) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    String selfUin = Utils.getAccount() + "";
                    String troopUin = (String) param.args[0];
                    long time = (long) param.args[1];
                    long interval = (long) param.args[2];
                    int msgseq = (int) param.args[3];
                    String opUin = (String) param.args[4];
                    String victimUin = (String) param.args[5];
                    String opName = ContactUtils.getTroopMemberNick(troopUin, opUin);
                    String victimName = ContactUtils.getTroopMemberNick(troopUin, victimUin);
                    GreyTipBuilder builder = GreyTipBuilder.create(MSG_TYPE_TROOP_GAP_GRAY_TIPS);
                    if (selfUin.endsWith(victimUin)) {
                        builder.append("你");
                    } else {
                        builder.append(' ').appendTroopMember(victimUin, victimName).append(' ');
                    }
                    builder.append("被");
                    if (selfUin.endsWith(opUin)) {
                        builder.append("你");
                    } else {
                        builder.append(' ').appendTroopMember(opUin, opName).append(' ');
                    }
                    if (interval == 0) {
                        builder.append("解除禁言");
                    } else {
                        builder.append("禁言").append(getGagTimeString(interval));
                    }
                    Object msg = builder.build(troopUin, 1, opUin, time, msgseq);
                    List<Object> list = new ArrayList<>();
                    list.add(msg);
                    invoke_virtual_declared_ordinal_modifier(Utils.getQQMessageFacade(), 0, 4, false, Modifier.PUBLIC, 0,
                            list, Utils.getAccount(), List.class, String.class, void.class);
                    param.setResult(null);
                }
            });
            Method m2 = findMethodByTypes_1(clzGagMgr, void.class, String.class, String.class, long.class, long.class, int.class, boolean.class, boolean.class);
            XposedBridge.hookMethod(m2, new XC_MethodHook(48) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    if (!isEnabled()) return;
                    String selfUin = Utils.getAccount() + "";
                    String troopUin = (String) param.args[0];
                    String opUin = (String) param.args[1];
                    long time = (long) param.args[2];
                    long interval = (long) param.args[3];
                    int msgseq = (int) param.args[4];
                    boolean gagTroop = (boolean) param.args[5];
                    String opName = ContactUtils.getTroopMemberNick(troopUin, opUin);
                    GreyTipBuilder builder = GreyTipBuilder.create(MSG_TYPE_TROOP_GAP_GRAY_TIPS);
                    if (gagTroop) {
                        if (selfUin.endsWith(opUin)) {
                            builder.append("你");
                        } else {
                            builder.append(' ').appendTroopMember(opUin, opName).append(' ');
                        }
                        builder.append(interval == 0 ? "关闭了全员禁言" : "开启了全员禁言");
                    } else {
                        builder.append("你被 ").appendTroopMember(opUin, opName);
                        if (interval == 0) {
                            builder.append(" 解除禁言");
                        } else {
                            builder.append(" 禁言").append(getGagTimeString(interval));
                        }
                    }
                    Object msg = builder.build(troopUin, 1, opUin, time, msgseq);
                    List<Object> list = new ArrayList<>();
                    list.add(msg);
                    invoke_virtual_declared_ordinal_modifier(Utils.getQQMessageFacade(), 0, 4, false, Modifier.PUBLIC, 0,
                            list, Utils.getAccount(), List.class, String.class, void.class);
                    param.setResult(null);
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    public static String getGagTimeString(long sec) {
        String _min = "分钟";
        String _hour = "小时";
        String _day = "天";
        if (sec < 60) {
            return 1 + _min;
        }
        long fsec = 59 + sec;
        long d = fsec / 86400;
        long h = (fsec - (86400 * d)) / 3600;
        long m = ((fsec - (86400 * d)) - (3600 * h)) / 60;
        String ret = "";
        if (d > 0) {
            ret = ret + d + _day;
        }
        if (h > 0) {
            ret = ret + h + _hour;
        }
        if (m > 0) {
            return ret + m + _min;
        }
        return ret;
    }
}
