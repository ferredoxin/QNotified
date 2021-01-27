package me.ketal.hook;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Toasts;

import static nil.nadph.qnotified.util.Initiator._SessionInfo;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class SortAtPanel extends CommonDelayableHook {
    public static final SortAtPanel INSTANCE = new SortAtPanel();
    boolean isSort = false;

    protected SortAtPanel() {
        super("ketal_At_Panel_Hook", SyncUtils.PROC_MAIN, false, new DexDeobfStep(DexKit.N_AtPanel__refreshUI), new DexDeobfStep(DexKit.N_AtPanel__showDialogAtView));
    }

    @Override
    protected boolean initOnce() {
        try {
            Class clazz = load("com.tencent.mobileqq.troop.quickat.ui.AtPanel");
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_AtPanel__showDialogAtView), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
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
                    try {
                        Object result = param.args[0];
                        if (isSort) {
                            Object sessionInfo = getFirstByType(param.thisObject, _SessionInfo());
                            logd(sessionInfo + "");
                            String troopUin = iget_object_or_null(sessionInfo, "troopUin", String.class);
                            if (troopUin == null)
                                troopUin = iget_object_or_null(sessionInfo, "a", String.class);
                            Object troopInfo = invoke_virtual(getTroopManager(), "b", troopUin, String.class, load("com.tencent.mobileqq.data.troop.TroopInfo"));
                            String ownerUin = iget_object_or_null(troopInfo, "troopowneruin", String.class);
                            String[] Administrator = iget_object_or_null(troopInfo, "Administrator", String.class).split("\\|");
                            List<String> admin = Arrays.asList(Administrator);
                            List list = getFirstByType(result, List.class);
                            String uin = getUin(list.get(0));
                            //logd("群号：" + troopUin + ",群主：" + ownerUin + "管理：" + admin);
                            boolean isAdmin = uin.equals("0");
                            Object temp;
                            for (int i = 1; i < list.size(); i++) {
                                Object member = list.get(i);
                                uin = getUin(member);
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
                    } catch (Exception e) {
                        Toasts.error(getApplication(), "版本不适配");
                        setEnabled(false);
                        log(e);
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
        //TODO find version
        return super.isValid();
    }
}
