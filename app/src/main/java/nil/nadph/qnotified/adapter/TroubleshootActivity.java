package nil.nadph.qnotified.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.record.EventRecord;
import nil.nadph.qnotified.record.FriendRecord;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.ResUtils;
import nil.nadph.qnotified.util.Utils;

import java.util.Iterator;
import java.util.Map;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ViewBuilder.newListItemButton;
import static nil.nadph.qnotified.util.ViewBuilder.subtitle;
import static nil.nadph.qnotified.util.Utils.*;

public class TroubleshootActivity implements ActivityAdapter {
    private Activity self;

    public TroubleshootActivity(Activity activity) {
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

        ll.addView(subtitle(self, "清除与重置(不可逆)"));
        ll.addView(newListItemButton(self, "重置模块设置", "不影响历史好友信息", null, clickToReset()));
        ll.addView(newListItemButton(self, "清除[已恢复]的历史记录", "删除当前帐号下所有状态为[已恢复]的历史好友记录", null, clickToWipeDeletedFriends()));
        ll.addView(newListItemButton(self, "清除所有的历史记录", "删除当前帐号下所有的历史好友记录", null, clickToWipeAllFriends()));

        ll.addView(subtitle(self, ""));
        ll.addView(subtitle(self, "以下内容基本上都没用，它们为了修复故障才留在这里。"));

        for (int i = 1; i <= DexKit.DEOBF_NUM; i++) {
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) continue;
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                Class ccurr = DexKit.tryLoadOrNull(i);
                String currName = "null";
                if (ccurr != null) {
                    currName = ccurr.getName();
                }
                ll.addView(subtitle(self, "  [" + i + "]" + shortName + "\n" + orig + "\n->" + currName));
            } catch (Throwable e) {
                ll.addView(subtitle(self, "  [" + i + "]" + e.toString()));
            }
        }


        ll.addView(subtitle(self, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader() + "\nContext.getClassLoader()\n" + self.getClassLoader() + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()));

        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        self.setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        invoke_virtual(self, "setTitle", "故障排除", CharSequence.class);
        invoke_virtual(self, "setImmersiveStatus");
        invoke_virtual(self, "enableLeftBtn", true, boolean.class);
        ActProxyMgr.setContentBackgroundDrawable(self, ResUtils.skin_background);

    }


    public View.OnClickListener clickToWipeDeletedFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Dialog dialog = createDialog(self);
                    invoke_virtual(dialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ExfriendManager exm = ExfriendManager.getCurrent();
                                Iterator it = exm.getEvents().entrySet().iterator();
                                while (it.hasNext()) {
                                    EventRecord ev = (EventRecord) ((Map.Entry) it.next()).getValue();
                                    if (exm.getPersons().get(ev.operand).friendStatus == FriendRecord.STATUS_FRIEND_MUTUAL)
                                        it.remove();
                                }
                                exm.saveConfigure();
                                showToast(self, TOAST_TYPE_SUCCESS, "操作成功", Toast.LENGTH_SHORT);
                            } catch (Throwable e) {
                            }
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(dialog, "setNegativeButton", "取消", new Utils.DummyCallback(), String.class, DialogInterface.OnClickListener.class);
                    dialog.setCancelable(true);
                    invoke_virtual(dialog, "setMessage", "此操作将删除当前帐号(" + getLongAccountUin() + ")下的 已恢复 的历史好友记录(记录可单独删除).如果因bug大量好友被标记为已删除,请先刷新好友列表,然后再点击此按钮.\n此操作不可恢复", CharSequence.class);
                    invoke_virtual(dialog, "setTitle", "确认操作", String.class);
                    dialog.show();
                } catch (Exception e) {
                }
            }
        };
    }

    public View.OnClickListener clickToWipeAllFriends() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Dialog dialog = createDialog(self);
                    invoke_virtual(dialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ExfriendManager exm = ExfriendManager.getCurrent();
                                exm.getConfig().getFile().delete();
                                exm.getConfig().reinit();
                                exm.reinit();
                                showToast(self, TOAST_TYPE_SUCCESS, "操作成功", Toast.LENGTH_SHORT);
                            } catch (Throwable e) {
                            }
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(dialog, "setNegativeButton", "取消", new Utils.DummyCallback(), String.class, DialogInterface.OnClickListener.class);
                    dialog.setCancelable(true);
                    invoke_virtual(dialog, "setMessage", "此操作将删除当前帐号(" + getLongAccountUin() + ")下的 全部 的历史好友记录,通常您不需要进行此操作.如果您的历史好友列表中因bug出现大量好友,请在联系人列表下拉刷新后点击 删除标记为已恢复的好友 .\n此操作不可恢复", CharSequence.class);
                    invoke_virtual(dialog, "setTitle", "确认操作", String.class);
                    dialog.show();
                } catch (Exception e) {
                }
            }
        };
    }

    public View.OnClickListener clickToReset() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Dialog dialog = createDialog(self);
                    invoke_virtual(dialog, "setPositiveButton", "确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ConfigManager cfg = ConfigManager.getDefault();
                                cfg.getAllConfig().clear();
                                cfg.getFile().delete();
                                System.exit(0);
                            } catch (Throwable e) {
                            }
                        }
                    }, String.class, DialogInterface.OnClickListener.class);
                    invoke_virtual(dialog, "setNegativeButton", "取消", new Utils.DummyCallback(), String.class, DialogInterface.OnClickListener.class);
                    dialog.setCancelable(true);
                    invoke_virtual(dialog, "setMessage", "此操作将删除该模块的所有配置信息,包括屏蔽通知的群列表,但不包括历史好友列表.点击确认后请等待3秒后手动重启QQ.\n此操作不可恢复", CharSequence.class);
                    invoke_virtual(dialog, "setTitle", "确认操作", String.class);
                    dialog.show();
                } catch (Exception e) {
                }
            }
        };
    }


    @Override
    public void doOnPostResume() throws Throwable {
    }

    @Override
    public void doOnPostPause() throws Throwable {
    }

    @Override
    public void doOnPostDestory() throws Throwable {
    }

    @Override
    public void doOnPostActivityResult(int requestCode, int resultCode, Intent data) throws Throwable {
    }

    @Override
    public boolean isWrapContent() throws Throwable {
        return true;
    }
}
