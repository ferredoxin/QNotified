package nil.nadph.qnotified.hook;

import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.pk.RevokeMsgInfoImpl;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static nil.nadph.qnotified.util.Initiator.*;
import static nil.nadph.qnotified.util.Utils.*;

/**
 * @author fkzhang
 * Created by fkzhang on 1/20/2016.
 * Changes by cinit:
 * 2020/03/08 Sun.20:33 Minor changes at GreyTip
 * 2020/04/08 Tue.23:21 Use RevokeMsgInfoImpl for ease, wanny cry
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
            Method revokeMsg = null;
            for (Method m : _QQMessageFacade().getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class)) {
                    Class<?>[] argt = m.getParameterTypes();
                    if (argt.length == 2 && argt[0].equals(ArrayList.class) && argt[1].equals(boolean.class)) {
                        revokeMsg = m;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(revokeMsg, new XC_MethodHook(-10086) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    mQQMsgFacade = param.thisObject;
                    if (!isEnabled()) return;
                    ArrayList list = (ArrayList) param.args[0];
                    param.setResult(null);
                    if (list == null || list.isEmpty()) return;
                    for (Object revokeMsgInfo : list) {
                        try {
                            onRevokeMsg(revokeMsgInfo);
                        } catch (Throwable t) {
                            log(t);
                        }
                    }
                    list.clear();
                }
            });
            inited = true;
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private void onRevokeMsg(Object revokeMsgInfo) throws Exception {
        RevokeMsgInfoImpl info = new RevokeMsgInfoImpl((Parcelable) revokeMsgInfo);
        String entityUin = info.friendUin;
        String revokerUin = info.fromUin;
        String authorUin = info.authorUin;
        int istroop = info.istroop;
        long msgUid = info.msgUid;
        long shmsgseq = info.shmsgseq;
        long time = info.time;
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
            if (authorUin == null || revokerUin.equals(authorUin)) {
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
        invoke_virtual_declared_ordinal_modifier(mQQMsgFacade, 0, 4, false, Modifier.PUBLIC, 0,
                list, Utils.getAccount(), List.class, String.class, void.class);
    }

    private Bundle createTroopMemberHighlightItem(String memberUin) {
        Bundle bundle = new Bundle();
        bundle.putInt("key_action", 5);
        bundle.putString("troop_mem_uin", memberUin);
        bundle.putBoolean("need_update_nick", true);
        return bundle;
    }

    private Object createBareHighlightGreyTip(String entityUin, int istroop, String fromUin, long time, String msg, long msgUid, long shmsgseq) throws Exception {
        int msgtype = -2030;// MessageRecord.MSG_TYPE_TROOP_GAP_GRAY_TIPS
        Object messageRecord = invoke_static_declared_ordinal(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 2, true, msgtype, int.class);
        callMethod(messageRecord, "init", Utils.getAccount(), entityUin, fromUin, msg, time, msgtype, istroop, time);
        setObjectField(messageRecord, "msgUid", msgUid);
        setObjectField(messageRecord, "shmsgseq", shmsgseq);
        setObjectField(messageRecord, "isread", true);
        return messageRecord;
    }

    private Object createBarePlainGreyTip(String entityUin, int istroop, String fromUin, long time, String msg, long msgUid, long shmsgseq) throws Exception {
        int msgtype = -2031;// MessageRecord.MSG_TYPE_REVOKE_GRAY_TIPS
        Object messageRecord = invoke_static_declared_ordinal(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 2, true, msgtype, int.class);
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
                Object troopMemberInfo = invoke_virtual_declared_ordinal(mTroopManager, 0, 3, false, troopUin, memberUin,
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
                String ret;//getDiscussionMemberShowName
                String nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS),
                        2, 10, false, Modifier.PUBLIC, 0,
                        getQQAppInterface(), troopUin, memberUin, _QQAppInterface(), String.class, String.class);
                if (nickname != null && (ret = nickname.replaceAll("\\u202E", "")).trim().length() > 0) {
                    return ret;
                }
            } catch (Throwable e) {
                log(e);
            }
        }
        try {
            String ret;//getBuddyName
            String nickname = (String) invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_CONTACT_UTILS), 1, 3, true, Modifier.PUBLIC, 0,
                    getQQAppInterface(), memberUin, true, _QQAppInterface(), String.class, boolean.class, String.class);
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
            list = (List) invoke_virtual_declared_ordinal(mQQMsgFacade, 0, 2, false,
                    uin, istroop, shmsgseq, msgUid, String.class, int.class, long.class, long.class, List.class);
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
