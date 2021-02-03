package cn.lliiooll.utils;

import de.robv.android.xposed.XposedHelpers;

public class MsgRecord {
    private final Object record;
    private final Class<?> clazz;
    
    public MsgRecord(Object msgRecord) {
        record = msgRecord;
        clazz = record.getClass();
    }
    
    /**
     * @return 是否群组
     */
    public boolean isTroop() {
        return XposedHelpers.getIntField(record, "istroop") == 1;
    }
    
    /**
     * @return 是否已读
     */
    public boolean isRead() {
        return XposedHelpers.getBooleanField(record, "isread");
    }
    
    /**
     * 设置消息已读
     */
    public void setRead(boolean read) {
        XposedHelpers.setBooleanField(record, "isread", read);
    }
    
    /**
     * 不要使用，暂定
     *
     * @return 是否发送
     */
    @Deprecated
    public boolean isSend() {
        return true;
        //return XposedHelpers.getIntField(record, "issend") == 1;
    }
    
    /**
     * {@link MsgType}
     *
     * @return 消息类型
     */
    public int getMsgType() {
        return XposedHelpers.getIntField(record, "msgtype");
    }
    
    
    /**
     * @return 消息内容
     */
    public String getMsg() {
        return (String) XposedHelpers.getObjectField(record, "msg");
    }
    
    /**
     * @return 同getMsg
     */
    public String getMsg2() {
        return (String) XposedHelpers.getObjectField(record, "msg2");
    }
    
    /**
     * @return 消息id
     */
    public long getMsgId() {
        return XposedHelpers.getLongField(record, "msgId");
    }
    
    /**
     * @return 消息uid
     */
    public long getMsgUid() {
        return XposedHelpers.getLongField(record, "msgUid");
    }
    
    /**
     * @return 消息seq
     */
    public long getMsgSeq() {
        return XposedHelpers.getLongField(record, "msgseq");
    }
    
    /**
     * @return 消息 "产生" 时间
     */
    public long getMsgSendTime() {
        return XposedHelpers.getLongField(record, "time");
    }
    
    /**
     * @return 当前登录qq账号
     */
    public String getSelfUin() {
        return (String) XposedHelpers.getObjectField(record, "selfuin");
    }
    
    /**
     * @return 消息发送者qq账号
     */
    public String getSenderUin() {
        return (String) XposedHelpers.getObjectField(record, "senderuin");
    }
    
    /**
     * @return 当 {@link MsgRecord#isTroop()} 为true,此处返回群号;否则为{@link MsgRecord#getSenderUin()} ()}
     */
    public String getFriendUin() {
        return (String) XposedHelpers.getObjectField(record, "frienduin");
    }
    
    /**
     * @return 消息是否为xml消息
     */
    public boolean isArkApp() {
        return getMsgType() == MsgType.MSG_TYPE_ARK_APP
            || getMsgType() == MsgType.MSG_TYPE_ARK_BABYQ_REPLY
            || getMsgType() == MsgType.MSG_TYPE_ARK_SDK_SHARE;
    }
    
    /**
     * @return 消息是否为json消息
     */
    public boolean isStruct() {
        return getMsgType() == MsgType.MSG_TYPE_STRUCT_LONG_TEXT
            || getMsgType() == MsgType.MSG_TYPE_STRUCT_MSG
            || getMsgType() == MsgType.MSG_TYPE_STRUCT_TROOP_NOTIFICATION;
    }
    
    /**
     * @return 消息是否为卡片消息
     */
    public boolean isCard() {
        return isArkApp() || isStruct();
    }
    
    /**
     * @return 消息是否为QQ钱包类消息，像红包这样的
     */
    public boolean isWallet() {
        return getMsgType() == MsgType.MSG_TYPE_QQWALLET_MSG;
    }
    
    public boolean isTroopAtAll() {
        return getMsg().contains("@全体成员 ");
    }
}
