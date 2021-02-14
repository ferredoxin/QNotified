/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 xenonhydride@gmail.com
 * https://github.com/ferredoxin/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this software.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.hook;

import android.os.Looper;

import androidx.annotation.NonNull;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.log;

public abstract class CommonDelayableHook extends BaseDelayableHook {
    
    private final String mKeyName;
    private final boolean mDefaultEnabled;
    private final int mTargetProcess;
    private final Step[] mPreconditions;
    private boolean mInited = false;
    
    protected CommonDelayableHook(@NonNull String keyName, @NonNull Step... preconditions) {
        this(keyName, SyncUtils.PROC_MAIN, false, preconditions);
    }
    
    protected CommonDelayableHook(@NonNull String keyName, int targetProcess, @NonNull Step... preconditions) {
        this(keyName, targetProcess, false, preconditions);
    }
    
    protected CommonDelayableHook(@NonNull String keyName, int targetProcess, boolean defEnabled, @NonNull Step... preconditions) {
        mKeyName = keyName;
        mTargetProcess = targetProcess;
        mDefaultEnabled = defEnabled;
        if (preconditions == null) {
            preconditions = new Step[0];
        }
        mPreconditions = preconditions;
    }
    
    @Override
    public final boolean isInited() {
        return mInited;
    }
    
    @Override
    public final boolean init() {
        if (mInited) {
            return true;
        }
        mInited = initOnce();
        return mInited;
    }
    
    protected abstract boolean initOnce();
    
    @Override
    public boolean isEnabled() {
        try {
            return ConfigManager.getDefaultConfig().getBooleanOrDefault(mKeyName, mDefaultEnabled);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(mKeyName, enabled);
            mgr.save();
        } catch (Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(), e + "");
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(), e + "");
                    }
                });
            }
        }
    }
    
    @NonNull
    @Override
    public Step[] getPreconditions() {
        return mPreconditions;
    }
    
    @Override
    public int getEffectiveProc() {
        return mTargetProcess;
    }
}
