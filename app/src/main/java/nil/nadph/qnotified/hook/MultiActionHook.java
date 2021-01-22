package nil.nadph.qnotified.hook;

import android.app.Activity;
import android.content.Context;
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
import de.robv.android.xposed.XposedBridge;
import me.singleneuron.qn_kernel.tlb.ConfigTable;
import nil.nadph.qnotified.R;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.bridge.QQMessageFacade;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.ui.ResUtils;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator._BaseChatPie;
import static nil.nadph.qnotified.util.Initiator._ChatMessage;
import static nil.nadph.qnotified.util.Utils.*;
/*
This code has been tested in QQ8.0.0-8.5.5 and TIM all versions.
 */
public class MultiActionHook extends CommonDelayableHook {
    public static final MultiActionHook INSTANCE = new MultiActionHook();
    private static Bitmap img;
    Class clz_MultiMsg_Manager;
    private final String fieldName = ConfigTable.INSTANCE.getConfig(MultiActionHook.class.getSimpleName());
    private Object baseChatPie;

    MultiActionHook() {
        super("qn_multi_action", SyncUtils.PROC_MAIN, false, new DexDeobfStep(DexKit.C_MessageCache), new DexDeobfStep(DexKit.C_MSG_REC_FAC), new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__createMulti), new DexDeobfStep(DexKit.C_MultiMsg_Manager));
    }

    @Override
    public boolean initOnce() {
        try {
            XposedBridge.hookMethod(DexKit.doFindMethod(DexKit.N_BASE_CHAT_PIE__createMulti), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (!isEnabled())
                            return;
                        clz_MultiMsg_Manager = DexKit.doFindClass(DexKit.C_MultiMsg_Manager);
                        LinearLayout rootView = iget_object_or_null(param.thisObject, fieldName, LinearLayout.class);
                        BaseActivity context = (BaseActivity) rootView.getContext();
                        baseChatPie = getFirstByType(param.thisObject, _BaseChatPie());
                        int count = rootView.getChildCount();
                        boolean enableTalkBack = rootView.getChildAt(0).getContentDescription() != null;
                        if (rootView.findViewById(R.id.ketalRecallImageView) == null )
                            rootView.addView(create(context, getRecallBitmap(), enableTalkBack), count - 1);
                        setMargin(context, rootView);
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

    private void recall() {
        try {
            Object manager = findMethodByTypes_1(clz_MultiMsg_Manager, clz_MultiMsg_Manager).invoke(null);
            List list = (List) findMethodByTypes_1(clz_MultiMsg_Manager, List.class).invoke(manager);
            for (Object msg : list)
                QQMessageFacade.revokeMessage(msg);
            invoke_virtual_any(baseChatPie, false, null, false, boolean.class, _ChatMessage(), boolean.class);
            baseChatPie = null;
        } catch (Exception e) {
            log(e);
        }
    }

    private void setMargin(Activity activity, LinearLayout rootView) {
        DisplayMetrics outMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
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

    private ImageView create(Context context, Bitmap bitmap, boolean enableTalkBack) {
        ImageView imageView = new ImageView(context);
        if (enableTalkBack) {
            imageView.setContentDescription("撤回");
        }
        imageView.setOnClickListener(v -> recall());
        imageView.setImageBitmap(bitmap);
        imageView.setId(R.id.ketalRecallImageView);
        return imageView;
    }

    private static Bitmap getRecallBitmap() {
        if (img == null || img.isRecycled())
            img = BitmapFactory.decodeStream(ResUtils.openAsset("recall.png"));
        return img;
    }
}
