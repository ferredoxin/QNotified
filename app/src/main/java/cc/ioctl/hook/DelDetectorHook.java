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
package cc.ioctl.hook;


import static android.widget.LinearLayout.LayoutParams.MATCH_PARENT;
import static android.widget.LinearLayout.LayoutParams.WRAP_CONTENT;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_static;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual_original;
import static nil.nadph.qnotified.util.Utils.dip2px;
import static nil.nadph.qnotified.util.Utils.dip2sp;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.ioctl.activity.ExfriendListActivity;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import me.singleneuron.hook.AppCenterHookKt;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.activity.TroubleshootActivity;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.bridge.FriendChunk;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.CliOper;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class DelDetectorHook extends CommonDelayableHook {

    public static final int VIEW_ID_DELETED_FRIEND = 0x00EE77AA;
    public static final DelDetectorHook INSTANCE = new DelDetectorHook();
    public HashSet addedListView = new HashSet();
    public WeakReference<TextView> exfriendRef;
    public WeakReference<TextView> redDotRef;
    private final XC_MethodHook exfriendEntryHook = new XC_MethodHook(1200) {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            try {
                if (LicenseStatus.sDisableCommonHooks) {
                    return;
                }
                boolean hide = false;
                try {
                    hide = ConfigManager.getDefaultConfig()
                        .getBooleanOrFalse("qn_hide_ex_entry_group");
                } catch (Throwable e) {
                    log(e);
                }
                if (hide) {
                    return;
                }
                if (!param.thisObject.getClass().getName()
                    .contains("ContactsFPSPinnedHeaderExpandableListView")) {
                    return;
                }
                LinearLayout layout_entrance;
                View lv = (View) param.thisObject;
                final Activity splashActivity = (Activity) Utils.getContext(lv);
                ResUtils.initTheme(splashActivity);
                layout_entrance = new LinearLayout(splashActivity);
                RelativeLayout rell = new RelativeLayout(splashActivity);
                if (!addedListView.contains(lv)) {
                    invoke_virtual_original(lv, "addFooterView", layout_entrance, View.class);
                    addedListView.add(lv);
                }
                layout_entrance.setOrientation(LinearLayout.VERTICAL);
                TextView exfriend = null;
                if (exfriendRef == null || (exfriend = exfriendRef.get()) == null) {
                    exfriend = new TextView(splashActivity);
                    exfriendRef = new WeakReference<>(exfriend);
                }
                exfriend.setTextColor(ResUtils.skin_blue);
                exfriend.setTextSize(dip2sp(splashActivity, 17));
                exfriend.setId(VIEW_ID_DELETED_FRIEND);
                exfriend.setText("历史好友");
                exfriend.setGravity(Gravity.CENTER);
                exfriend.setClickable(true);

                TextView redDot = new TextView(splashActivity);
                redDotRef = new WeakReference<>(redDot);
                redDot.setTextColor(0xFFFF0000);

                redDot.setGravity(Gravity.CENTER);
                redDot.getPaint().setFakeBoldText(true);
                redDot.setTextSize(Utils.dip2sp(splashActivity, 10));
                try {
                    invoke_static(load("com/tencent/widget/CustomWidgetUtil"), "a", redDot, 3, 1, 0,
                        TextView.class, int.class, int.class, int.class, void.class);
                } catch (NullPointerException e) {
                    redDot.setTextColor(Color.RED);
                }
                ExfriendManager.get(Utils.getLongAccountUin()).setRedDot();

                int height = dip2px(splashActivity, 48);
                RelativeLayout.LayoutParams exlp = new RelativeLayout.LayoutParams(MATCH_PARENT,
                    height);
                exlp.topMargin = 0;
                exlp.leftMargin = 0;
                try {
                    if (exfriend.getParent() != null) {
                        ((ViewGroup) exfriend.getParent()).removeView(exfriend);
                    }
                } catch (Exception e) {
                    log(e);
                }
                rell.addView(exfriend, exlp);
                RelativeLayout.LayoutParams dotlp = new RelativeLayout.LayoutParams(WRAP_CONTENT,
                    WRAP_CONTENT);
                dotlp.topMargin = 0;
                dotlp.rightMargin = Utils.dip2px(splashActivity, 24);
                dotlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                dotlp.addRule(RelativeLayout.CENTER_VERTICAL);
                rell.addView(redDot, dotlp);
                layout_entrance.addView(rell);
                ViewGroup.LayoutParams llp = new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT);
                layout_entrance.setPadding(0, (int) (height * 0.3f), 0, (int) (0.3f * height));
                exfriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LicenseStatus.sDisableCommonHooks) {
                            return;
                        }
                        Intent intent = new Intent(splashActivity, ExfriendListActivity.class);
                        splashActivity.startActivity(intent);
                    }
                });
                exfriend.postInvalidate();
            } catch (Throwable e) {
                log(e);
                throw e;
            }
        }

    };

    private DelDetectorHook() {
        super("__NOT_USED__");
    }

    @Override
    public boolean initOnce() {
        findAndHookMethod(load("com/tencent/widget/PinnedHeaderExpandableListView"), "setAdapter",
            ExpandableListAdapter.class, exfriendEntryHook);
        AppCenterHookKt.initAppCenterHook();
        XposedHelpers
            .findAndHookMethod(load("com/tencent/mobileqq/activity/SplashActivity"), "doOnResume",
                new XC_MethodHook(700) {
                    boolean z = false;

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        try {
                            if (Utils.getLongAccountUin() > 10000) {
                                ExfriendManager ex = ExfriendManager.getCurrent();
                                ex.timeToUpdateFl();
                            }
                        } catch (Throwable e) {
                            log(e);
                            throw e;
                        }
                        if (Utils.getBuildTimestamp() < 0 && (Math.random() < 0.25)) {
                            TroubleshootActivity.quitLooper();
                        } else {
                            if (z) {
                                return;
                            }
                            CliOper.onLoad();
                            z = true;
                        }
                    }
                });
        findAndHookMethod(load("friendlist/GetFriendListResp"), "readFrom",
            load("com/qq/taf/jce/JceInputStream"), new XC_MethodHook(200) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        FriendChunk fc = new FriendChunk(param.thisObject);
                        ExfriendManager.onGetFriendListResp(fc);
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            });

        findAndHookMethod(load("friendlist/DelFriendResp"), "readFrom",
            load("com/qq/taf/jce/JceInputStream"), new XC_MethodHook(200) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        long uin = (Long) iget_object_or_null(param.thisObject, "uin");
                        long deluin = (Long) iget_object_or_null(param.thisObject, "deluin");
                        int result = (Integer) iget_object_or_null(param.thisObject, "result");
                        short errorCode = (Short) iget_object_or_null(param.thisObject,
                            "errorCode");
                        if (result == 0 && errorCode == 0) {
                            ExfriendManager.get(uin).markActiveDelete(deluin);
                        }
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            });
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }
}
