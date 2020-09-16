/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
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

import android.app.Application;
import android.os.Looper;
import android.widget.Toast;

import java.io.File;

import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.Utils;

import static nil.nadph.qnotified.util.Utils.*;

public class DefaultBubbleHook extends BaseDelayableHook {
    private static final DefaultBubbleHook self = new DefaultBubbleHook();

    private DefaultBubbleHook() {
    }

    public static DefaultBubbleHook get() {
        return self;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[0];
    }

    @Override
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return true;
    }

    @Override
    public boolean isValid() {
        Application app = getApplication();
        return app == null || !isTim(app);
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            File dir = new File(getApplication().getFilesDir().getAbsolutePath() + "/bubble_info");
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
            Application app = getApplication();
            if (app != null && isTim(app)) return false;
            File dir = new File(app.getFilesDir().getAbsolutePath() + "/bubble_info");
            return !dir.exists() || !dir.canRead();
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
