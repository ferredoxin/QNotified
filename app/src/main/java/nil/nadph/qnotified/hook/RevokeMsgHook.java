package nil.nadph.qnotified.hook;

import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
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
 * Modified by cinit: minor changes at GreyTip on 2020/3/8 Sun.20:33
 */
public class RevokeMsgHook extends BaseDelayableHook {
    public static final String qn_anti_revoke_msg = "qn_anti_revoke_msg";
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
                    param.setResult(null);
                    if (list == null || list.isEmpty()) return;
                    Object obj = list.get(0);
                    try {
                        onRevokeMsg(obj);
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

    private void onRevokeMsg(Object revokeMsgInfo) {
        String entityUin = (String) iget_object_or_null(revokeMsgInfo, "a", String.class);//frienduin
        String revokerUin = (String) iget_object_or_null(revokeMsgInfo, "b", String.class);//senduin
        String authorUin = (String) iget_object_or_null(revokeMsgInfo, "d", String.class);
        int istroop = (int) getFirstNSFByType(revokeMsgInfo, int.class);
        long msgUid = (long) iget_object_or_null(revokeMsgInfo, "b", long.class);
        long shmsgseq = (long) iget_object_or_null(revokeMsgInfo, "a", long.class);
        long time = (long) iget_object_or_null(revokeMsgInfo, "c", long.class);
        String selfUin = "" + getLongAccountUin();
        if (selfUin.equals(revokerUin)) {
            return;
        }
        String uin = istroop == 0 ? revokerUin : entityUin;
        Object msgObject = getMessage(uin, istroop, shmsgseq, msgUid);
        long id = getMessageUid(msgObject);
        if (isCallingFrom(_C2CMessageProcessor().getName())) return;
        boolean isGroupChat = istroop != 0;
        long newMsgUid;
        if (msgUid != 0) {
            newMsgUid = msgUid + new Random().nextInt();
        } else {
            newMsgUid = 0;
        }
        Object revokeGreyTip;
        if (isGroupChat) {
            if (revokerUin.equals(authorUin)) {
                //自己撤回
                String revokerNick = getTroopMemberNick(entityUin, revokerUin);
                String greyMsg = "\"" + revokerNick + "\u202d\"";
                if (msgObject != null) {
                    greyMsg += "尝试撤回一条消息";
                    String message = getMessageContent(msgObject);
                    int msgtype = getMessageType(msgObject);
                    if (msgtype == -1000 /*text msg*/) {
                        if (!TextUtils.isEmpty(message)) {
                            greyMsg += ": " + message;
                        }
                    }
                } else {
                    greyMsg += "撤回了一条消息(没收到)";
                }
                revokeGreyTip = createBareHighlightGreyTip(entityUin, istroop, revokerUin, time + 1, greyMsg, newMsgUid, shmsgseq);
                addHightlightItem(revokeGreyTip, 1, 1 + revokerNick.length(), createTroopMemberHighlightItem(revokerUin));
            } else {
                //被权限狗撤回(含管理,群主)
                String revokerNick = getTroopMemberNick(entityUin, revokerUin);
                String authorNick = getTroopMemberNick(entityUin, authorUin);
                if (msgObject == null) {
                    String greyMsg = "\"" + revokerNick + "\u202d\"撤回了\"" + authorNick + "\u202d\"的消息(没收到)";
                    revokeGreyTip = createBareHighlightGreyTip(entityUin, istroop, revokerUin, time + 1, greyMsg, newMsgUid, shmsgseq);
                    addHightlightItem(revokeGreyTip, 1, 1 + revokerNick.length(), createTroopMemberHighlightItem(revokerUin));
                    addHightlightItem(revokeGreyTip, 1 + revokerNick.length() + 1 + 5, 1 + revokerNick.length() + 1 + 5 + authorNick.length(), createTroopMemberHighlightItem(authorUin));
                } else {
                    String greyMsg = "\"" + revokerNick + "\u202d\"尝试撤回\"" + authorNick + "\u202d\"的消息";
                    String message = getMessageContent(msgObject);
                    int msgtype = getMessageType(msgObject);
                    if (msgtype == -1000 /*text msg*/) {
                        if (!TextUtils.isEmpty(message)) {
                            greyMsg += ": " + message;
                        }
                    }
                    revokeGreyTip = createBareHighlightGreyTip(entityUin, istroop, revokerUin, time + 1, greyMsg, newMsgUid, shmsgseq);
                    addHightlightItem(revokeGreyTip, 1, 1 + revokerNick.length(), createTroopMemberHighlightItem(revokerUin));
                    addHightlightItem(revokeGreyTip, 1 + revokerNick.length() + 1 + 6, 1 + revokerNick.length() + 1 + 6 + authorNick.length(), createTroopMemberHighlightItem(authorUin));
                }
            }
        } else {
            String greyMsg;
            if (msgObject == null) {
                greyMsg = "对方撤回了一条消息(没收到)";
            } else {
                String message = getMessageContent(msgObject);
                int msgtype = getMessageType(msgObject);
                greyMsg = "对方尝试撤回一条消息";
                if (msgtype == -1000 /*text msg*/) {
                    if (!TextUtils.isEmpty(message)) {
                        greyMsg += ": " + message;
                    }
                }
            }
            revokeGreyTip = createBarePlainGreyTip(revokerUin, istroop, revokerUin, time + 1, greyMsg, newMsgUid, shmsgseq);
        }
        List<Object> list = new ArrayList<>();
        list.add(revokeGreyTip);
        callMethod(mQQMsgFacade, "a", list, Utils.getAccount());
    }

    private Bundle createTroopMemberHighlightItem(String memberUin) {
        Bundle bundle = new Bundle();
        bundle.putInt("key_action", 5);
        bundle.putString("troop_mem_uin", memberUin);
        bundle.putBoolean("need_update_nick", true);
        return bundle;
    }

    private Object createBareHighlightGreyTip(String entityUin, int istroop, String fromUin, long time, String msg, long msgUid, long shmsgseq) {
        int msgtype = -2030;// MessageRecord.MSG_TYPE_TROOP_GAP_GRAY_TIPS
        Object messageRecord = callStaticMethod(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), "a", msgtype);
        callMethod(messageRecord, "init", Utils.getAccount(), entityUin, fromUin, msg, time, msgtype, istroop, time);
        setObjectField(messageRecord, "msgUid", msgUid);
        setObjectField(messageRecord, "shmsgseq", shmsgseq);
        setObjectField(messageRecord, "isread", true);
        return messageRecord;
    }

    private Object createBarePlainGreyTip(String entityUin, int istroop, String fromUin, long time, String msg, long msgUid, long shmsgseq) {
        int msgtype = -2031;// MessageRecord.MSG_TYPE_REVOKE_GRAY_TIPS
        Object messageRecord = callStaticMethod(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), "a", msgtype);
        callMethod(messageRecord, "init", Utils.getAccount(), entityUin, fromUin, msg, time, msgtype, istroop, time);
        setObjectField(messageRecord, "msgUid", msgUid);
        setObjectField(messageRecord, "shmsgseq", shmsgseq);
        setObjectField(messageRecord, "isread", true);
        return messageRecord;
    }

    private void addHightlightItem(Object msgForGreyTip, int start, int end, Bundle bundle) {
        try {
            invoke_virtual(msgForGreyTip, "addHightlightItem", start, end, bundle, int.class, int.class, Bundle.class);
        } catch (Exception e) {
            log(e);
        }
    }

    public String getTroopMemberNick(String troopUin, String memberUin) {
        if (troopUin != null && troopUin.length() > 0) {
            try {
                Object mTroopManager = getTroopManager();
                Object troopMemberInfo = invoke_virtual(mTroopManager, "a", troopUin, memberUin,
                        String.class, String.class, load("com.tencent.mobileqq.data.TroopMemberInfo"));
                if (troopMemberInfo != null) {
                    String troopnick = (String) XposedHelpers.getObjectField(troopMemberInfo, "troopnick");
                    if (troopnick != null) {
                        String ret = troopnick.replaceAll("\\u202E", "");
                        if (ret.trim().length() > 0) {
                            return ret;
                        }
                    }
                }
            } catch (Exception e) {
                log(e);
            }
            try {
                String ret;
                String nickname = (String) callStaticMethod(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), "c", getQQAppInterface(), troopUin, memberUin);
                if (nickname != null && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                    return ret;
                }
            } catch (Throwable e) {
                log(e);
            }
        }
        try {
            String ret;
            String nickname = (String) callStaticMethod(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), "b", getQQAppInterface(), memberUin, true);
            if (nickname != null && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                return ret;
            }
        } catch (Throwable e) {
            log(e);
        }
        //**sigh**
        return memberUin;
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

    private long getMessageUid(Object msgObject) {
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
        //FIXME: is MSF really necessary?
        return SyncUtils.PROC_MAIN | SyncUtils.PROC_MSF;
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
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_anti_revoke_msg, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
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
