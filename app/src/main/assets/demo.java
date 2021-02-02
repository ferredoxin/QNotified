//@QNScriptInfoStart
//@author:          lliiooll
//@name:            示例脚本
//@version:         0.0.1
//@label:           demo
//@decs:            用于QN脚本开发的入门示例
//@QNScriptInfoEnd

public void onLoad(){
// 将会在脚本加载时调用
    QNClient.toast("脚本加载成功!");
    }
public void onFriendMessage(){
    String uin=pFM.getSender();
    QNClient.sendFriendMsg(uin,"消息处理成功!"+pFM.getMsg());
    // 将会在收到好友消息且脚本启用时调用
    // String uin=param.uin;// 发送者QQ
    // String content=param.content;// 消息内容
    // QNClient.send(uin,"消息处理完毕~\n"+content,0);// 发送文字消息
    // QNClient.send(好友/群号码,"wdnmd当场裂开来");// 发送文字消息
    // QNClient.sendImg(好友/群号码,"url或者文件");// 发送图片
    // QNClient.sendRecord(好友/群号码,"url或者文件");// 发送语音
    // QNClient.sendCard(好友/群号码,"json或者xml代码");// 发送卡片消息(只有高级白名单才可以)
    // QNClient.kick(群号码,成员号码);// 从一个群踢出一个人
    // QNClient.mute(群号码,成员号码,禁言时间[分钟]);// 在某个群禁言一个人
    // QNClient.muteAll(群号码);// 开启一个群的全群禁言
    }
