package nil.nadph.qnotified.hook;

import android.app.Application;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import static nil.nadph.qnotified.util.Initiator._PicItemBuilder;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class EmoPicHook extends BaseDelayableHook {

    private EmoPicHook() {
    }

    private static final EmoPicHook self = new EmoPicHook();

    public static EmoPicHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            final ConfigManager cfg = ConfigManager.getDefault();
            boolean canInit = checkPreconditions();
            if (!canInit && ConfigManager.getDefault().getBooleanOrFalse(qn_sticker_as_pic)) {
                if (Looper.myLooper() != null) {
                    showToast(getApplication(), TOAST_TYPE_ERROR, "QNotified:表情转图片功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) return false;
            XposedHelpers.findAndHookMethod(_PicItemBuilder(), "onClick", View.class, new XC_MethodHook(51) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    Object v0 = invoke_static(DexKit.doFindClass(DexKit.C_AIO_UTILS), "a", param.args[0], View.class, Object.class);
                    Object chatMessage = iget_object_or_null(v0, "a", load("com.tencent.mobileqq.data.ChatMessage"));
                    Object picMessageExtraData = iget_object_or_null(chatMessage, "picExtraData");
                    iput_object(chatMessage, "imageType", int.class, 0);
                    if (picMessageExtraData != null) {
                        iput_object(picMessageExtraData, "imageBizType", int.class, 0);
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
        return DexKit.tryLoadOrNull(DexKit.C_AIO_UTILS) != null;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_AIO_UTILS};
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
    public boolean isEnabled() {
        try {
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            return ConfigManager.getDefault().getBooleanOrFalse(qn_sticker_as_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
