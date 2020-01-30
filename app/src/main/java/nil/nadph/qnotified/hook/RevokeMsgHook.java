package nil.nadph.qnotified.hook;

import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.robv.android.xposed.XposedHelpers.*;
import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

/**
 * @author fkzhang
 * Created by fkzhang on 1/20/2016.
 * minor changes
 */
public class RevokeMsgHook extends BaseDelayableHook {
    private static final RevokeMsgHook self = new RevokeMsgHook();
    private Object mQQMsgFacade = null;
    private boolean inited = false;

    private RevokeMsgHook() {
    }

    public static RevokeMsgHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            XposedHelpers.findAndHookMethod(_QQMessageFacade(), "a", ArrayList.class, boolean.class, new XC_MethodHook(-10086) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    mQQMsgFacade = param.thisObject;
                    if (!isEnabled()) return;
                    ArrayList list = (ArrayList) param.args[0];
                    if (list == null || list.isEmpty()) return;
                    param.setResult(null);
                    Object obj = list.get(0);
                    try {
                        setMessageTip(obj);
                    } catch (Throwable t) {
                        log(t);
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

    private void setMessageTip(Object revokeMsgInfo) {
        String friendUin = (String) iget_object_or_null(revokeMsgInfo, "a", String.class);
        String senderUin = (String) iget_object_or_null(revokeMsgInfo, "b", String.class);
        int istroop = (int) getFirstNSFByType(revokeMsgInfo, int.class);
        long msgUid = (long) iget_object_or_null(revokeMsgInfo, "b", long.class);
        long shmsgseq = (long) iget_object_or_null(revokeMsgInfo, "a", long.class);
        long time = (long) iget_object_or_null(revokeMsgInfo, "c", long.class);
        Object qqApp = getQQAppInterface();
        String selfUin = "" + getLongAccountUin();
        if (selfUin.equals(senderUin))
            return;
        String uin = istroop == 0 ? senderUin : friendUin;
        Object msgObject = getMessage(uin, istroop, shmsgseq, msgUid);
        long id = getMessageId(msgObject);
        String msg = istroop == 0 ? getFriendName(null, senderUin) : getTroopName(friendUin, senderUin);
        if (id != 0) {
            if (isCallingFrom(_C2CMessageProcessor().getName()))
                return;
            msg = "\"" + msg + "\"" + "尝试撤回一条消息";
            String message = getMessageContent(msgObject);
            int msgtype = getMessageType(msgObject);
            if (msgtype == -1000 /*text msg*/) {
                if (!TextUtils.isEmpty(message)) {
                    msg += ": " + message;
                }
            }
            showMessageTip(friendUin, senderUin, msgUid, shmsgseq, time, msg, istroop);
        } else {
            msg = "\"" + msg + "\"" + "撤回了一条消息(没收到)";
            showMessageTip(friendUin, senderUin, msgUid, shmsgseq, time, msg, istroop);
        }
    }

    private void showMessageTip(String friendUin, String senderUin, long msgUid, long shmsgseq,
                                long time, String msg, int istroop) {
        if (msgUid != 0) {
            msgUid += new Random().nextInt();
        }
        try {
            List tips = createMessageTip(friendUin, senderUin, msgUid, shmsgseq, time + 1, msg, istroop);
            if (tips.isEmpty()) return;
            callMethod(mQQMsgFacade, "a", tips, Utils.getAccount());
        } catch (Throwable t) {
            XposedBridge.log(t);
        }
    }

    private List createMessageTip(String friendUin, String senderUin, long msgUid,
                                  long shmsgseq, long time, String msg, int istroop) {
        int msgtype = -2031; // MessageRecord.MSG_TYPE_REVOKE_GRAY_TIPS
        Object messageRecord = callStaticMethod(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), "a", msgtype);
        if (istroop == 0) { // private chat revoke
            callMethod(messageRecord, "init", Utils.getAccount(), senderUin, senderUin, msg, time, msgtype,
                    istroop, time);
        } else { // group chat revoke
            callMethod(messageRecord, "init", Utils.getAccount(), friendUin, senderUin, msg, time, msgtype,
                    istroop, time);
        }
        setObjectField(messageRecord, "msgUid", msgUid);
        setObjectField(messageRecord, "shmsgseq", shmsgseq);
        setObjectField(messageRecord, "isread", true);
        List<Object> list = new ArrayList<>();
        list.add(messageRecord);
        return list;
    }

    private String getFriendName(String friendUin, String senderUin) {
        //TODO: 群名片>备注>昵称>uin
        String nickname = null;
        try {
            if (friendUin != null) {
                nickname = (String) callStaticMethod(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), "c", getQQAppInterface(), friendUin, senderUin);
            }
            if (TextUtils.isEmpty(nickname)) {
                nickname = (String) callStaticMethod(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), "b", getQQAppInterface(), senderUin, true);
            }
            if (TextUtils.isEmpty(nickname)) {
                nickname = senderUin;
            }
        } catch (Exception e) {
            nickname = senderUin;
            log(e);
        }
        return nickname.replaceAll("\\u202E", "").trim();
    }

    private String getTroopName(String friendUin, String senderUin) {
        Object mTroopManager = null;
        try {
            mTroopManager = getTroopManager();
        } catch (Exception e) {
            log(e);
        }
        if (mTroopManager == null || friendUin == null)
            return getFriendName(friendUin, senderUin);
        Object troopMemberInfo = null;
        try {
            troopMemberInfo = invoke_virtual(mTroopManager, "a", friendUin, senderUin,
                    String.class, String.class, load("com.tencent.mobileqq.data.TroopMemberInfo"));
        } catch (Exception e) {
            log(e);
        }
        if (troopMemberInfo == null) {
            return getFriendName(friendUin, senderUin);
        }
        String nickname = (String) XposedHelpers.getObjectField(troopMemberInfo, "troopnick");
        if (TextUtils.isEmpty(nickname)) {
            nickname = (String) XposedHelpers.getObjectField(troopMemberInfo, "friendnick");
        }
        if (TextUtils.isEmpty(nickname)) {
            nickname = getFriendName(friendUin, senderUin);
        }
        return nickname.replaceAll("\\u202E", "").trim();
    }

    private Object getMessage(String uin, int istroop, long shmsgseq, long msgUid) {
        List list = null;
        try {
            list = (List) invoke_virtual(mQQMsgFacade, "a", uin, istroop, shmsgseq, msgUid, String.class, int.class,
                    long.class, long.class, List.class);
        } catch (Exception e) {
            log(e);
        }
        if (list == null || list.isEmpty())
            return null;
        return list.get(0);
    }

    private String getMessageContent(Object msgObject) {
        return (String) iget_object_or_null(msgObject, "msg");
    }

    private long getMessageId(Object msgObject) {
        if (msgObject == null)
            return 0;

        return (long) iget_object_or_null(msgObject, "msgUid");
    }

    private int getMessageType(Object msgObject) {
        if (msgObject == null)
            return -1;

        return (int) iget_object_or_null(msgObject, "msgtype");
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
        //return 0xFFFFFFFF;
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_anti_revoke_msg);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
