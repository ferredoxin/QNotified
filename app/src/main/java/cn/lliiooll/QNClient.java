package cn.lliiooll;

import android.widget.Toast;
import cn.lliiooll.hook.QNHook;
import cn.lliiooll.script.QNScript;
import cn.lliiooll.utils.QNScriptUtils;
import nil.nadph.qnotified.bridge.ChatActivityFacade;
import nil.nadph.qnotified.bridge.SessionInfoImpl;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.Date;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_INFO;
import static nil.nadph.qnotified.util.Utils.getApplication;

public class QNClient {

    /**
     * 私聊发送一条消息
     *
     * @param qq  目标qq
     * @param msg 消息
     */
    public static void sendFriendMsg(String qq, String msg) {
        send(qq, msg, 0);
    }

    /**
     * 群聊发送一条消息
     *
     * @param qq  目标qq
     * @param msg 消息
     */
    public static void sendGroupMsg(String qq, String msg) {
        send(qq, msg, 1);
    }

    /**
     * 发送一条文字消息
     *
     * @param uin     要发送的 群/好友
     * @param content 要发送的内容
     * @param type    类型，当发送给好友为0.否则为1
     */
    public static void send(String uin, String content, int type) {
        // to do
        ChatActivityFacade.sendMessage(
            Utils.getQQAppInterface(), getApplication(), SessionInfoImpl.createSessionInfo(uin, type), content
        );
    }

    /**
     * 发送一条toast
     *
     * @param msg 要显示的消息
     */
    public static void toast(String msg) {
        //Utils.showToast(getApplication(), TOAST_TYPE_INFO, msg, Toasts.LENGTH_LONG);
        //Toast.makeText(getApplication(), msg, Toast.LENGTH_LONG).show();
    }

    public void hook(Member member, QNHook hook) {
        //TODO: hook
    }
}
