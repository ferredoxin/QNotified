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

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.ReflexUtil.iget_object_or_null;
import static nil.nadph.qnotified.util.ReflexUtil.invoke_virtual;
import static nil.nadph.qnotified.util.ReflexUtil.new_instance;
import static nil.nadph.qnotified.util.Utils.log;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.activity.EulaActivity;
import nil.nadph.qnotified.activity.SettingsActivity;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.ReflexUtil;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class SettingEntryHook extends CommonDelayableHook {

    public static final SettingEntryHook INSTANCE = new SettingEntryHook();

    private SettingEntryHook() {
        super("__NOT_USED__", new DexDeobfStep(DexKit.C_DIALOG_UTIL));
    }

    @Override
    public boolean initOnce() {
        try {
            XposedHelpers
                .findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"),
                    "doOnCreate", Bundle.class, new XC_MethodHook(52) {
                        @Override
                        protected void afterHookedMethod(final MethodHookParam param)
                            throws Throwable {
                            try {
                                final Activity activity = (Activity) param.thisObject;
                                Class<?> itemClass;
                                View itemRef;
                                itemRef = (View) iget_object_or_null(activity, "a",
                                    load("com/tencent/mobileqq/widget/FormSimpleItem"));
                                if (itemRef == null && (itemClass = load(
                                    "com/tencent/mobileqq/widget/FormCommonSingleLineItem"))
                                    != null) {
                                    itemRef = (View) iget_object_or_null(activity, "a", itemClass);
                                }
                                if (itemRef == null) {
                                    Class<?> clz = load(
                                        "com/tencent/mobileqq/widget/FormCommonSingleLineItem");
                                    if (clz == null) {
                                        clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                                    }
                                    itemRef = (View) ReflexUtil.getFirstNSFByType(activity, clz);
                                }
                                View item;
                                if (itemRef == null) {
                                    // we are in triassic period?
                                    item = (View) new_instance(
                                        load("com/tencent/mobileqq/widget/FormSimpleItem"),
                                        activity, Context.class);
                                } else {
                                    // modern age
                                    item = (View) new_instance(itemRef.getClass(), activity,
                                        Context.class);
                                }
                                item.setId(R.id.setting2Activity_settingEntryItem);
                                invoke_virtual(item, "setLeftText", "QNotified",
                                    CharSequence.class);
                                invoke_virtual(item, "setBgType", 2, int.class);
                                if (LicenseStatus.hasUserAcceptEula()) {
                                    invoke_virtual(item, "setRightText", Utils.QN_VERSION_NAME,
                                        CharSequence.class);
                                } else {
                                    invoke_virtual(item, "setRightText", "[未激活]",
                                        CharSequence.class);
                                }
                                item.setOnClickListener(v -> {
                                    if (LicenseStatus.hasUserAcceptEula()) {
                                        activity.startActivity(
                                            new Intent(activity, SettingsActivity.class));
                                    } else {
                                        activity.startActivity(
                                            new Intent(activity, EulaActivity.class));
                                        activity.finish();
                                    }
                                });
                                if (itemRef != null) {
                                    //modern age
                                    ViewGroup list = (ViewGroup) itemRef.getParent();
                                    ViewGroup.LayoutParams reflp;
                                    if (list.getChildCount() == 1) {
                                        //junk!
                                        list = (ViewGroup) list.getParent();
                                        reflp = ((View) itemRef.getParent()).getLayoutParams();
                                    } else {
                                        reflp = itemRef.getLayoutParams();
                                    }
                                    ViewGroup.LayoutParams lp = null;
                                    if (reflp != null) {
                                        lp = new ViewGroup.LayoutParams(
                                            MATCH_PARENT, /*reflp.height*/WRAP_CONTENT);
                                    }
                                    int index = 0;
                                    int account_switch = list.getContext().getResources()
                                        .getIdentifier("account_switch", "id",
                                            list.getContext().getPackageName());
                                    try {
                                        if (account_switch > 0) {
                                            View accountItem = (View) list
                                                .findViewById(account_switch).getParent();
                                            for (int i = 0; i < list.getChildCount(); i++) {
                                                if (list.getChildAt(i) == accountItem) {
                                                    index = i + 1;
                                                    break;
                                                }
                                            }
                                        }
                                        if (index > list.getChildCount()) {
                                            index = 0;
                                        }
                                    } catch (NullPointerException ignored) {
                                    }
                                    list.addView(item, index, lp);
                                } else {
                                    // triassic period, we have to find the ViewGroup ourselves
                                    int qqsetting2_msg_notify = activity.getResources()
                                        .getIdentifier("qqsetting2_msg_notify", "id",
                                            activity.getPackageName());
                                    if (qqsetting2_msg_notify == 0) {
                                        throw new UnsupportedOperationException(
                                            "R.id.qqsetting2_msg_notify not found in triassic period");
                                    } else {
                                        ViewGroup vg = (ViewGroup) activity
                                            .findViewById(qqsetting2_msg_notify).getParent()
                                            .getParent();
                                        vg.addView(item, 0, new ViewGroup.LayoutParams(
                                            MATCH_PARENT, /*reflp.height*/WRAP_CONTENT));
                                    }
                                }
                            } catch (Throwable e) {
                                log(e);
                                throw e;
                            }
                        }
                    });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean checkPreconditions() {
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
