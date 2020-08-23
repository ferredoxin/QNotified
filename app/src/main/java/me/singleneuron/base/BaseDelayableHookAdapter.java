package me.singleneuron.base;

import android.os.Looper;
import android.widget.Toast;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.getApplication;
import static nil.nadph.qnotified.util.Utils.log;

public abstract class BaseDelayableHookAdapter extends BaseDelayableHook {

    private boolean inited = false;

    private final int proc;
    protected final String cfgName;
    private final Step[] cond;
    private final boolean defVal;
    protected boolean recordTime = false;

    protected BaseDelayableHookAdapter(String cfgName) {
        this(cfgName, SyncUtils.PROC_MAIN);
    }

    protected BaseDelayableHookAdapter(String cfgName, int proc) {
        this(cfgName, proc, new Step[0], false);
    }

    protected BaseDelayableHookAdapter(String cfgName, int proc, Step[] cond, boolean defVal) {
        this.cfgName = cfgName;
        this.cond = cond;
        this.proc = proc;
        this.defVal = defVal;
    }

    @Override
    public int getEffectiveProc() {
        return proc;
    }

    @Override
    public boolean init() {
        if (!checkEnabled()) return false;
        if (inited) return true;
        try {
            inited = doInit();
        } catch (Exception e) {
            Utils.log(e);
            inited = false;
        }
        return inited;
    }

    protected abstract boolean doInit();

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public Step[] getPreconditions() {
        return cond;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(cfgName, enabled);
            mgr.save();
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(() -> Utils.showToast(getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT));
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrDefault(cfgName, defVal);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    public boolean checkEnabled() {
        if (LicenseStatus.sDisableCommonHooks) return false;
        if (!isEnabled()) return false;
        return true;
    }

    public abstract class XposedMethodHookAdapter extends XC_MethodHook {

        @Override
        final protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            long startTime = 0;
            if (recordTime) {
                startTime = System.currentTimeMillis();
            }
            if (!checkEnabled()) return;
            try {
                beforeMethod(param);
            } catch (Exception e) {
                Utils.log(e);
            }
            if (recordTime) {
                Utils.logd(cfgName+" costs time: "+(System.currentTimeMillis()-startTime)+" ms");
            }
        }

        @Override
        final protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            long startTime = 0;
            if (recordTime) {
                startTime = System.currentTimeMillis();
            }
            if (!checkEnabled()) return;
            try {
                afterMethod(param);
            } catch (Exception e) {
                Utils.log(e);
            }
            if (recordTime) {
                Utils.logd(cfgName+" costs time: "+(System.currentTimeMillis()-startTime)+" ms");
            }
        }

        protected void beforeMethod(XC_MethodHook.MethodHookParam param) throws Throwable { }
        protected void afterMethod(XC_MethodHook.MethodHookParam param) throws Throwable { }

    }

    public abstract class XposedMethodReplacementAdapter extends XC_MethodReplacement {

        @Override
        final protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
            long startTime = 0;
            Object object;
            if (recordTime) {
                startTime = System.currentTimeMillis();
            }
            if (!checkEnabled()) {
                object = XposedBridge.invokeOriginalMethod(methodHookParam.method,methodHookParam.thisObject,methodHookParam.args);
            } else {
                object = replaceMethod(methodHookParam);
            }
            if (recordTime) {
                Utils.logd(cfgName+" costs time: "+(System.currentTimeMillis()-startTime)+" ms");
            }
            return object;
        }

        abstract protected Object replaceMethod(MethodHookParam param) throws Throwable;

    }

}
