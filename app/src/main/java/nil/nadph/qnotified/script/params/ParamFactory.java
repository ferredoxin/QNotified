package nil.nadph.qnotified.script.params;

public class ParamFactory {

    public static FriendMessageParam friendMessage() {
        return new FriendMessageParam();
    }

    public static FriendRequestParam friendRequest() {
        return new FriendRequestParam();
    }

    public static FriendAddedParam friendAdded() {
        return new FriendAddedParam();
    }

    public static GroupMessageParam groupMessage() {
        return new GroupMessageParam();
    }

    public static GroupRequestParam groupRequest() {
        return new GroupRequestParam();
    }

    public static GroupJoinedParam groupJoined() {
        return new GroupJoinedParam();
    }

}
