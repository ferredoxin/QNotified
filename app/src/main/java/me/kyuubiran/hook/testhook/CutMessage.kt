/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
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
package me.kyuubiran.hook.testhook

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.util.LOG_TYPE_FIND_METHOD
import me.kyuubiran.util.getObjectOrNull
import me.kyuubiran.util.logd
import me.kyuubiran.util.logdt
import me.singleneuron.qn_kernel.data.MsgRecordData
import nil.nadph.qnotified.base.annotation.FunctionEntry
import nil.nadph.qnotified.hook.CommonDelayableHook
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//截取消息
@FunctionEntry
object CutMessage : CommonDelayableHook("kr_test_cut_message") {

    override fun initOnce() = try {
        val QQMessageFacade = Initiator._QQMessageFacade()
        val Msg = Initiator.load("${QQMessageFacade.name}\$Message")
        for (m: Method in QQMessageFacade.declaredMethods) {
            val argt = m.parameterTypes
            if (m.name == "a" && argt.size == 1 && argt[0] != Msg::class.java) {
                XposedBridge.hookMethod(m, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        if (LicenseStatus.sDisableCommonHooks) return
                        if (!isEnabled) return
                        val msgRecord = param?.args?.get(0) ?: return
                        if (msgRecord::class.java.name != "com.tencent.imcore.message.QQMessageFacade${'$'}Message") return
                        val msgRecordData = MsgRecordData(msgRecord)
                        try {
                            logd(LOG_TYPE_FIND_METHOD, "->$m")
                            logd("收到一份消息: \n$msgRecordData")
                        } catch (t: Throwable) {
                            logdt(t)
                        }
                    }
                })
            }
        }
        true
    } catch (t: Throwable) {
        Utils.log(t)
        false
    }

    //消息文本
    fun getMsg(msgRecord: Any): String {
        return getObjectOrNull(msgRecord, "msg", String::class.java) as String
    }

    //也是消息文本
    fun getMsg2(msgRecord: Any): String {
        return getObjectOrNull(msgRecord, "msg2", String::class.java) as String
    }

    //消息id
    //@Deprecated
    fun getMsgId(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "msgId", Long::class.java) as Long
    }

    //消息uid
    fun getMsgUid(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "msgUid", Long::class.java) as Long
    }

    //好友QQ [当为群聊聊天时 则为QQ群号]
    fun getFriendUin(msgRecord: Any): String {
        return getObjectOrNull(msgRecord, "frienduin", String::class.java) as String
    }

    //发送人QQ
    fun getSenderUin(msgRecord: Any): String {
        return getObjectOrNull(msgRecord, "senderuin", String::class.java) as String
    }

    //自己QQ
    fun getSelfUin(msgRecord: Any): String {
        return getObjectOrNull(msgRecord, "selfuin", String::class.java) as String
    }

    //消息类型
    fun getMsgType(msgRecord: Any): Int {
        return getObjectOrNull(msgRecord, "msgtype", Int::class.java) as Int
    }

    //额外flag
    fun getExtraFlag(msgRecord: Any): Int {
        return getObjectOrNull(msgRecord, "extraflag", Int::class.java) as Int
    }

    //时间戳
    fun getTime(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "time", Long::class.java) as Long
    }

    //是否已读
    fun getIsRead(msgRecord: Any): Boolean {
        return getObjectOrNull(msgRecord, "isread", Boolean::class.java) as Boolean
    }

    //是否发送
    fun getIsSend(msgRecord: Any): Int {
        return getObjectOrNull(msgRecord, "issend", Boolean::class.java) as Int
    }

    //是否群组
    fun getIsTroop(msgRecord: Any): Int {
        return getObjectOrNull(msgRecord, "istroop", Boolean::class.java) as Int
    }

    //未知
    fun getMsgSeq(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "msgseq", Long::class.java) as Long
    }

    //未知
    fun getShMsgSeq(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "shmsgseq", Long::class.java) as Long
    }

    //未知
    fun getUinSeq(msgRecord: Any): Long {
        return getObjectOrNull(msgRecord, "uinseq", Long::class.java) as Long
    }

    //消息data
    fun getMsgData(msgRecord: Any): ByteArray {
        return getObjectOrNull(msgRecord, "msgData", ByteArray::class.java) as ByteArray
    }
}

/*
消息类型说明:
MSG_TYPE_TEXT = -1000                       文本消息
MSG_TYPE_TEXT_VIDEO = -1001                 小视频
MSG_TYPE_TROOP_TIPS_ADD_MEMBER = -1012      加群消息
MSG_TYPE_MIX = -1035                        混合消息[比如同时包含图片和文本]
MSG_TYPE_REPLY_TEXT = -1049                 回复消息
MSG_TYPE_MEDIA_PIC = -2000                  图片消息
MSG_TYPE_MEDIA_PTT = -2002                  语音消息
MSG_TYPE_MEDIA_FILE = -2005                 文件
MSG_TYPE_MEDIA_MARKFACE = -2007             表情消息[并非"我的收藏" 而是从QQ表情商店下载的表情]
MSG_TYPE_MEDIA_VIDEO = -2009                QQ语音/视频通话
MSG_TYPE_STRUCT_MSG = -2011                 卡片消息[分享/签到/转发消息等]
MSG_TYPE_ARK_APP = -5008                    小程序分享消息
MSG_TYPE_POKE_MSG = -5012                   戳一戳
MSG_TYPE_POKE_EMO_MSG = -5018               另类戳一戳
 */
