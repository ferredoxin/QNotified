package nil.nadph.qnotified.script.params;

public class GroupMessageParam {
    /**
     * 发送者id
     */
    public long senderuin;
    /**
     * 群id
     */
    public long uin;
    /**
     * 消息内容
     */
    public String content;

    public GroupMessageParam setSenderUin(long uin) {
        this.senderuin = uin;
        return this;
    }

    public GroupMessageParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public GroupMessageParam setContent(String content) {
        this.content = content;
        return this;
    }

    public GroupMessageParam create(){
        return this;
    }
}
