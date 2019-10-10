package nil.nadph.qnotified.hook;

import de.robv.android.xposed.XC_MethodHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.DexKit;

import java.util.ArrayList;
import java.util.List;

import static nil.nadph.qnotified.util.Utils.log;
import de.robv.android.xposed.*;

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
            XposedHelpers.findAndHookMethod(_QQMessageFacade(),"a",ArrayList.class, boolean.class,
                    new XC_MethodHook(-51) {
                @Override
                protected void onBeforeHooked(@NonNull XC_MemberHook.MemberHookParam param) {
                    ArrayList list = param.args[0];
                    if (isCallingFrom(QQConfigUtils.findClass(C2CMessageProcessor)) ||
                            list == null || list.isEmpty()) {
                        param.setResult(null);
                        return;
                    }
                    Object revokeMsgInfo = list.get(0);

                    XField xField = XField.create(revokeMsgInfo);
                    String friendUin = xField.exact(String.class, "a").get();
                    String fromUin = xField.exact(String.class, "b").get();
                    int isTroop = xField.exact(int.class, "a").get();
                    long msgUid = xField.exact(long.class, "b").get();
                    long shmsgseq = xField.exact(long.class, "a").get();
                    long time = xField.exact(long.class, "c").get();

                    Object qqApp = XField.create(param).type(QQAppInterface).get();
                    String selfUin = XMethod.create(qqApp).name("getCurrentAccountUin").invoke();

                    if (selfUin.equals(fromUin)) {
                        param.setResult(null);
                        return;
                    }

                    int msgType = XField.create($(MessageRecord)).name("MSG_TYPE_REVOKE_GRAY_TIPS").get();
                    List tip = getRevokeTip(qqApp, selfUin, friendUin, fromUin, msgUid, shmsgseq,
                            time + 1, msgType, isTroop);
                    if (tip != null && !tip.isEmpty()) {
                        XMethod.create(param).name("a").invoke(tip, selfUin);
                    }
                    param.setResult(null);
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
                              long msgUid, long shmsgseq, long time, int msgType, int isTroop) {
        Object messageRecord = XMethod.create($(MessageRecordFactory)).name("a").invoke(msgType);

        String name;
        if (isTroop == 0) {
            name = "对方";
        } else {
            name = XMethod.create($(ContactUtils)).name("a").invoke(qqAppInterface, fromUin,
                    friendUin, isTroop == 1 ? 1 : 2, 0);
        }

        XMethod.create(messageRecord).name("init").invoke(selfUin, isTroop == 0 ? fromUin :
                friendUin, fromUin, name + "尝试撤回一条消息", time, msgType, isTroop, time);

        XField.create(messageRecord).name("msgUid").set(msgUid == 0 ? 0 : msgUid + new Random().nextInt());
        XField.create(messageRecord).name("shmsgseq").set(shmsgseq);
        XField.create(messageRecord).name("isread").set(true);

        List<Object> list = new ArrayList<>();
        list.add(messageRecord);
        return list;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN|SyncUtils.PROC_MSF;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_ABS_GAL_SCENE};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
