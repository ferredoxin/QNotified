package me.ketal.hook;

import android.content.Context;
import android.view.View;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;
/*
This code has been tested in QQ8.0.0-8.5.5 and TIM all versions.
 */
public class HideAssistantRemoveTips extends CommonDelayableHook {
    public static final HideAssistantRemoveTips INSTANCE = new HideAssistantRemoveTips();

    protected HideAssistantRemoveTips() {
        super("ketal_hide_assistant_removetips", SyncUtils.PROC_MAIN, false);
    }

    @Override
    protected boolean initOnce() {
        try {
            Class clazz = load("com.tencent.mobileqq.activity.ChatActivityUtils");
            XposedHelpers.findAndHookMethod(clazz, "a", Context.class, String.class, View.OnClickListener.class, View.OnClickListener.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (isEnabled())
                        param.setResult(null);
                }
            });
            return true;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
