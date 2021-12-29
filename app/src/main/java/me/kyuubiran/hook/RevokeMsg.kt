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
package me.kyuubiran.hook

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import me.kyuubiran.util.getDefaultCfg
import me.kyuubiran.util.logdt
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.bridge.ContactUtils
import nil.nadph.qnotified.bridge.RevokeMsgInfoImpl
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.step.DexDeobfStep
import nil.nadph.qnotified.util.DexKit
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.ReflexUtil.*
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.*

//防撤回狐狸狸版
@FunctionEntry
object RevokeMsg : CommonDelayableHook(
    "kr_revoke_msg",
    SyncUtils.PROC_MAIN or SyncUtils.PROC_MSF,
    DexDeobfStep(DexKit.C_MSG_REC_FAC),
    DexDeobfStep(DexKit.C_CONTACT_UTILS)
) {
    var mQQMsgFacade: Any? = null

    //总开关 b
    const val kr_revoke_msg = "kr_revoke_msg"

    //是否显示消息内容 b
    const val kr_revoke_msg_show_msg_text_enabled = "kr_revoke_msg_show_msg_text_enabled"

    //自定义撤回消息提示文本 str
    const val kr_revoke_msg_tips_text = "kr_revoke_msg_tips_text"

    //自定义未收到消息提示文本 str
    const val kr_revoke_unreceived_msg_tips_text = "kr_revoke_unreceived_msg_tips_text"

    override fun initOnce(): Boolean {
        return try {
            var doRevokeMsg: Method? = null
            for (m: Method in Initiator._QQMessageFacade().declaredMethods) {
                if (m.returnType == Void.TYPE) {
                    val argt = m.parameterTypes
                    if (argt.size == 2 && argt[0] == ArrayList::class.java && argt[1] == Boolean::class.java) {
                        doRevokeMsg = m
                        break
                    }
                }
            }
            XposedBridge.hookMethod(doRevokeMsg, object : XC_MethodHook(-10086) {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    mQQMsgFacade = param.thisObject
                    if (LicenseStatus.sDisableCommonHooks) return
                    if (!isEnabled) return
                    val list = param.args[0] as ArrayList<*>
                    param.result = null
                    if (list.isEmpty()) return
                    for (revokeMsgInfo in list) {
                        try {
                            onRevokeMsg(revokeMsgInfo)
                        } catch (t: Throwable) {
                            logdt(t)
                        }
                    }
                    list.clear()
                }
            })
            true
        } catch (t: Throwable) {
            logdt(t)
            false
        }
    }

    private fun onRevokeMsg(revokeMsgInfo: Any) {
        val info = RevokeMsgInfoImpl(revokeMsgInfo as Parcelable)
        val entityUin = info.friendUin
        val revokerUin = info.fromUin
        val authorUin = info.authorUin
        val istroop = info.istroop
        val msgUid = info.msgUid
        val shmsgseq = info.shmsgseq
        val time = info.time
        val selfUin = "" + Utils.getLongAccountUin()
        if (selfUin == revokerUin) {
            return
        }
        val uin = if (istroop == 0) revokerUin else entityUin
        val msgObject = getMessage(uin, istroop, shmsgseq, msgUid)
        val id = getMessageUid(msgObject)
        if (Utils.isCallingFrom(Initiator._C2CMessageProcessor().name)) return
        val isGroupChat = istroop != 0
        val newMsgUid: Long
        newMsgUid = if (msgUid != 0L) {
            msgUid + Random().nextInt()
        } else {
            0
        }
        val revokeGreyTip: Any
        if (isGroupChat) {
            if (authorUin == null || revokerUin == authorUin) {
                //自己撤回
                val revokerNick = ContactUtils.getTroopMemberNick(entityUin, revokerUin)
                var greyMsg = "\"" + revokerNick + "\u202d\""
                if (msgObject != null) {
                    greyMsg += getMsgRevokedTipsText()
                    val message = try {
                        getMessageContentStripped(msgObject)
                    } catch (e: Exception) {
                        ""
                    }
                    val msgtype = getMessageType(msgObject)
                    if (msgtype == -1000 /*text msg*/) {
                        if (!TextUtils.isEmpty(message) && isShowMsgTextEnabled()) {
                            greyMsg += ": $message"
                        }
                    }
                } else {
                    greyMsg += getUnreceivedMsgRevokedTipsText()
                }
                revokeGreyTip = createBareHighlightGreyTip(
                    entityUin,
                    istroop,
                    revokerUin,
                    time + 1,
                    greyMsg,
                    newMsgUid,
                    shmsgseq
                )
                addHightlightItem(
                    revokeGreyTip,
                    1,
                    1 + revokerNick.length,
                    createTroopMemberHighlightItem(revokerUin)
                )
            } else {
                //被权限狗撤回(含管理,群主)
                val revokerNick = ContactUtils.getTroopMemberNick(entityUin, revokerUin)
                val authorNick = ContactUtils.getTroopMemberNick(entityUin, authorUin)
                if (msgObject == null) {
                    val greyMsg = "\"$revokerNick\u202d\"撤回了\"$authorNick\u202d\"的消息(没收到)"
                    revokeGreyTip = createBareHighlightGreyTip(
                        entityUin,
                        istroop,
                        revokerUin,
                        time + 1,
                        greyMsg,
                        newMsgUid,
                        shmsgseq
                    )
                    addHightlightItem(
                        revokeGreyTip,
                        1,
                        1 + revokerNick.length,
                        createTroopMemberHighlightItem(revokerUin)
                    )
                    addHightlightItem(
                        revokeGreyTip,
                        1 + revokerNick.length + 1 + 5,
                        1 + revokerNick.length + 1 + 5 + authorNick.length,
                        createTroopMemberHighlightItem(authorUin)
                    )
                } else {
                    var greyMsg = "\"$revokerNick\u202d\"尝试撤回\"$authorNick\u202d\"的消息"
                    val message = getMessageContentStripped(msgObject)
                    val msgtype = getMessageType(msgObject)
                    if (msgtype == -1000 /*text msg*/) {
                        if (!TextUtils.isEmpty(message) && isShowMsgTextEnabled()) {
                            greyMsg += ": $message"
                        }
                    }
                    revokeGreyTip = createBareHighlightGreyTip(
                        entityUin,
                        istroop,
                        revokerUin,
                        time + 1,
                        greyMsg,
                        newMsgUid,
                        shmsgseq
                    )
                    addHightlightItem(
                        revokeGreyTip,
                        1,
                        1 + revokerNick.length,
                        createTroopMemberHighlightItem(revokerUin)
                    )
                    addHightlightItem(
                        revokeGreyTip,
                        1 + revokerNick.length + 1 + 6,
                        1 + revokerNick.length + 1 + 6 + authorNick.length,
                        createTroopMemberHighlightItem(authorUin)
                    )
                }
            }
        } else {
            var greyMsg: String
            if (msgObject == null) {
                greyMsg = "对方" + getUnreceivedMsgRevokedTipsText()
            } else {
                val message = getMessageContentStripped(msgObject)
                val msgtype = getMessageType(msgObject)
                greyMsg = "对方" + getMsgRevokedTipsText()
                if (msgtype == -1000 /*text msg*/) {
                    if (!TextUtils.isEmpty(message) && isShowMsgTextEnabled()) {
                        greyMsg += ": $message"
                    }
                }
            }
            revokeGreyTip = createBarePlainGreyTip(
                revokerUin,
                istroop,
                revokerUin,
                time + 1,
                greyMsg,
                newMsgUid,
                shmsgseq
            )
        }
        val list: MutableList<Any> = ArrayList()
        list.add(revokeGreyTip)
        invoke_virtual_declared_ordinal_modifier(
            mQQMsgFacade, 0, 4, false, Modifier.PUBLIC, 0,
            list, Utils.getAccount(), MutableList::class.java, String::class.java, Void.TYPE
        )
    }

    private fun createTroopMemberHighlightItem(memberUin: String): Bundle {
        val bundle = Bundle()
        bundle.putInt("key_action", 5)
        bundle.putString("troop_mem_uin", memberUin)
        bundle.putBoolean("need_update_nick", true)
        return bundle
    }


    private fun createBareHighlightGreyTip(
        entityUin: String,
        istroop: Int,
        fromUin: String,
        time: Long,
        msg: String,
        msgUid: Long,
        shmsgseq: Long
    ): Any {
        val msgtype = -2030 // MessageRecord.MSG_TYPE_TROOP_GAP_GRAY_TIPS
        val messageRecord = invoke_static_declared_ordinal_modifier(
            DexKit.doFindClass(DexKit.C_MSG_REC_FAC),
            0,
            1,
            true,
            Modifier.PUBLIC,
            0,
            msgtype,
            Int::class.javaPrimitiveType
        )
        XposedHelpers.callMethod(
            messageRecord,
            "init",
            Utils.getAccount(),
            entityUin,
            fromUin,
            msg,
            time,
            msgtype,
            istroop,
            time
        )
        XposedHelpers.setObjectField(messageRecord, "msgUid", msgUid)
        XposedHelpers.setObjectField(messageRecord, "shmsgseq", shmsgseq)
        XposedHelpers.setObjectField(messageRecord, "isread", true)
        return messageRecord
    }


    private fun createBarePlainGreyTip(
        entityUin: String,
        istroop: Int,
        fromUin: String,
        time: Long,
        msg: String,
        msgUid: Long,
        shmsgseq: Long
    ): Any {
        val msgtype = -2031 // MessageRecord.MSG_TYPE_REVOKE_GRAY_TIPS
        val messageRecord = invoke_static_declared_ordinal_modifier(
            DexKit.doFindClass(DexKit.C_MSG_REC_FAC),
            0,
            1,
            true,
            Modifier.PUBLIC,
            0,
            msgtype,
            Int::class.javaPrimitiveType
        )
        XposedHelpers.callMethod(
            messageRecord,
            "init",
            Utils.getAccount(),
            entityUin,
            fromUin,
            msg,
            time,
            msgtype,
            istroop,
            time
        )
        XposedHelpers.setObjectField(messageRecord, "msgUid", msgUid)
        XposedHelpers.setObjectField(messageRecord, "shmsgseq", shmsgseq)
        XposedHelpers.setObjectField(messageRecord, "isread", true)
        return messageRecord
    }

    private fun addHightlightItem(msgForGreyTip: Any, start: Int, end: Int, bundle: Bundle) {
        try {
            invoke_virtual(
                msgForGreyTip,
                "addHightlightItem",
                start,
                end,
                bundle,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Bundle::class.java
            )
        } catch (e: Exception) {
            logdt(e)
        }
    }

    private fun getMessage(uin: String, istroop: Int, shmsgseq: Long, msgUid: Long): Any? {
        var list: List<*>? = null
        try {
            list = invoke_virtual_declared_ordinal(
                mQQMsgFacade,
                0,
                2,
                false,
                uin,
                istroop,
                shmsgseq,
                msgUid,
                String::class.java,
                Int::class.javaPrimitiveType,
                Long::class.javaPrimitiveType,
                Long::class.javaPrimitiveType,
                MutableList::class.java
            ) as List<*>
        } catch (e: Exception) {
            logdt(e)
        }
        return if (list == null || list.isEmpty()) null else list[0]
    }

    private fun getMessageContentStripped(msgObject: Any): String? {
        var msg = try {
            iget_object_or_null(msgObject, "msg") as String
        } catch (e: Exception) {
            ""
        }
        msg = msg.replace('\n', ' ').replace('\r', ' ').replace("\u202E", "")
        if (msg.length > 103) msg = msg.substring(0, 100) + "..."
        return msg
    }

    private fun getMessageUid(msgObject: Any?): Long {
        return if (msgObject == null) 0 else iget_object_or_null(msgObject, "msgUid") as Long
    }

    private fun getMessageType(msgObject: Any?): Int {
        return if (msgObject == null) -1 else iget_object_or_null(msgObject, "msgtype") as Int
    }

    private fun isShowMsgTextEnabled(): Boolean {
        return try {
            getDefaultCfg().getBooleanOrFalse(kr_revoke_msg_show_msg_text_enabled)
        } catch (t: Throwable) {
            logdt(t)
            false
        }
    }

    private fun getMsgRevokedTipsText(): String {
        return try {
            getDefaultCfg().getString(kr_revoke_msg_tips_text) ?: ""
        } catch (t: Throwable) {
            logdt(t)
            ""
        }
    }

    private fun getUnreceivedMsgRevokedTipsText(): String {
        return try {
            getDefaultCfg().getString(kr_revoke_unreceived_msg_tips_text) ?: ""
        } catch (t: Throwable) {
            logdt(t)
            ""
        }
    }
}
