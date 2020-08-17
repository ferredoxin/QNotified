package nil.nadph.qnotified.script.params;

public class GroupJoinedParam {
    /**
     * 群id
     */
    public long uin;
    /**
     * 群员id
     */
    public long senderuin;

    public GroupJoinedParam setUin(long uin) {
        this.uin = uin;
        return this;
    }

    public GroupJoinedParam setSenderUin(long uin) {
        this.senderuin = uin;
        return this;
    }

    public GroupJoinedParam create() {
        return this;
    }
}
