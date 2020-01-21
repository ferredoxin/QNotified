package nil.nadph.qnotified.hook;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.StartupHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.adapter.ActProxyMgr;
import nil.nadph.qnotified.util.Utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class SettingEntryHook extends BaseDelayableHook {
    private SettingEntryHook() {
    }

    private static final SettingEntryHook self = new SettingEntryHook();

    public static SettingEntryHook get() {
        return self;
    }

    private boolean inited = false;

    public static final int R_ID_SETTING_ENTRY = 0x300AFF71;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", Bundle.class, new XC_MethodHook(52) {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    try {
                        View itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormSimpleItem"));
                        if (itemRef == null)
                            itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormCommonSingleLineItem"));
                        View item = (View) new_instance(itemRef.getClass(), param.thisObject, Context.class);
                        item.setId(R_ID_SETTING_ENTRY);
                        invoke_virtual(item, "setLeftText", "QNotified", CharSequence.class);
                        invoke_virtual(item, "setRightText", Utils.QN_VERSION_NAME, CharSequence.class);
                        invoke_virtual(item, "setBgType", 2, int.class);
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                StartupHook.startProxyActivity((Context) param.thisObject, ActProxyMgr.ACTION_ADV_SETTINGS);
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
                                View accountItem = (View) ((View) list.findViewById(account_switch)).getParent();
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
    public int[] getPreconditions() {
        return new int[0];
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
