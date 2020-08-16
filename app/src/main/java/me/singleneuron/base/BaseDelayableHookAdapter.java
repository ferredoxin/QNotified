package me.singleneuron.base;

import me.singleneuron.hook.NoApplet;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.BaseDelayableHook;
import nil.nadph.qnotified.step.Step;

public abstract class BaseDelayableHookAdapter extends BaseDelayableHook {

    private boolean inited = false;


    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean init() {
        if (inited) return false;
        inited = true;
        return false;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void setEnabled(boolean enabled) {

    }
}
