/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.ExfriendManager;
import nil.nadph.qnotified.MainHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.activity.EulaActivity;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.*;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class SettingEntryHook extends BaseDelayableHook {
    public static final int R_ID_SETTING_ENTRY = 0x300AFF71;
    private static final SettingEntryHook self = new SettingEntryHook();
    private boolean inited = false;

    private SettingEntryHook() {
    }

    @NonNull
    public static SettingEntryHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", Bundle.class, new XC_MethodHook(52) {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    if (LicenseStatus.isLoadingDisabled() || LicenseStatus.isSilentGone()) return;
                    try {
                        Class<?> itemClass = null;
                        View itemRef = null;
                        itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormSimpleItem"));
                        if (itemRef == null && (itemClass = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem")) != null)
                            itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", itemClass);
                        if (itemRef == null) {
                            Class<?> clz = load("com/tencent/mobileqq/widget/FormCommonSingleLineItem");
                            if (clz == null)
                                clz = load("com/tencent/mobileqq/widget/FormSimpleItem");
                            itemRef = (View) Utils.getFirstNSFByType(param.thisObject, clz);
                        }
                        View item = (View) new_instance(itemRef.getClass(), param.thisObject, Context.class);
                        item.setId(R_ID_SETTING_ENTRY);
                        invoke_virtual(item, "setLeftText", "QNotified", CharSequence.class);
                        invoke_virtual(item, "setBgType", 2, int.class);
                        if (LicenseStatus.isBlacklisted()) {
                            invoke_virtual(item, "setRightText", "[禁用]", CharSequence.class);
                            item.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(final View v) {
                                    if (!LicenseStatus.isBlacklisted()) return false;
                                    Toast.makeText((Context) param.thisObject, "正在重新检验授权", Toast.LENGTH_SHORT).show();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                ExfriendManager.getCurrent().doUpdateUserStatusFlags();
                                            } catch (final Exception e) {
                                                v.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText((Context) param.thisObject, e.toString(), Toast.LENGTH_LONG).show();
                                                    }
                                                });
                                            }
                                        }
                                    }).start();
                                    return true;
                                }
                            });
                        } else {
                            if (LicenseStatus.hasUserAcceptEula()) {
                                invoke_virtual(item, "setRightText", Utils.QN_VERSION_NAME, CharSequence.class);
                            } else {
                                invoke_virtual(item, "setRightText", "[未激活]", CharSequence.class);
                            }
                        }
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (LicenseStatus.isBlacklisted()) {
                                    Utils.showToast((Context) param.thisObject, TOAST_TYPE_ERROR, "无法使用本模块因为您已被拉黑", Toast.LENGTH_LONG);
                                } else {
                                    if (LicenseStatus.hasUserAcceptEula()) {
                                        MainHook.startProxyActivity((Context) param.thisObject, ActProxyMgr.ACTION_ADV_SETTINGS);
                                    } else {
                                        MainHook.startProxyActivity((Context) param.thisObject, EulaActivity.class);
                                        if (param.thisObject instanceof Activity) {
                                            ((Activity) param.thisObject).finish();
                                        }
                                    }
                                }
                            }
                        });
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
                            lp = new ViewGroup.LayoutParams(MATCH_PARENT, /*reflp.height*/WRAP_CONTENT);
                        }
                        int index = 0;
                        int account_switch = list.getContext().getResources().getIdentifier("account_switch", "id", list.getContext().getPackageName());
                        try {
                            if (account_switch > 0) {
                                View accountItem = (View) list.findViewById(account_switch).getParent();
                                for (int i = 0; i < list.getChildCount(); i++) {
                                    if (list.getChildAt(i) == accountItem) {
                                        index = i + 1;
                                        break;
                                    }
                                }
                            }
                            if (index > list.getChildCount()) index = 0;
                        } catch (NullPointerException ignored) {
                        }
                        list.addView(item, index, lp);
                    } catch (Throwable e) {
                        log(e);
                        throw e;
                    }
                }
            });
            inited = true;
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
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_DIALOG_UTIL)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        //do nothing
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
