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
package me.ketal.hook;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;

import static me.ketal.util.TIMVersion.TIM_3_1_1;
import static me.singleneuron.util.QQVersion.QQ_8_1_3;
import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.Utils.*;

public class SortAtPanel extends CommonDelayableHook {
    public static final SortAtPanel INSTANCE = new SortAtPanel();
    boolean isSort = false;

    protected SortAtPanel() {
        super("ketal_At_Panel_Hook", new DexDeobfStep(DexKit.N_AtPanel__refreshUI), new DexDeobfStep(DexKit.N_AtPanel__showDialogAtView));
    }

    @Override
    protected boolean initOnce() {
        try {
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_AtPanel__showDialogAtView), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (!isEnabled())
                        return;
                    String key = (String) param.args[1];
                    isSort = TextUtils.isEmpty(key);
                }
            });
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_AtPanel__refreshUI), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled())
                        return;
                    Object result = param.args[0];
                    if (isSort) {
                        Object sessionInfo = getFirstByType(param.thisObject, _SessionInfo());
                        String troopUin = iget_object_or_null(sessionInfo, "troopUin", String.class);
                        if (troopUin == null)
                            troopUin = iget_object_or_null(sessionInfo, "a", String.class);
                        Class<?> clzTroopInfo = load("com.tencent.mobileqq.data.troop.TroopInfo");
                        if (clzTroopInfo == null)
                            clzTroopInfo = load("com.tencent.mobileqq.data.TroopInfo");
                        Object troopInfo = invoke_virtual(getTroopManager(), "b", troopUin, String.class, clzTroopInfo);
                        String ownerUin = iget_object_or_null(troopInfo, "troopowneruin", String.class);
                        String[] administrator = iget_object_or_null(troopInfo, "Administrator", String.class).split("\\|");
                        List<String> admin = Arrays.asList(administrator);
                        List<Object> list = getFirstByType(result, List.class);
                        String uin = getUin(list.get(0));
                        boolean isAdmin = "0".equals(uin);
                        Object temp;
                        for (int i = 1; i < list.size(); i++) {
                            Object member = list.get(i);
                            uin = getUin(member);
                            if (uin == null) {
                                throw new NullPointerException("uin == null");
                            }
                            if (uin.equals(ownerUin)) {
                                temp = member;
                                list.remove(member);
                                list.add(isAdmin ? 1 : 0, temp);
                            } else if (admin.contains(uin)) {
                                temp = member;
                                list.remove(member);
                                list.add(isAdmin ? 2 : 1, temp);
                            }
                        }
                    }
                }
            });
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    private String getUin(Object member) {
        String uin = iget_object_or_null(member, "uin", String.class);
        if (uin == null) uin = iget_object_or_null(member, "a", String.class);
        try {
            Long.parseLong(uin);
        } catch (Exception e) {
            return null;
        }
        return uin;
    }

    @Override
    public boolean isValid() {
        if (HostInformationProviderKt.getHostInformationProvider().isTim() && HostInformationProviderKt.getHostInformationProvider().getVersionCode() >= TIM_3_1_1)
            return true;
        else return !HostInformationProviderKt.getHostInformationProvider().isTim() && HostInformationProviderKt.getHostInformationProvider().getVersionCode() >= QQ_8_1_3;
    }
}
