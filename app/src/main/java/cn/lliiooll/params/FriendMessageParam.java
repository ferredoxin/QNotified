package cn.lliiooll.params;

public class FriendMessageParam implements BaseParams {

    private String self;
    private String sender;
    private String msg;

    public FriendMessageParam(String self, String sender, String msg) {
        this.self = self;
        this.sender = sender;
        this.msg = msg;
    }

    public String getSelf() {
        return self;
    }

    public String getSender() {
        return sender;
    }

    public String getMsg() {
        return msg;
    }

    public static FriendMessageParamBuilder builder() {
        return new FriendMessageParamBuilder();
    }

    public static class FriendMessageParamBuilder {

        private String self;
        private String sender;
        private String msg;

        public FriendMessageParamBuilder setSelfUin(String self) {
            this.self = self;
            return this;
        }

        public FriendMessageParamBuilder setSenderUin(String sender) {
            this.sender = sender;
            return this;
        }

        public FriendMessageParamBuilder setMsg(String msg) {
            this.msg = msg;
            return this;
        }

        public FriendMessageParam build() {
            return new FriendMessageParam(self, sender, msg);
        }
    }

    @Override
    public String toString() {
        return "self:" + getSelf() + " sender:" + getSender() + " msg:" + getMsg();
    }
}
