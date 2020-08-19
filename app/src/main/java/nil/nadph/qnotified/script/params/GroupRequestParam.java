package nil.nadph.qnotified.script.params;

public class GroupRequestParam {
    /**
     * 请求id
     */
    public long senderuin;
    /**
     * 群id
     */
    public long uin;
    /**
     * 验证消息
     */
    public String content;

    public GroupRequestParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public GroupRequestParam setSenderUin(long uin) {
        this.senderuin = uin;
        return this;
    }

    public GroupRequestParam setContent(String content) {
        this.content = content;
        return this;
    }

    public GroupRequestParam create() {
        return this;
    }

    /**
     * 接受请求
     */
    public void accept() {
        // to do
    }

    /**
     * 拒绝请求
     */
    public void refuse() {
        // to do
    }
}
