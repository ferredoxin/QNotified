/*
 * QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2021 dmca@ioctl.cc
 * https://github.com/ferredoxin/QNotified
 *
 * This software is non-free but opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by ferredoxin.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/ferredoxin/QNotified/blob/master/LICENSE.md>.
 */
package cc.ioctl.hook;

import static nil.nadph.qnotified.util.Utils.log;

import android.app.Application;
import android.os.Looper;
import java.io.File;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.util.Toasts;
import nil.nadph.qnotified.util.Utils;

@FunctionEntry
public class DefaultBubbleHook extends CommonDelayableHook {

    public static final DefaultBubbleHook INSTANCE = new DefaultBubbleHook();

    private DefaultBubbleHook() {
        super("__NOT_USED__");
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
    public boolean isEnabled() {
        try {
            Application app = HostInformationProviderKt.getHostInfo().getApplication();
            if (app != null && HostInformationProviderKt.getHostInfo().isTim()) {
                return false;
            }
            File dir = new File(app.getFilesDir().getAbsolutePath() + "/bubble_info");
            return !dir.exists() || !dir.canRead();
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            File dir = new File(
                HostInformationProviderKt.getHostInfo().getApplication().getFilesDir()
                    .getAbsolutePath() + "/bubble_info");
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
                Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(), e + "");
            } else {
                SyncUtils.post(new Runnable() {
                    @Override
                    public void run() {
                        Toasts.error(HostInformationProviderKt.getHostInfo().getApplication(),
                            e + "");
                    }
                });
            }
        }
    }
}
