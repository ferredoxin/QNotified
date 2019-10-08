package nil.nadph.qnotified.hook;

import android.os.Looper;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.record.ConfigManager;
import nil.nadph.qnotified.util.DexKit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;
import nil.nadph.qnotified.ipc.*;

public class FlashPicHook extends BaseDelayableHook {
    private FlashPicHook() {
    }

    private static final FlashPicHook self = new FlashPicHook();

    public static FlashPicHook get() {
        return self;
    }

    private boolean inited = false;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            final ConfigManager cfg = ConfigManager.getDefault();
            boolean canInit = checkPreconditions();
            if (!canInit && ConfigManager.getDefault().getBooleanOrFalse(qn_flash_as_pic)) {
                if (Looper.myLooper() != null) {
                    showToast(getApplication(), TOAST_TYPE_ERROR, "QNotified:闪照功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) return false;
            Class clz = DexKit.tryLoadOrNull(DexKit.C_FLASH_PIC_HELPER);
            Method isFlashPic = null;
            for (Method mi : clz.getDeclaredMethods()) {
                if (mi.getReturnType().equals(boolean.class) && mi.getParameterTypes().length == 1) {
                    isFlashPic = mi;
                    break;
                }
            }
            XposedBridge.hookMethod(isFlashPic, new XC_MethodHook(52) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (!ConfigManager.getDefault().getBooleanOrFalse(qn_flash_as_pic)) return;
                    } catch (Exception e) {
                        log(e);
                    }
                    String sn_ItemBuilderFactory = getShort$Name(DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC));
                    String sn_BasePicDownloadProcessor = getShort$Name(DexKit.doFindClass(DexKit.C_BASE_PIC_DL_PROC));
                    if (isCallingFrom(sn_ItemBuilderFactory) || isCallingFrom(sn_BasePicDownloadProcessor) || isCallingFrom("FlashPicItemBuilder")) {
                        param.setResult(false);
                    }
                }
            });
            Class tmp;
            Class mPicItemBuilder = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder");
            if (mPicItemBuilder == null) {
                try {
                    tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$6");
                    mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
                } catch (Exception ignored) {
                }
            }
            if (mPicItemBuilder == null) {
                try {
                    tmp = load("com.tencent.mobileqq.activity.aio.item.PicItemBuilder$7");
                    mPicItemBuilder = tmp.getDeclaredField("this$0").getType();
                } catch (Exception ignored) {
                }
            }
            Class mBaseBubbleBuilder$ViewHolder = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder$ViewHolder");
            if (mBaseBubbleBuilder$ViewHolder == null) {
                tmp = load("com.tencent.mobileqq.activity.aio.BaseBubbleBuilder");
                for (Method mi : tmp.getDeclaredMethods()) {
                    if (Modifier.isAbstract(mi.getModifiers()) && mi.getParameterTypes().length == 0) {
                        mBaseBubbleBuilder$ViewHolder = mi.getReturnType();
                        break;
                    }
                }
            }
            Method m = null;
            for (Method mi : mPicItemBuilder.getDeclaredMethods()) {
                if (mi.getReturnType().equals(View.class) && mi.getParameterTypes().length == 5) {
                    m = mi;
                    break;
                }
            }
            final Method __tmnp_isF = isFlashPic;
            final Class __tmp_mBaseBubbleBuilder$ViewHolder = mBaseBubbleBuilder$ViewHolder;
            XposedBridge.hookMethod(m, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!cfg.getBooleanOrFalse(qn_flash_as_pic)) return;
                    Object viewHolder = param.args[1];
                    if (viewHolder == null) return;
                    Object baseChatItemLayout = iget_object_or_null(viewHolder, "a", load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"));
                    boolean isFlashPic = (boolean) XposedBridge.invokeOriginalMethod(__tmnp_isF, null, new Object[]{param.args[0]});
                    XposedHelpers.callMethod(baseChatItemLayout, "setTailMessage", isFlashPic, "闪照", null);
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
        return DexKit.tryLoadOrNull(DexKit.C_FLASH_PIC_HELPER) != null
                && DexKit.tryLoadOrNull(DexKit.C_BASE_PIC_DL_PROC) != null && DexKit.tryLoadOrNull(DexKit.C_ITEM_BUILDER_FAC) != null;
    }
	
	@Override
	public int getEffectiveProc() {
		return SyncUtils.PROC_MAIN;
	}

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_FLASH_PIC_HELPER, DexKit.C_BASE_PIC_DL_PROC, DexKit.C_ITEM_BUILDER_FAC};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefault().getBooleanOrFalse(qn_flash_as_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
	
}
