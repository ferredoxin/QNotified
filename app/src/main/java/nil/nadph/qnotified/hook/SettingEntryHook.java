package nil.nadph.qnotified.hook;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.adapter.ActProxyMgr;
import nil.nadph.qnotified.QQMainHook;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.util.Utils;

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

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            XposedHelpers.findAndHookMethod(load("com.tencent.mobileqq.activity.QQSettingSettingActivity"), "doOnCreate", Bundle.class, new XC_MethodHook(47) {
                @Override
                protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                    try {
                        View itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormSimpleItem"));
                        if (itemRef == null)
                            itemRef = (View) Utils.iget_object_or_null(param.thisObject, "a", load("com/tencent/mobileqq/widget/FormCommonSingleLineItem"));
                        View item = (View) new_instance(itemRef.getClass(), param.thisObject, Context.class);
                        invoke_virtual(item, "setLeftText", "QNotified", CharSequence.class);
                        invoke_virtual(item, "setRightText", Utils.QN_VERSION_NAME, CharSequence.class);
                        LinearLayout list = (LinearLayout) itemRef.getParent();
                        list.addView(item, 0, itemRef.getLayoutParams());
                        item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                QQMainHook.startProxyActivity((Context) param.thisObject, ActProxyMgr.ACTION_ADV_SETTINGS);
                            }
                        });
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
