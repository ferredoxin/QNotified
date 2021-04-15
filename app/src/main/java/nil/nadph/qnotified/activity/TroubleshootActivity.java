/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package nil.nadph.qnotified.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.os.MessageQueue;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tencent.mobileqq.widget.BounceScrollView;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import cc.ioctl.activity.ExfriendListActivity;
import cc.ioctl.activity.MmkvTestActivity;
import cc.ioctl.hook.InspectMessage;
import ltd.nextalone.hook.EnableQLog;
import me.singleneuron.activity.BugReportActivity;
import me.singleneuron.activity.DatabaseTestActivity;
import me.singleneuron.data.CardMsgCheckResult;
import me.singleneuron.hook.DebugDump;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import me.singleneuron.util.KotlinUtilsKt;
import nil.nadph.qnotified.BuildConfig;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.config.EventRecord;
import nil.nadph.qnotified.config.FriendRecord;
import nil.nadph.qnotified.lifecycle.ActProxyMgr;
import nil.nadph.qnotified.lifecycle.Parasitics;
import nil.nadph.qnotified.ui.CustomDialog;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.ui.ViewBuilder.*;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.iput_object;
import static nil.nadph.qnotified.util.Utils.*;

@SuppressLint("Registered")
public class TroubleshootActivity extends IphoneTitleBarActivityCompat {

    public static void quitLooper() throws Exception {
        Looper looper = Looper.getMainLooper();
        MessageQueue queue = (MessageQueue) iget_object_or_null(looper, "mQueue");
        iput_object(queue, "mQuitAllowed", true);
        looper.quit();
    }

    @Override
    public boolean doOnCreate(Bundle savedInstanceState) {
        super.doOnCreate(savedInstanceState);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup.LayoutParams mmlp = new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        LinearLayout __ll = new LinearLayout(this);
        __ll.setOrientation(LinearLayout.VERTICAL);
        ViewGroup bounceScrollView = new BounceScrollView(this, null);
        bounceScrollView.setLayoutParams(mmlp);
        bounceScrollView.addView(ll, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        LinearLayout.LayoutParams fixlp = new LinearLayout.LayoutParams(MATCH_PARENT,
            dip2px(this, 48));
        RelativeLayout.LayoutParams __lp_l = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        int mar = (int) (dip2px(this, 12) + 0.5f);
        __lp_l.setMargins(mar, 0, mar, 0);
        __lp_l.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        __lp_l.addRule(RelativeLayout.CENTER_VERTICAL);
        RelativeLayout.LayoutParams __lp_r = new RelativeLayout.LayoutParams(WRAP_CONTENT,
            WRAP_CONTENT);
        __lp_r.setMargins(mar, 0, mar, 0);
        __lp_r.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        __lp_r.addRule(RelativeLayout.CENTER_VERTICAL);
        ColorStateList hiColor = ColorStateList.valueOf(Color.argb(255, 242, 140, 72));
        RelativeLayout _t;
        ll.addView(subtitle(this, "若模块更新后仍有问题或bug请点击[清除缓存]可尝试修复问题"));
        ll.addView(newListItemButton(this, "清除缓存", "清除模块缓存并重新计算适配数据", null, clickToCleanCache()));
        ll.addView(subtitle(this, "清除与重置(不可逆)"));
        ll.addView(newListItemButton(this, "重置模块设置", "不影响历史好友信息", null, clickToReset()));
        ll.addView(newListItemButton(this, "清除[已恢复]的历史记录", "删除当前帐号下所有状态为[已恢复]的历史好友记录", null,
            clickToWipeDeletedFriends()));
        ll.addView(newListItemButton(this, "清除所有的历史记录", "删除当前帐号下所有的历史好友记录", null,
            clickToWipeAllFriends()));
        ll.addView(
            newListItemButton(this, "刷新黑白名单状态", "这个按钮没啥用", null, clickToRefreshUserStatus()));
        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "反馈"));
        ll.addView(newListItemButton(this, "提交BUG反馈", null, null,
            clickToProxyActAction(BugReportActivity.class)));
        ll.addView(subtitle(this, ""));
        ll.addView(subtitle(this, "以下内容基本上都没用，它们为了修复故障才留在这里。"));
        ll.addView(subtitle(this, "测试"));
        ll.addView(newListItemHookSwitchInit(this, DebugDump.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "检查消息", null, InspectMessage.INSTANCE));
        ll.addView(newListItemHookSwitchInit(this, "开启QQ日志", "前缀NAdump", EnableQLog.INSTANCE));
        ll.addView(newListItemButton(this, "强制重新生成日志历史记录", null, null, new View.OnClickListener() {
            final String LAST_TRACE_HASHCODE_CONFIG = "lastTraceHashcode";
            final String LAST_TRACE_DATA_CONFIG = "lastTraceDate";

            @Override
            public void onClick(View v) {
                try {
                    ConfigManager configManager = ConfigManager.getDefaultConfig();
                    configManager.remove(LAST_TRACE_DATA_CONFIG);
                    configManager.remove(LAST_TRACE_HASHCODE_CONFIG);
                    configManager.save();
                } catch (Exception e) {
                    Utils.runOnUiThread(() -> Toast
                        .makeText(HostInformationProviderKt.getHostInfo().getApplication(),
                            e.toString(), Toast.LENGTH_LONG).show());
                    Utils.log(e);
                }
            }
        }));
        ll.addView(newListItemButton(this, "测试卡片黑名单", null, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                    TroubleshootActivity.this, R.style.MaterialDialog);
                EditText editText = new EditText(TroubleshootActivity.this);
                editText.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(editText)
                    .setPositiveButton("确定", (dialog, which) -> new Thread(() -> {
                        String msg = editText.getText().toString();
                        CardMsgCheckResult result = KotlinUtilsKt.checkCardMsg(msg);
                        Utils.runOnUiThread(() -> Toast
                            .makeText(TroubleshootActivity.this, result.toString(),
                                Toast.LENGTH_LONG).show());
                    }).start()).create().show();
            }
        }));
        ll.addView(
            newListItemButton(this, "打开X5调试页面", "内置浏览器调试页面", null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Class<?> browser = Class
                            .forName("com.tencent.mobileqq.activity.QQBrowserDelegationActivity");
                        Intent intent = new Intent(TroubleshootActivity.this, browser);
                        intent.putExtra("fling_action_key", 2);
                        intent.putExtra("fling_code_key", TroubleshootActivity.this.hashCode());
                        intent.putExtra("useDefBackText", true);
                        intent.putExtra("param_force_internal_browser", true);
                        intent.putExtra("url", "http://debugx5.qq.com/");
                        startActivity(intent);
                    } catch (Throwable e) {
                        Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            }));
        if (Initiator.load("com.tencent.mobileqq.debug.DebugActivity") != null) {
            ll.addView(
                newListItemButton(this, "打开 DebugActivity", null, null, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Class<?> browser = Class
                                .forName("com.tencent.mobileqq.debug.DebugActivity");
                            Intent intent = new Intent(TroubleshootActivity.this, browser);
                            startActivity(intent);
                        } catch (Throwable e) {
                            Toast.makeText(TroubleshootActivity.this, e.toString(),
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                }));
        }
        ll.addView(newListItemButton(this, "退出 Looper", "没事别按", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quitLooper();
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT)
                        .show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "abort()", "没事别按", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    long libc = Natives.dlopen("libc.so", Natives.RTLD_NOLOAD);
                    if (libc == 0) {
                        throw new RuntimeException("dlopen libc.so failed");
                    }
                    long abort = Natives.dlsym(libc, "abort");
                    if (abort == 0) {
                        String msg = Natives.dlerror();
                        if (msg != null) {
                            throw new RuntimeException(msg);
                        } else {
                            throw new RuntimeException("dlsym 'abort' failed");
                        }
                    }
                    Natives.call(abort);
                } catch (Throwable e) {
                    Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT)
                        .show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "((void(*)())0)();", "空指针测试, 没事别按", null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Natives.load(TroubleshootActivity.this);
                        Natives.call(0L);
                    } catch (Throwable e) {
                        Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            }));
        ll.addView(newListItemButton(this, "*((int*)0)=0;", "空指针测试, 没事别按", null,
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Natives.load(TroubleshootActivity.this);
                        Natives.memset(0, 0, 1);
                    } catch (Throwable e) {
                        Toast.makeText(TroubleshootActivity.this, e.toString(), Toast.LENGTH_SHORT)
                            .show();
                    }
                }
            }));
        ll.addView(newListItemButton(this, "测试通知", "点击测试通知", null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent inner = new Intent(getApplication(), ExfriendListActivity.class);
                    Intent wrapper = new Intent();
                    wrapper.setClassName(getApplication().getPackageName(),
                        ActProxyMgr.STUB_DEFAULT_ACTIVITY);
                    wrapper.putExtra(ActProxyMgr.ACTIVITY_PROXY_INTENT, inner);
                    PendingIntent pi = PendingIntent.getActivity(getApplication(), 0, wrapper, 0);
                    NotificationManager nm = (NotificationManager) HostInformationProviderKt
                        .getHostInfo().getApplication()
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                    Notification n = ExfriendManager.getCurrent()
                        .createNotiComp(nm, "Ticker", "Title", "Content",
                            new long[]{100, 200, 200, 100}, pi);
                    nm.notify(ExfriendManager.ID_EX_NOTIFY, n);
                } catch (Throwable e) {
                    CustomDialog.createFailsafe(TroubleshootActivity.this).setCancelable(true)
                        .setPositiveButton(getString(android.R.string.ok), null)
                        .setTitle(getShort$Name(e)).setMessage(Log.getStackTraceString(e)).show();
                }
            }
        }));
        ll.addView(newListItemButton(this, "测试数据库", null, null,
            clickToProxyActAction(DatabaseTestActivity.class)));
        ll.addView(newListItemButton(this, "测试 MMKV", null, null,
            clickToProxyActAction(MmkvTestActivity.class)));
        if (BuildConfig.DEBUG) {
            ll.addView(newListItemButton(this, "新界面", null, null,
                clickToProxyActAction(MainActivity.class)));
        }

        ll.addView(subtitle(this, ""));

        ll.addView(subtitle(this, "反混淆信息", ResUtils.skin_black.getDefaultColor()));
        for (int i = 1; i <= DexKit.DEOBF_NUM_C; i++) {
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) {
                    continue;
                }
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                String currName = "(void*)0";
                DexMethodDescriptor md = DexKit.getMethodDescFromCache(i);
                if (md != null) {
                    currName = md.toString();
                } else {
                    Class<?> c = DexKit.loadClassFromCache(i);
                    if (c != null) {
                        currName = c.getName();
                    }
                }
                ll.addView(
                    subtitle(this, "  [" + i + "]" + shortName + "\n" + orig + "\n= " + currName,
                        ResUtils.skin_black.getDefaultColor(), true));
            } catch (Throwable e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString(),
                    ResUtils.skin_black.getDefaultColor(), true));
            }
        }
        for (int ii = 1; ii <= DexKit.DEOBF_NUM_N; ii++) {
            int i = 20000 + ii;
            try {
                String tag = DexKit.a(i);
                String orig = DexKit.c(i);
                if (orig == null) {
                    continue;
                }
                orig = orig.replace("/", ".");
                String shortName = Utils.getShort$Name(orig);
                String currName = "(void*)0";
                DexMethodDescriptor md = DexKit.getMethodDescFromCache(i);
                if (md != null) {
                    currName = md.toString();
                } else {
                    Class<?> c = DexKit.loadClassFromCache(i);
                    if (c != null) {
                        currName = c.getName();
                    }
                }
                ll.addView(
                    subtitle(this, "  [" + i + "]" + shortName + "\n" + orig + "\n= " + currName,
                        ResUtils.skin_black.getDefaultColor(), true));
            } catch (Throwable e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString(),
                    ResUtils.skin_black.getDefaultColor(), true));
            }
        }

        Set<Map.Entry<String, Object>> set = ConfigTable.INSTANCE.getCacheMap().entrySet();
        int i = 40001;
        for (Map.Entry<String, Object> entry : set) {
            try {
                String shortName = entry.getKey();
                String currName = entry.getValue() + "";
                ll.addView(subtitle(this, "  [" + i + "]" + shortName + "\n" + currName,
                    ResUtils.skin_black.getDefaultColor(), true));
            } catch (Exception e) {
                ll.addView(subtitle(this, "  [" + i + "]" + e.toString(),
                    ResUtils.skin_black.getDefaultColor(), true));
            }
            i++;
        }

        {
            int cost;
            cost = Parasitics.getResourceInjectionCost();
            ll.addView(
                subtitle(this, "ResourceInjectionCost: " + (cost < 0 ? "FAILED" : cost + "ms"),
                    ResUtils.skin_black.getDefaultColor(), true));
            cost = Parasitics.getActivityStubHookCost();
            ll.addView(
                subtitle(this, "ActivityStubHookCost: " + (cost < 0 ? "FAILED" : cost + "ms"),
                    ResUtils.skin_black.getDefaultColor(), true));
        }

        ll.addView(subtitle(this, "SystemClassLoader\n" + ClassLoader.getSystemClassLoader()
                + "\nContext.getClassLoader()\n" + getClassLoader()
                + "\nThread.getContextClassLoader()\n" + Thread.currentThread().getContextClassLoader()
                + "\nInitiator.getHostClassLoader()\n" + Initiator.getHostClassLoader(),
            ResUtils.skin_black.getDefaultColor(), true));
        long ts = Utils.getBuildTimestamp();
        ll.addView(subtitle(this, "Build Time: " + (ts > 0 ? new Date(ts).toString() : "unknown"),
            ResUtils.skin_black.getDefaultColor(), true));
        String info;
        try {
            Natives.load(this);
            info = "pagesize=" + Natives.getpagesize() + ", sizeof(void*)=" + Natives.sizeofptr()
                + ", addr="
                + Long.toHexString(Natives.dlopen("libnatives.so", Natives.RTLD_NOLOAD));
        } catch (Throwable e3) {
            log(e3);
            info = e3.toString();
        }
        ll.addView(subtitle(this, info, ResUtils.skin_black.getDefaultColor(), true));
        __ll.setLayoutParams(new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        setContentView(bounceScrollView);
        LinearLayout.LayoutParams _lp_fat = new LinearLayout.LayoutParams(MATCH_PARENT, 0);
        _lp_fat.weight = 1;
        setTitle("故障排除");
        setContentBackgroundDrawable(ResUtils.skin_background);
        return true;
    }

    public View.OnClickListener clickToRefreshUserStatus() {
        return view -> {
            long uin = Utils.getLongAccountUin();
            if (uin < 10000) {
                return;
            }
            CustomDialog.createFailsafe(view.getContext()).setTitle("状态")
                .setCancelable(true).setMessage("破解版没有黑白名单哦").setPositiveButton("确认", null).show();
        };
    }

    public View.OnClickListener clickToWipeDeletedFriends() {
        return v -> {
            CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
            dialog.setPositiveButton("确认", (dialog1, which) -> {
                try {
                    ExfriendManager exm = ExfriendManager.getCurrent();
                    Iterator it = exm.getEvents().entrySet().iterator();
                    while (it.hasNext()) {
                        EventRecord ev = (EventRecord) ((Map.Entry) it.next()).getValue();
                        if (exm.getPersons().get(ev.operand).friendStatus
                            == FriendRecord.STATUS_FRIEND_MUTUAL) {
                            it.remove();
                        }
                    }
                    exm.saveConfigure();
                    Toasts.success(TroubleshootActivity.this, "操作成功");
                } catch (Throwable e) {
                }
            });
            dialog.setNegativeButton("取消", new DummyCallback());
            dialog.setCancelable(true);
            dialog.setMessage("此操作将删除当前帐号(" + getLongAccountUin()
                + ")下的 已恢复 的历史好友记录(记录可单独删除).如果因bug大量好友被标记为已删除,请先刷新好友列表,然后再点击此按钮.\n此操作不可恢复");
            dialog.setTitle("确认操作");
            dialog.show();
        };
    }

    public View.OnClickListener clickToWipeAllFriends() {
        return v -> {
            CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        Long qq = Utils.getLongAccountUin();
                        new File(
                            HostInformationProviderKt.getHostInfo().getApplication().getFilesDir()
                                .getAbsolutePath() + "/qnotified_" + qq + ".dat").delete();
                        ExfriendManager exm = ExfriendManager.getCurrent();
                        exm.getConfig().reinit();
                        exm.reinit();
                        Toasts.success(TroubleshootActivity.this, "操作成功");
                    } catch (Throwable e) {
                        Utils.log(e);
                    }
                }
            });
            dialog.setNegativeButton("取消", new DummyCallback());
            dialog.setCancelable(true);
            dialog.setMessage("此操作将删除当前帐号(" + getLongAccountUin()
                + ")下的 全部 的历史好友记录,通常您不需要进行此操作.如果您的历史好友列表中因bug出现大量好友,请在联系人列表下拉刷新后点击 删除标记为已恢复的好友 .\n此操作不可恢复");
            dialog.setTitle("确认操作");
            dialog.show();
        };
    }

    public View.OnClickListener clickToCleanCache() {
        return v -> {
            CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
            dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        new File(
                            HostInformationProviderKt.getHostInfo().getApplication().getFilesDir()
                                .getAbsolutePath() + "/qnotified_cache.dat").delete();
                        ConfigManager cfg = ConfigManager.getCache();
                        cfg.getAllConfig().clear();
                        //cfg.getFile().delete();
                        System.exit(0);
                    } catch (Throwable e) {
                        log(e);
                    }
                }
            });
            dialog.setNegativeButton("取消", new DummyCallback());
            dialog.setCancelable(true);
            dialog.setMessage(
                "确认清除缓存,并重新计算适配数据?\n点击确认后请等待3秒后手动重启" + HostInformationProviderKt.getHostInfo()
                    .getHostName() + ".");
            dialog.setTitle("确认操作");
            dialog.show();
        };
    }

    public View.OnClickListener clickToReset() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = CustomDialog.create(TroubleshootActivity.this);
                dialog.setPositiveButton("确认", (dialog1, which) -> {
                    try {
                        new File(
                            HostInformationProviderKt.getHostInfo().getApplication().getFilesDir()
                                .getAbsolutePath() + "/qnotified_config.dat").delete();
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        cfg.getAllConfig().clear();
                        //cfg.getFile().delete();
                        System.exit(0);
                    } catch (Throwable e) {
                        log(e);
                    }
                });
                dialog.setNegativeButton("取消", new Utils.DummyCallback());
                dialog.setCancelable(true);
                dialog.setMessage("此操作将删除该模块的所有配置信息,包括屏蔽通知的群列表,但不包括历史好友列表.点击确认后请等待3秒后手动重启"
                    + HostInformationProviderKt.getHostInfo().getHostName() + ".\n此操作不可恢复");
                dialog.setTitle("确认操作");
                dialog.show();
            }
        };
    }

}
