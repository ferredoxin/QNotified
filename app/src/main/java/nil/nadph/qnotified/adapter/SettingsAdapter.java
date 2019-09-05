package nil.nadph.qnotified.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.QThemeKit;
import nil.nadph.qnotified.util.Utils;

import java.io.File;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ActProxyMgr.*;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.QQViewBuilder.*;
import static nil.nadph.qnotified.util.Utils.*;

public class SettingsAdapter implements ActivityAdapter {

    private Activity self;
    private TextView __tv_muted_atall, __tv_muted_redpacket;


    public SettingsAdapter(Activity activity) {
        self = activity;
    }

    @Override
    public void doOnPostCreate(Bundle savedInstanceState) throws Throwable {
        LinearLayout ll = new LinearLayout(self);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(self);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = (ViewGroup) new_instance(load("com/tencent/mobileqq/widget/BounceScrollView"), self, null, Context.class, AttributeSet.class);
        //invoke_virtual(bounceScrollView,"a",true,500,500,boolean.class,int.class,int.class);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        bounceScrollView.setBackgroundColor(QThemeKit.qq_setting_item_bg_nor.getDefaultColor());
        //invoke_virtual(bounceScrollView,"setNeedHorizontalGesture",true,boolean.class);
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT, dip2px(self, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        int mar = (int) (dip2px(self, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        ll.addView(subtitle(self, "列表"));
        ll.addView(newListItemButton(self, "历史好友", null, null, clickToProxyActAction(ACTION_EXFRIEND_LIST)));
        ll.addView(newListItemButton(self, "导出历史好友列表", "支持json/csv/yaml格式", null, clickTheComing()));
        ll.addView(subtitle(self, "设置"));
        ll.addView(newListItemSwitchConfigNext(self, "隐藏小程序入口", "隐藏消息列表下拉出现的小程序列表", qn_hide_msg_list_miniapp, false));
        ll.addView(newListItemSwitchConfigNext(self, "隐藏分组下方入口", "隐藏分组列表最下方的历史好友按钮", qn_hide_ex_entry_group, false));
        ll.addView(newListItemSwitchConfig(self, "主动删除好友时不通知", "仅在测试模块时才可能需要关闭", qn_del_op_silence, true));
        ll.addView(newListItemButton(self, "重置模块", "会丢失所有历史好友信息", null, clickTheComing()));
        ll.addView(subtitle(self, "消息通知设置(不影响接收消息)"));
        ll.addView(_t = newListItemButton(self, "屏蔽指定群@全体成员通知", Html.fromHtml("<font color='" + get_RGB(hiColor.getDefaultColor()) + "'>[@全体成员]</font>就这点破事"), "0个群", clickToProxyActAction(ACTION_MUTE_AT_ALL)));
        __tv_muted_atall = _t.findViewById(R_ID_VALUE);
        ll.addView(_t = newListItemButton(self, "屏蔽指定群的红包通知", Html.fromHtml("<font color='" + get_RGB(hiColor.getDefaultColor()) + "'>[QQ红包][有红包][有福袋]</font>恭喜发财"), "0个群", clickToProxyActAction(ACTION_MUTE_RED_PACKET)));
        __tv_muted_redpacket = _t.findViewById(R_ID_VALUE);
        ll.addView(subtitle(self, "实验性功能(不一定有效,使用者后果自负)"));
        ll.addView(newListItemSwitchConfigNext(self, "上传透明头像", "开启后上传透明头像不会变黑", qn_enable_transparent, true));
        ll.addView(newListItemSwitchConfig(self, "发送卡片消息", "xml,json等", qn_send_card_msg, false));
        ll.addView(newListItemSwitchConfig(self, "语音转发", null, qn_enable_voice_forward, false));
        ll.addView(newListItemSwitchConfig(self, "以图片方式打开表情", null, qn_sticker_as_pic, false));
        ll.addView(newListItemButton(self, "重定向文件下载目录", new File(Environment.getExternalStorageDirectory(), "Tencent/QQfile_recv").getAbsolutePath(), "禁用", clickTheComing()));
        ll.addView(subtitle(self, "参数设定"));
        ll.addView(newListItemButton(self, "DelFriendReq.delType", "只能为1或2", "[不改动]", clickTheComing()));
        ll.addView(newListItemButton(self, "AddFriendReq.sourceID", "改错可能导致无法添加好友", "[不改动]", clickTheComing()));
        ll.addView(subtitle(self, "关于"));
        ll.addView(newListItemDummy(self, "QQ版本", null, Utils.getQQVersionName(self)));
        ll.addView(newListItemDummy(self, "模块版本", null, Utils.QN_VERSION_NAME));
        ll.addView(newListItemButton(self, "检查更新", null, "暂不开放", clickTheComing()));
        ll.addView(subtitle(self, "调试"));
        ll.addView(newListItemButton(self, "Shell.exec", "正常情况下无需使用此功能", null, clickTheComing()));
        ll.addView(subtitle(self, "作者"));
        ll.addView(newListItemButton(self, "打赏", "请选择扶贫方式", null, clickTheComing()));
        ll.addView(newListItemButton(self, "QQ", "点击私信反馈", "1041703712", clickToChat()));
        ll.addView(newListItemButton(self, "Mail", null, "xenonhydride@gmail.com", null));
        ll.addView(newListItemButton(self, "Github", "Bug -> Issue", "cinit", clickToUrl("https://github.com/cinit/QNotified")));
        ll.addView(newListItemButton(self, "Telegram", null, "Auride", clickToUrl("https://t.me/Auride")));
        ll.addView(subtitle(self, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader() + "\nContext.getClassLoader()\n" + self.getClassLoader() + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()));

        bounceScrollView.setFocusable(true);
        bounceScrollView.setFocusableInTouchMode(true);
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        //__ll.addView(bounceScrollView,_lp_fat);
        //sdlv.setBackgroundColor(0xFFAA0000)
        invoke_virtual(self, "setTitle", "高级与设置", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        //TextView rightBtn=(TextView)invoke_virtual(self,"getRightTextView");
        //log("Title:"+invoke_virtual(self,"getTextTitle"));

        //.addView(sdlv,lp);
        ConfigManager.getDefault().save();
    }

    @Override
    public void doOnPostResume() throws Throwable {
        ConfigManager cfg = ConfigManager.getDefault();
        String str = cfg.getString(qn_muted_at_all);
        int n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_atall.setText(n + "个群");
        str = cfg.getString(qn_muted_red_packet);
        n = 0;
        if (str != null && str.length() > 4) n = str.split(",").length;
        __tv_muted_redpacket.setText(n + "个群");

    }

    @Override
    public void doOnPostPause() throws Throwable {

    }

    @Override
    public void doOnPostDestory() throws Throwable {

    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
