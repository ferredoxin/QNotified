package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.mobileqq.app.BaseActivity;

import java.lang.reflect.Field;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Initiator._ChatMessage;
import static nil.nadph.qnotified.util.Initiator._QQAppInterface;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.getQQAppInterface;
import static nil.nadph.qnotified.util.Utils.iget_object_or_null;
import static nil.nadph.qnotified.util.Utils.invoke_static;
import static nil.nadph.qnotified.util.Utils.invoke_virtual;
import static nil.nadph.qnotified.util.Utils.invoke_virtual_any;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.sget_object;

/*
It can't work on tim, it can be solved by positioning the confusion class.
But I can't complete.
 */
public class MultiActionHook extends BaseDelayableHook {
    public static final String qn_mulit_action = "qn_multi_action";
    private static final MultiActionHook self = new MultiActionHook();
    private static Bitmap img;
    private boolean inited = false;
    private Activity context;
    private LinearLayout rootView;
    private Object baseChatPie;

    MultiActionHook() {
    }

    public static MultiActionHook get() {
        return self;
    }

    private static Bitmap getRecallBitmap() {
        if (img == null || img.isRecycled())
            img = BitmapFactory.decodeStream(ResUtils.openAsset("recall.png"));
        return img;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = load("com.tencent.mobileqq.activity.aio.helper.AIOMultiActionHelper");
            XposedHelpers.findAndHookMethod(clz, "a", _ChatMessage(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        rootView = iget_object_or_null(param.thisObject, "a", LinearLayout.class);
                        context = iget_object_or_null(param.thisObject, "a", BaseActivity.class);
                        baseChatPie = iget_object_or_null(param.thisObject, "a", _BaseChatPie());
                        int count = rootView.getChildCount();
                        rootView.addView(create(getRecallBitmap()), count - 1);
                        setMargin(rootView);
                    } catch (Exception e) {
                        log(e);
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

    private void recall() {
        Class clz_MultiMsgManager = load("com.tencent.mobileqq.multimsg.MultiMsgManager");
        Class clz_revoke_helper = load("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper");
        Class clz_q_progress_dialog = load("com.tencent.mobileqq.widget.QQProgressDialog");
        Class clz_troop_utils = load("com.tencent.mobileqq.troop.utils.TroopUtils");
        try {
            Object manager = invoke_static(clz_MultiMsgManager, "a", clz_MultiMsgManager);
            List list = (List) invoke_virtual(manager, "a", List.class);
            for (Object msg : list) {
                Object helper = clz_revoke_helper.getConstructor(_BaseChatPie()).newInstance(baseChatPie);
                invoke_virtual(helper, "f", msg, _ChatMessage());
                String friendUin = (String) iget_object_or_null(msg, "frienduin");
                boolean z = (boolean) invoke_static(clz_troop_utils, "a", getQQAppInterface(), friendUin, getQQAppInterface().getAccount(), _QQAppInterface(), String.class, String.class);
                invoke_virtual(helper, "a", "0X800A7F6", z, String.class, boolean.class);

                for (Field field : _BaseChatPie().getDeclaredFields()) {
                    if (field.getType().equals(clz_q_progress_dialog)) {
                        field.setAccessible(true);
                        Object o = field.get(baseChatPie);
                        if (o != null) {
                            invoke_virtual(o, "dismiss");
                        }
                    }
                }
            }
            invoke_virtual_any(baseChatPie, false, null, false, boolean.class, _ChatMessage(), boolean.class);
        } catch (Exception e) {
            log(e);
        }
    }

    private void setMargin(LinearLayout rootView) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int count = rootView.getChildCount();
        int rootMargin = ((RelativeLayout.LayoutParams) rootView.getLayoutParams()).leftMargin;
        int w = ((LinearLayout.LayoutParams) rootView.getChildAt(0).getLayoutParams()).height;
        int leftMargin = (width - rootMargin * 2 - w * count) / (count - 1);
        for (int i = 0; i < count; i++) {
            View view = rootView.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            if (i == 0)
                layoutParams.setMargins(0, 0, 0, 0);
            else
                layoutParams.setMargins(leftMargin, 0, 0, 0);
            layoutParams.gravity = 16;
            view.setLayoutParams(layoutParams);
        }
    }

    private ImageView create(Bitmap bitmap) {
        ImageView imageView = new ImageView(context);
        boolean enableTalkBack = true;
        try {
            enableTalkBack = (boolean) sget_object(load("com.tencent.common.config.AppSetting"), "enableTalkBack");
        } catch (Exception e) {
            //log(e);
        }
        if (enableTalkBack) {
            imageView.setContentDescription("撤回");
        }
        imageView.setOnClickListener(v -> recall());
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_mulit_action);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_mulit_action, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }
}
