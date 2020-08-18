package nil.nadph.qnotified.script.params;

public class FriendAddedParam {
    /**
     * 好友id
     */
    public long uin;

    public FriendAddedParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public FriendAddedParam create() {
        return this;
    }
}
