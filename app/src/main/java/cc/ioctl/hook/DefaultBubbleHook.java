/*
 * QNotified - An Xposed module for QQ/TIM
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
package cc.ioctl.hook;

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;

import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.TOAST_TYPE_ERROR;
import static nil.nadph.qnotified.util.Utils.log;

public class DefaultBubbleHook extends CommonDelayableHook {
    private static final DefaultBubbleHook self = new DefaultBubbleHook();

    private DefaultBubbleHook() {
        super("__NOT_USED__");
    }

    public static DefaultBubbleHook get() {
        return self;
    }

    @Override
    public boolean initOnce() {
        return true;
    }

    @Override
    public boolean isValid() {
        Application app = HostInformationProviderKt.getHostInfo().getApplication();
        return app == null || !HostInformationProviderKt.getHostInfo().isTim();
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            File dir = new File(HostInformationProviderKt.getHostInfo().getApplication().getFilesDir().getAbsolutePath() + "/bubble_info");
            boolean curr = !dir.exists() || !dir.canRead();
            if (dir.exists()) {
                if (enabled && !curr) {
                    dir.setWritable(false);
                    dir.setReadable(false);
                    dir.setExecutable(false);
                }
                if (!enabled && curr) {
                    dir.setWritable(true);
                    dir.setReadable(true);
                    dir.setExecutable(true);
                }
            }
        } catch (final Exception e) {
            Utils.log(e);
            if (Looper.myLooper() == Looper.getMainLooper()) {
                Utils.showToast(HostInformationProviderKt.getHostInfo().getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Utils.showToast(HostInformationProviderKt.getHostInfo().getApplication(), TOAST_TYPE_ERROR, e + "", Toast.LENGTH_SHORT);
                    }
                });
            }
        }
    }

    @Override
    public boolean isEnabled() {
        try {
            Application app = HostInformationProviderKt.getHostInfo().getApplication();
            if (app != null && HostInformationProviderKt.getHostInfo().isTim()) return false;
            File dir = new File(app.getFilesDir().getAbsolutePath() + "/bubble_info");
            return !dir.exists() || !dir.canRead();
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
