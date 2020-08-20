package nil.nadph.qnotified.script.params;

public class ParamFactory {

    /**
     * 构建一个好友消息参数对象
     *
     * @return {@link FriendMessageParam}
     */
    public static FriendMessageParam friendMessage() {
        return new FriendMessageParam();
    }

    /**
     * 构建一个好友请求参数对象
     *
     * @return {@link FriendRequestParam}
     */
    public static FriendRequestParam friendRequest() {
        return new FriendRequestParam();
    }

    /**
     * 构建一个好友添加完毕参数对象
     *
     * @return {@link FriendAddedParam}
     */
    public static FriendAddedParam friendAdded() {
        return new FriendAddedParam();
    }

    /**
     * 构建一个群消息参数对象
     *
     * @return {@link GroupMessageParam}
     */
    public static GroupMessageParam groupMessage() {
        return new GroupMessageParam();
    }

    /**
     * 构建一个入群请求参数对象
     *
     * @return {@link GroupRequestParam}
     */
    public static GroupRequestParam groupRequest() {
        return new GroupRequestParam();
    }

    /**
     * 构建一个成员入群参数对象
     *
     * @return {@link GroupJoinedParam}
     */
    public static GroupJoinedParam groupJoined() {
        return new GroupJoinedParam();
    }

}
