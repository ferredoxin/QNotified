package nil.nadph.qnotified.script.params;

public class FriendRequestParam {
    /**
     * 好友id
     */
    public long uin;
    /**
     * 验证消息
     */
    public String content;

    public FriendRequestParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public FriendRequestParam setContent(String content) {
        this.content = content;
        return this;
    }

    public FriendRequestParam create() {
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
