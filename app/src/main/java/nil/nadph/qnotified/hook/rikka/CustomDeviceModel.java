package nil.nadph.qnotified.hook.rikka;

import java.lang.reflect.Field;

import de.robv.android.xposed.XposedHelpers;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.dialog.RikkaCustomDeviceModelDialog;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Initiator;
import nil.nadph.qnotified.util.Utils;

//自定义机型
public class CustomDeviceModel extends BaseDelayableHook {
    private static final CustomDeviceModel self = new CustomDeviceModel();
    private boolean isInit = false;

    public static CustomDeviceModel get() {
        return self;
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_ANY;
    }

    @Override
    public boolean isInited() {
        return isInit;
    }

    @Override
    public boolean init() {
        if (isInit) return true;
        try {
            Class<?> Clz = Initiator.load("android.os.Build");
            Field manufacturer = XposedHelpers.findField(Clz, "MANUFACTURER");
            Field model = XposedHelpers.findField(Clz, "MODEL");
            manufacturer.setAccessible(true);
            model.setAccessible(true);
            manufacturer.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceManufacturer());
            model.set(Clz.newInstance(), RikkaCustomDeviceModelDialog.getCurrentDeviceModel());
            isInit = true;
            return true;
        } catch (Throwable e) {
            Utils.log(e);
            return false;
        }
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isEnabled() {
        return RikkaCustomDeviceModelDialog.IsEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        //not supported.
    }
}
