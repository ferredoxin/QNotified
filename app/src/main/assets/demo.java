import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.script.QNClient;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
// 示例java
        String name="示例脚本";// 脚本名称
        String label="demo";// 脚本标签
        String version="0.0.1";// 脚本版本
        String author="lliiooll";// 脚本作者
        String decs="用于QN脚本开发的入门示例";// 脚本简介

public void onLoad(){
// 将会在脚本加载时调用，在这里注册hook
        BaseDelayableHook.registerHook(ExampleHook.get());
}
public void onEnable(){
// 将会在脚本启用时调用
}
public void onDisable(){
// 将会在脚本禁用时调用
}
public void onGroupMessage(Object param){
// 将会在收到群消息且脚本启用时调用
        long senderuin=param.senderuin;// 发送者QQ
        long uin=param.uin;// 群号
        String content=param.content;// 消息内容
}
public void onFriendMessage(Object param){
// 将会在收到好友消息且脚本启用时调用
        long uin=param.uin;// 发送者QQ
        String content=param.content;// 消息内容

        // QNClient.send(好友/群号码,"wdnmd当场裂开来");// 发送文字消息
        // QNClient.sendImg(好友/群号码,"url或者文件");// 发送图片
        // QNClient.sendRecord(好友/群号码,"url或者文件");// 发送语音
        // QNClient.sendCard(好友/群号码,"json或者xml代码");// 发送卡片消息(只有高级白名单才可以)
        // QNClient.kick(群号码,成员号码);// 从一个群踢出一个人
        // QNClient.mute(群号码,成员号码,禁言时间[分钟]);// 在某个群禁言一个人
        // QNClient.muteAll(群号码);// 开启一个群的全群禁言
}
public void onFriendRequest(Object param){
// 将会在收到好友请求且脚本启用时调用
        long uin=param.uin;// 发送者QQ
        String content=param.content;// 验证内容
        param.accept();// 同意
        param.refuse();// 拒绝
}
public void onFriendAdded(Object param){
// 将会在好友添加完毕且脚本启用时调用
        long uin=param.uin;// 好友QQ
}
public void onGroupRequest(Object param){
// 将会在收到加群请求且目前qq为管理员且脚本启用时调用
        long senderuin=param.senderuin;// 发送者QQ
        long uin=param.uin;// 群号
        String content=param.content;// 验证内容
        param.accept();// 同意入群
        param.refuse();// 拒绝入群
}
public void onGroupJoined(Object param){
// 将会在有人入群且脚本启用时调用
        long senderuin=param.senderuin;// 入群QQ
        long uin=param.uin;// 群号
}

/**
 * 示例hook
 */
public static class ExampleHook extends BaseDelayableHook {

    public static ExampleHook get() {
        return new ExampleHook();
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return false;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }
}