package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.StartupHook;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;
import de.robv.android.xposed.*;
import java.lang.reflect.*;
import nil.nadph.qnotified.util.*;

public class RevokeMsgHook extends BaseDelayableHook {
    private static final RevokeMsgHook self = new RevokeMsgHook();

    RevokeMsgHook() {
    }

    public static RevokeMsgHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            XposedHelpers.findAndHookMethod(_QQMessageFacade(), "a", ArrayList.class, boolean.class, StartupHook.invokeRecord);
			XposedHelpers.findAndHookMethod(_QQMessageFacade(), "a", String.class, int.class, long.class, long.class, StartupHook.invokeRecord);
            /*XposedHelpers.findAndHookMethod(_QQMessageFacade(), "a", ArrayList.class, boolean.class,
                    new XC_MethodHook(-51) {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            try {
                                ArrayList list = (ArrayList) param.args[0];
                                if (isCallingFrom(_C2CMessageProcessor().getName()) ||
                                        list == null || list.isEmpty()) {
                                    param.setResult(null);
                                    return;
                                }
                                Object revokeMsgInfo = list.get(0);
                                String friendUin = (String) iget_object_or_null(revokeMsgInfo, "a", String.class);
                                String fromUin = (String) iget_object_or_null(revokeMsgInfo, "b", String.class);
                                int isTroop = (int) Utils.getFirstNSFByType(revokeMsgInfo, int.class);
                                long msgUid = (long) iget_object_or_null(revokeMsgInfo, "b", long.class);
                                long shmsgseq = (long) iget_object_or_null(revokeMsgInfo, "a", long.class);
                                long time = (long) iget_object_or_null(revokeMsgInfo, "c", long.class);
                                Object qqApp = getQQAppInterface();
                                String selfUin = "" + getLongAccountUin();
                                if (selfUin.equals(fromUin)) {
                                    param.setResult(null);
                                    return;
                                }
                                int msgType = -0x7ef; //sget_object(load("com/tencent/mobileqq/data/MessageRecord"),("MSG_TYPE_REVOKE_GRAY_TIPS");
                                List tip = getRevokeTip(qqApp, selfUin, friendUin, fromUin, msgUid, shmsgseq,
                                        time + 1, msgType, isTroop);
                                if (tip != null && !tip.isEmpty()) {
                                    invoke_virtual(param.thisObject, "a", tip, selfUin, List.class, String.class);
                                }
                                param.setResult(null);
                            } catch (Throwable e) {
                                log(e);
                            }
                        }
                    });*/
					
					
					
					
			XposedBridge.hookAllConstructors(load("com/tencent/mobileqq/graytip/UniteGrayTipParam"), new XC_MethodHook() {
					@Override
					protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
						/*String selfuin=getmyselfuin();*/
						String fromuin=(String)param.args[1];
						String message=(String)param.args[2];
						if (/*selfuin!=null&&!selfuin.isEmpty()&&*/message.contains("撤回")){
							Member m = param.method;
							String ret = m.getDeclaringClass().getSimpleName() + "->" + ((m instanceof Method) ? m.getName() : "<init>") + "(";
							Class[] argt;
							if (m instanceof Method)
								argt = ((Method) m).getParameterTypes();
							else if (m instanceof Constructor)
								argt = ((Constructor) m).getParameterTypes();
							else argt = new Class[0];
							for (int i = 0; i < argt.length; i++) {
								if (i != 0) ret += ",\n";
								ret += param.args[i];
							}
							ret += ")=" + param.getResult();
							Utils.log(ret);
							ret = "↑dump object:" + m.getDeclaringClass().getCanonicalName() + "\n";
							Field[] fs = m.getDeclaringClass().getDeclaredFields();
							for (int i = 0; i < fs.length; i++) {
								fs[i].setAccessible(true);
								ret += (i < fs.length - 1 ? "├" : "↓") + fs[i].getName() + "=" + ClazzExplorer.en_toStr(fs[i].get(param.thisObject)) + "\n";
							}
							log(ret);
							Utils.dumpTrace();
							/*if (!selfuin.equals(fromuin)){
								param.args[2]=getShowMsg(message);
							}*/
						}
					}
				});
					
					
					
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private List getRevokeTip(Object qqAppInterface, String selfUin, String friendUin, String fromUin,
                              long msgUid, long shmsgseq, long time, int msgType, int isTroop) throws Exception {
        Object messageRecord = invoke_static(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), "a", msgType, int.class);
        String name;
        if (isTroop == 0) {
            name = "对方";
        } else {
            try {
                name = "\"" + (String) invoke_static(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), "a", qqAppInterface, fromUin,
                        friendUin, isTroop == 1 ? 1 : 2, 0, load("com/tencent/mobileqq/app/QQAppInterface"), String.class, String.class, int.class, int.class) + "\"";
            } catch (Exception e) {
                name = fromUin;
            }
        }
        invoke_virtual(messageRecord, "init", selfUin, isTroop == 0 ? fromUin :
                        friendUin, fromUin, name + "尝试撤回一条消息", time, msgType, isTroop, time,
                String.class, String.class, String.class, String.class, long.class, int.class, int.class, long.class);
        iput_object(messageRecord, "msgUid", msgUid == 0 ? 0 : msgUid + new Random().nextInt());
        iput_object(messageRecord, "shmsgseq", shmsgseq);
        iput_object(messageRecord, "isread", true);
        List<Object> list = new ArrayList<>();
        list.add(messageRecord);
        return list;
    }

    @Override
    public int getEffectiveProc() {
        //return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
        return 0xFFFFFFFF;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_MSG_REC_FAC, DexKit.C_CONTACT_UTILS};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qn_anti_revoke_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
