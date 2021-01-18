package nil.nadph.qnotified.hook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.tencent.mobileqq.app.BaseActivity;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import me.ketal.util.TIMConfigTable;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.QQMessageFacade;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.DexKit;

import static me.ketal.util.TIMVersion.TIM_3_0_0;
import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Initiator._ChatMessage;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;
/*
This code has been tested in QQ8.0.0-8.5.5 and TIM all versions.
 */
public class MultiActionHook extends CommonDelayableHook {
    public static final MultiActionHook INSTANCE = new MultiActionHook();
    private static Bitmap img;
    Class clz_AIO_MultiAction_Helper;
    Class clz_MultiMsgManager;
    private BaseActivity context;
    private LinearLayout rootView;
    private Object baseChatPie;

    MultiActionHook() {
        super("qn_multi_action", SyncUtils.PROC_MAIN, false, new DexDeobfStep(DexKit.C_MessageCache), new DexDeobfStep(DexKit.C_MSG_REC_FAC));
    }

    @Override
    public boolean initOnce() {
        try {
            if (isTim(getApplication()) && getHostVersionCode() <= TIM_3_0_0)
                //Toasts.showToast(context, TOAST_TYPE_ERROR, "不支持当前版本的TIM，请更新至3.1.1及更高后使用此功能", Toast.LENGTH_SHORT);
                return false;
            findClass();
            String hookMethod = findMethodByTypes_1(clz_AIO_MultiAction_Helper, void.class, _ChatMessage()).getName();
            XposedHelpers.findAndHookMethod(clz_AIO_MultiAction_Helper, hookMethod, _ChatMessage(), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        rootView = getFirstByType(param.thisObject, LinearLayout.class);
                        context = getFirstByType(param.thisObject, BaseActivity.class);
                        baseChatPie = getFirstByType(param.thisObject, _BaseChatPie());
                        int count = rootView.getChildCount();
                        boolean enableTalkBack = rootView.getChildAt(0).getContentDescription() != null;
                        rootView.addView(create(getRecallBitmap(), enableTalkBack), count - 1);
                        setMargin(rootView);
                    } catch (Exception e) {
                        log(e);
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }

    private void findClass() {
        if (isTim(getApplication())) {
            String[] methods = ((String) TIMConfigTable.INSTANCE.getConfig(MultiActionHook.class.getSimpleName())).split("\\|");
            clz_AIO_MultiAction_Helper = load(methods[0]);
            clz_MultiMsgManager = load(methods[1]);
        } else {
            clz_AIO_MultiAction_Helper = load("com.tencent.mobileqq.activity.aio.helper.AIOMultiActionHelper");
            clz_MultiMsgManager = load("com.tencent.mobileqq.multimsg.MultiMsgManager");
        }
    }

    private void recall() {
        try {
            Object manager = invoke_static(clz_MultiMsgManager, "a", clz_MultiMsgManager);
            String methodName = findMethodByTypes_1(clz_MultiMsgManager,List.class).getName();
            List list = (List) invoke_virtual(manager, methodName, List.class);
            for (Object msg : list)
                QQMessageFacade.revokeMessage(msg);
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
        for (int i = 1; i < count; i++) {
            View view = rootView.getChildAt(i);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(w, w);
            layoutParams.setMargins(leftMargin, 0, 0, 0);
            layoutParams.gravity = 16;
            view.setLayoutParams(layoutParams);
        }
    }

    private ImageView create(Bitmap bitmap, boolean enableTalkBack) {
        ImageView imageView = new ImageView(context);
        if (enableTalkBack) {
            imageView.setContentDescription("撤回");
        }
        imageView.setOnClickListener(v -> recall());
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    private static Bitmap getRecallBitmap() {
        if (img == null || img.isRecycled())
            img = BitmapFactory.decodeStream(ResUtils.openAsset("recall.png"));
        return img;
    }
}
