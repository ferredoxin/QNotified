package me.singleneuron.data

import me.kyuubiran.utils.getObjectOrNull
import me.kyuubiran.utils.putObject
import java.text.SimpleDateFormat
import java.util.*

data class MsgRecordData(val msgRecord: Any?) {

    companion object {
        const val MSG_TYPE_TEXT = -1000                       //文本消息
        const val MSG_TYPE_TEXT_VIDEO = -1001                 //小视频
        const val MSG_TYPE_TROOP_TIPS_ADD_MEMBER = -1012      //加群消息
        const val MSG_TYPE_MIX = -1035                        //混合消息[比如同时包含图片和文本]
        const val MSG_TYPE_REPLY_TEXT = -1049                 //回复消息
        const val MSG_TYPE_MEDIA_PIC = -2000                  //图片消息
        const val MSG_TYPE_MEDIA_PTT = -2002                  //语音消息
        const val MSG_TYPE_MEDIA_FILE = -2005                 //文件
        const val MSG_TYPE_MEDIA_MARKFACE = -2007             //表情消息[并非"我的收藏" 而是从QQ表情商店下载的表情]
        const val MSG_TYPE_MEDIA_VIDEO = -2009                //QQ语音/视频通话
        const val MSG_TYPE_STRUCT_MSG = -2011                 //卡片消息[分享/签到/转发消息等]
        const val MSG_TYPE_ARK_APP = -5008                    //小程序分享消息
        const val MSG_TYPE_POKE_MSG = -5012                   //戳一戳
        const val MSG_TYPE_POKE_EMO_MSG = -5018               //另类戳一戳
        val MSG_TYPE_MAP = mapOf(
                MSG_TYPE_TEXT to "文本消息",
                MSG_TYPE_TEXT_VIDEO to "小视频",
                MSG_TYPE_TROOP_TIPS_ADD_MEMBER to "加群消息",
                MSG_TYPE_MIX to "混合消息[比如同时包含图片和文本]",
                MSG_TYPE_REPLY_TEXT to "回复消息",
                MSG_TYPE_MEDIA_PIC to "图片消息",
                MSG_TYPE_MEDIA_PTT to "语音消息",
                MSG_TYPE_MEDIA_FILE to "文件",
                MSG_TYPE_MEDIA_MARKFACE to "表情消息[并非\"我的收藏\" 而是从QQ表情商店下载的表情]",
                MSG_TYPE_MEDIA_VIDEO to "QQ语音/视频通话",
                MSG_TYPE_STRUCT_MSG to "卡片消息[分享/签到/转发消息等]",
                MSG_TYPE_ARK_APP to "小程序分享消息",
                MSG_TYPE_POKE_MSG to "戳一戳",
                MSG_TYPE_POKE_EMO_MSG to "另类戳一戳"
        )
    }

    //消息文本
    val msg: String
        @Throws(NullPointerException::class)
        get() = getObjectOrNull(msgRecord, "msg", String::class.java) as String

    //也是消息文本
    val msg2: String?
        get() = getObjectOrNull(msgRecord, "msg2", String::class.java) as String?

    //消息id
    //@Deprecated
    val msgId: Long?
        get() = getObjectOrNull(msgRecord, "msgId", Long::class.java) as Long?

    //消息uid
    val msgUid: Long?
        get() = getObjectOrNull(msgRecord, "msgUid", Long::class.java) as Long?

    //好友QQ [当为群聊聊天时 则为QQ群号]
    val friendUin: String?
        get() = getObjectOrNull(msgRecord, "frienduin", String::class.java) as String?

    //发送人QQ
    val senderUin: String?
        get() = getObjectOrNull(msgRecord, "senderuin", String::class.java) as String?

    //自己QQ
    val selfUin: String?
        get() = getObjectOrNull(msgRecord, "selfuin", String::class.java) as String?

    //消息类型
    val msgType: Int?
        get() = getObjectOrNull(msgRecord, "msgtype", Int::class.java) as Int?
    val readableMsgType: String?
        get() = msgType?.let { MSG_TYPE_MAP[it] }

    //额外flag
    val extraFlag: Int?
        get() = getObjectOrNull(msgRecord, "extraflag", Int::class.java) as Int?

    //时间戳
    val time: Long?
        get() = getObjectOrNull(msgRecord, "time", Long::class.java) as Long?
    val readableTime: String?
        get() = time?.let {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(it*1000))
        }

    //是否已读
    val isRead: Boolean?
        get() =  getObjectOrNull(msgRecord, "isread", Boolean::class.java) as Boolean?

    //是否发送
    val isSend: Int?
        get() =  getObjectOrNull(msgRecord, "issend", Boolean::class.java) as Int?

    //是否群组
    val isTroop: Int?
        get() = getObjectOrNull(msgRecord, "istroop", Boolean::class.java) as Int?

    //未知
    val msgSeq: Long?
        get() =  getObjectOrNull(msgRecord, "msgseq", Long::class.java) as Long?

    //未知
    val shMsgSeq: Long?
        get() =  getObjectOrNull(msgRecord, "shmsgseq", Long::class.java) as Long?

    //未知
    val uinSeq: Long?
        get() =  getObjectOrNull(msgRecord, "uinseq", Long::class.java) as Long?

    //消息data
    val msgData: ByteArray?
        get() =  getObjectOrNull(msgRecord, "msgData", ByteArray::class.java) as ByteArray?

    fun <T> get(fieldName: String): T {
        return getObjectOrNull(msgRecord, "msgData", ByteArray::class.java) as T
    }

    fun <T> set(fieldName: String, value:T)where T:Any {
        putObject(msgRecord,fieldName,value)
    }

    @Throws(NullPointerException::class)
    override fun toString(): String {
        val stringBuilder = StringBuilder()
        stringBuilder.apply {
            append("消息文本: $msg\n")
            msg2?.let{append("也是消息文本: $msg2\n")}
            msgId?.let{append("消息id: $msgId\n")}
            msgUid?.let{append("消息uid: $msgUid\n")}
            friendUin?.let{append("好友QQ [当为群聊聊天时 则为QQ群号]: $friendUin\n")}
            senderUin?.let{append("发送人QQ: $senderUin\n")}
            selfUin?.let{append("自己QQ: $selfUin\n")}
            msgType?.let{append("消息类型: $readableMsgType\n")}
            extraFlag?.let{append("额外flag: ${extraFlag?.toString(16)}\n")}
            readableTime?.let{append("时间戳: $readableTime\n")}
            isRead?.let{append("是否已读: $isRead\n")}
            isSend?.let{append("是否发送: $isSend\n")}
            isTroop?.let{append("是否群组: $isTroop\n")}
            msgSeq?.let{append("msgSeq：$msgSeq\n")}
            shMsgSeq?.let{append("shMsgSeq: $shMsgSeq\n")}
            uinSeq?.let{append("uinSeq: $uinSeq\n")}
        }
        return stringBuilder.toString()
    }

}