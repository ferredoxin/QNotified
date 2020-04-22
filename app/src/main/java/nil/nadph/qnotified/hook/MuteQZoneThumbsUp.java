/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 cinit@github.com
 * https://github.com/cinit/QNotified
 *
 * This software is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.  If not, see
 * <https://www.gnu.org/licenses/>.
 */
package nil.nadph.qnotified.hook;

import android.os.Looper;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Utils.*;

public class MuteQZoneThumbsUp extends BaseDelayableHook {

    public static final String qn_mute_thumb_up = "qn_mute_thumb_up";
    private static final MuteQZoneThumbsUp self = new MuteQZoneThumbsUp();
    private boolean inited = false;

    private MuteQZoneThumbsUp() {
    }

    public static MuteQZoneThumbsUp get() {
        return self;
    }

    private int MSG_INFO_OFFSET = -1;

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Class clz = DexKit.doFindClass(DexKit.C_QZONE_MSG_NOTIFY);
            Method showQZoneMsgNotification = null;
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class)) {
                    if (showQZoneMsgNotification == null ||
                            m.getParameterTypes().length > showQZoneMsgNotification.getParameterTypes().length) {
                        showQZoneMsgNotification = m;
                    }
                }
            }
            XposedBridge.hookMethod(showQZoneMsgNotification, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (!isEnabled()) return;
                    if (MSG_INFO_OFFSET < 0) {
                        Class<?>[] argt = ((Method) param.method).getParameterTypes();
                        int hit = 0;
                        for (int i = 0; i < argt.length; i++) {
                            if (argt[i].equals(String.class)) {
                                if (hit == 1) {
                                    MSG_INFO_OFFSET = i;
                                    break;
                                } else {
                                    hit++;
                                }
                            }
                        }
                    }
                    String desc = (String) param.args[MSG_INFO_OFFSET];
                    if (desc != null && desc.endsWith("赞了你的说说")) {
                        param.setResult(null);
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
    public int getEffectiveProc() {
        return SyncUtils.PROC_MAIN;
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public int[] getPreconditions() {
        return new int[]{DexKit.C_QZONE_MSG_NOTIFY};
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_mute_thumb_up, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_mute_thumb_up);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
