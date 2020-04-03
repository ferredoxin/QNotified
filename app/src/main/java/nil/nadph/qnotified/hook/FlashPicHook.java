package nil.nadph.qnotified.hook;

import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static nil.nadph.qnotified.util.Initiator._MessageRecord;
import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class FlashPicHook extends BaseDelayableHook {
    public static final String qn_flash_as_pic = "qn_flash_as_pic";
    private static final FlashPicHook self = new FlashPicHook();
    private boolean inited = false;

    private FlashPicHook() {
    }

    public static FlashPicHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            final ConfigManager cfg = ConfigManager.getDefaultConfig();
            boolean canInit = checkPreconditions();
            if (!canInit && ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic)) {
                if (Looper.myLooper() != null) {
                    showToast(getApplication(), TOAST_TYPE_ERROR, "QNotified:闪照功能初始化错误", Toast.LENGTH_LONG);
                }
            }
            if (!canInit) return false;
            Class clz = DexKit.tryLoadOrNull(DexKit.C_FLASH_PIC_HELPER);
            Method isFlashPic = null;
            for (Method mi : clz.getDeclaredMethods()) {
                if (mi.getReturnType().equals(boolean.class) && mi.getParameterTypes().length == 1) {
                    String name = mi.getName();
                    if (name.equals("a") || name.equals("z")) {
                        isFlashPic = mi;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(isFlashPic, new XC_MethodHook(52) {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    try {
                        if (!ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic)) return;
                    } catch (Exception e) {
                        log(e);
                    }
                    String sn_ItemBuilderFactory = getShort$Name(DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC));
                    String sn_BasePicDownloadProcessor = getShort$Name(DexKit.doFindClass(DexKit.C_BASE_PIC_DL_PROC));
                    if (isCallingFromEither(sn_ItemBuilderFactory, sn_BasePicDownloadProcessor, "FlashPicItemBuilder")) {
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
            final Class<?> __tmp_mBaseBubbleBuilder$ViewHolder = mBaseBubbleBuilder$ViewHolder;
            XposedBridge.hookMethod(m, new XC_MethodHook() {
                private Field fBaseChatItemLayout = null;

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (!cfg.getBooleanOrFalse(qn_flash_as_pic)) return;
                    Object viewHolder = param.args[1];
                    if (viewHolder == null) return;
                    if (fBaseChatItemLayout == null) {
                        fBaseChatItemLayout = Utils.findField(viewHolder.getClass(), load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"), "a");
                        if (fBaseChatItemLayout == null) {
                            fBaseChatItemLayout = Utils.getFirstNSFFieldByType(viewHolder.getClass(), load("com.tencent.mobileqq.activity.aio.BaseChatItemLayout"));
                        }
                        fBaseChatItemLayout.setAccessible(true);
                    }
                    Object baseChatItemLayout = fBaseChatItemLayout.get(viewHolder);
                    if (isFlashPic(param.args[0])) {
                        XposedHelpers.callMethod(baseChatItemLayout, "setTailMessage", true, "闪照", null);
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

    private static Field MsgRecord_msgtype = null;
    private static Method MsgRecord_getExtInfoFromExtStr = null;

    public static boolean isFlashPic(Object msgRecord) {
        try {
            if (MsgRecord_msgtype == null) {
                MsgRecord_msgtype = _MessageRecord().getField("msgtype");
                MsgRecord_msgtype.setAccessible(true);
            }
            if (MsgRecord_getExtInfoFromExtStr == null) {
                MsgRecord_getExtInfoFromExtStr = _MessageRecord().getMethod("getExtInfoFromExtStr", String.class);
                MsgRecord_getExtInfoFromExtStr.setAccessible(true);
            }
            int msgtype = (int) MsgRecord_msgtype.get(msgRecord);
            return (msgtype == -2000 || msgtype == -2006)
                    && !TextUtils.isEmpty((String) MsgRecord_getExtInfoFromExtStr.invoke(msgRecord, "commen_flash_pic"));
        } catch (Exception e) {
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
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_flash_as_pic, enabled);
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

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_flash_as_pic);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

}
