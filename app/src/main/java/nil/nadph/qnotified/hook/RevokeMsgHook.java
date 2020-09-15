/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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

import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.ContactUtils;
import nil.nadph.qnotified.bridge.RevokeMsgInfoImpl;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.setObjectField;
import static nil.nadph.qnotified.util.Initiator._C2CMessageProcessor;
import static nil.nadph.qnotified.util.Initiator._QQMessageFacade;
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
                    if (LicenseStatus.sDisableCommonHooks) return;
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
                String revokerNick = ContactUtils.getTroopMemberNick(entityUin, revokerUin);
                String greyMsg = "\"" + revokerNick + "\u202d\"";
                if (msgObject != null) {
                    greyMsg += "尝试撤回一条消息";
                    String message = getMessageContentStripped(msgObject);
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
                String revokerNick = ContactUtils.getTroopMemberNick(entityUin, revokerUin);
                String authorNick = ContactUtils.getTroopMemberNick(entityUin, authorUin);
                if (msgObject == null) {
                    String greyMsg = "\"" + revokerNick + "\u202d\"撤回了\"" + authorNick + "\u202d\"的消息(没收到)";
                    revokeGreyTip = createBareHighlightGreyTip(entityUin, istroop, revokerUin, time + 1, greyMsg, newMsgUid, shmsgseq);
                    addHightlightItem(revokeGreyTip, 1, 1 + revokerNick.length(), createTroopMemberHighlightItem(revokerUin));
                    addHightlightItem(revokeGreyTip, 1 + revokerNick.length() + 1 + 5, 1 + revokerNick.length() + 1 + 5 + authorNick.length(), createTroopMemberHighlightItem(authorUin));
                } else {
                    String greyMsg = "\"" + revokerNick + "\u202d\"尝试撤回\"" + authorNick + "\u202d\"的消息";
                    String message = getMessageContentStripped(msgObject);
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
                String message = getMessageContentStripped(msgObject);
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
        Object messageRecord = invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 1, true, Modifier.PUBLIC, 0, msgtype, int.class);
        callMethod(messageRecord, "init", Utils.getAccount(), entityUin, fromUin, msg, time, msgtype, istroop, time);
        setObjectField(messageRecord, "msgUid", msgUid);
        setObjectField(messageRecord, "shmsgseq", shmsgseq);
        setObjectField(messageRecord, "isread", true);
        return messageRecord;
    }

    private Object createBarePlainGreyTip(String entityUin, int istroop, String fromUin, long time, String msg, long msgUid, long shmsgseq) throws Exception {
        int msgtype = -2031;// MessageRecord.MSG_TYPE_REVOKE_GRAY_TIPS
        Object messageRecord = invoke_static_declared_ordinal_modifier(DexKit.doFindClass(DexKit.C_MSG_REC_FAC), 0, 1, true, Modifier.PUBLIC, 0, msgtype, int.class);
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

    private String getMessageContentStripped(Object msgObject) {
        String msg = (String) iget_object_or_null(msgObject, "msg");
        if (msg != null) {
            msg = msg.replace('\n', ' ').replace('\r', ' ').replace("\u202E", "");
            if (msg.length() > 103) msg = msg.substring(0, 100) + "...";
        }
        return msg;
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
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_MSG_REC_FAC), new DexDeobfStep(DexKit.C_CONTACT_UTILS)};
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
