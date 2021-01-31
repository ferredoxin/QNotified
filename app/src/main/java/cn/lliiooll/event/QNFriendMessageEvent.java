package cn.lliiooll.event;

import cn.lliiooll.params.BaseParams;
import cn.lliiooll.params.FriendMessageParam;
import cn.lliiooll.utils.MsgRecord;

public class QNFriendMessageEvent extends QNBaseEvent {


    private final MsgRecord record;

    public QNFriendMessageEvent(MsgRecord record) {
        super("onFriendMessage", "pFM");
        this.record = record;
    }

    public static QNFriendMessageEvent create(MsgRecord data) {
        return new QNFriendMessageEvent(data);
    }

    @Override
    public BaseParams doParse() {
        return FriendMessageParam.builder()
            .setSelfUin(record.getSelfUin())
            .setSenderUin(record.getSenderUin())
            .setMsg(record.getMsg())
            .build();
    }
}
