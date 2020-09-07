package me.kyuubiran.hook.testhook

import android.os.Looper
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import me.kyuubiran.utils.getObjectOrNull
import me.kyuubiran.utils.logd
import me.singleneuron.data.MsgRecordData
import nil.nadph.qnotified.SyncUtils
import nil.nadph.qnotified.config.ConfigManager
import nil.nadph.qnotified.hook.BaseDelayableHook
import nil.nadph.qnotified.step.Step
import nil.nadph.qnotified.util.Initiator
import nil.nadph.qnotified.util.LicenseStatus
import nil.nadph.qnotified.util.Utils
import java.lang.reflect.Method

//截取消息
object CutMessage : BaseDelayableHook() {
    private const val kr_test_cut_message: String = "kr_test_cut_message"
    var isInit = false

    override fun getPreconditions(): Array<Step?> {
        return arrayOfNulls(0)
    }

    override fun init(): Boolean {
        if (isInited) return true
        return try {
            val QQMessageFacade = Initiator.load("com.tencent.imcore.message.QQMessageFacade")
            val Msg = Initiator.load("com.tencent.imcore.message.QQMessageFacade\$Message")
            for (m: Method in QQMessageFacade.declaredMethods) {
                val argt = m.parameterTypes
                if (m.name == "a" && argt.size == 1 && argt[0] != Msg::class.java) {
                    XposedBridge.hookMethod(m, object : XC_MethodHook() {
                        override fun beforeHookedMethod(param: MethodHookParam?) {
                            if (LicenseStatus.sDisableCommonHooks) return
                            if (!isEnabled) return
                            val msgRecord = param?.args?.get(0)?: return
                            if (msgRecord::class.java.name!="com.tencent.imcore.message.QQMessageFacade${'$'}Message") return
                            val msgRecordData = MsgRecordData(msgRecord)
                            try {
                                logd("收到一份消息: \n$msgRecordData")
                                //logd(msgRecord::class.java.name)
                                /*val msg = getMsg(msgRecord)
                                val senderUin = getSenderUin(msgRecord)
                                val msgType = getMsgType(msgRecord)
                                val friendUin = getFriendUin(msgRecord)
                                val selfUin = getSelfUin(msgRecord)
                                val time = getTime(msgRecord)
                                logd("收到一份来自${senderUin}的消息:\n${msg}\n消息类型是${msgType}\n好友QQ是${friendUin}\n自己QQ是${selfUin}\n时间戳${time}")*/
                            } catch (t: Throwable) {
                                //log(t)
                            }
                        }
                    })
                }
            }
            isInit = true
            true
        } catch (t: Throwable) {
            Utils.log(t)
            false
        }
    }

    override fun isEnabled(): Boolean {
        return try {
            ConfigManager.getDefaultConfig().getBooleanOrFalse(kr_test_cut_message)
        } catch (e: java.lang.Exception) {
            Utils.log(e)
            false
        }
    }

    override fun getEffectiveProc(): Int {
        return SyncUtils.PROC_MAIN
    }

    override fun setEnabled(enabled: Boolean) {
        try {
            val mgr = ConfigManager.getDefaultConfig()
            mgr.allConfig[kr_test_cut_message] = enabled
            mgr.save()
        } catch (e: Exception) {
            Utils.log(e)
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(Utils.getApplication(), Utils.TOAST_TYPE_ERROR, e.toString() + "", Toast.LENGTH_SHORT)
            } else {
                SyncUtils.post { Utils.showToast(Utils.getApplication(), Utils.TOAST_TYPE_ERROR, e.toString() + "", Toast.LENGTH_SHORT) }
            }
        }
    }

    override fun isInited(): Boolean {
        return isInit
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