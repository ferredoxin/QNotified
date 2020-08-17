package nil.nadph.qnotified.script.params;

public class FriendMessageParam {
    /**
     * 好友id
     */
    public long uin;
    /**
     * 消息内容
     */
    public String content;

    public FriendMessageParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public FriendMessageParam setContent(String content) {
        this.content = content;
        return this;
    }

    public FriendMessageParam create(){
        return this;
    }
}
