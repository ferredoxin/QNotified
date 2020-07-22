/* QNotified - An Xposed module for QQ/TIM
 * Copyright (C) 2019-2020 xenonhydride@gmail.com
 * https://github.com/cinit/QNotified
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
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import nil.nadph.qnotified.SyncUtils;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import nil.nadph.qnotified.util.Utils;

import java.lang.reflect.Method;

import static nil.nadph.qnotified.util.Initiator.load;
import static nil.nadph.qnotified.util.Utils.*;

public class SimpleCheckInHook extends BaseDelayableHook {
    public static final String qn_sign_in_as_text = "qn_sign_in_as_text";
    private static final SimpleCheckInHook self = new SimpleCheckInHook();
    private boolean inited = false;

    SimpleCheckInHook() {
    }

    public static SimpleCheckInHook get() {
        return self;
    }

    @Override
    public boolean init() {
        if (inited) return true;
        try {
            Method getMsgType = null;
            for (Method m : DexKit.doFindClass(DexKit.C_ITEM_BUILDER_FAC).getMethods()) {
                if (m.getReturnType().equals(int.class)) {
                    Class[] argt = m.getParameterTypes();
                    if (argt.length > 0 && argt[argt.length - 1].equals(load("com.tencent.mobileqq.data.ChatMessage"))) {
                        getMsgType = m;
                        break;
                    }
                }
            }
            XposedBridge.hookMethod(getMsgType, new XC_MethodHook(39) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) return;
                    try {
                        ConfigManager cfg = ConfigManager.getDefaultConfig();
                        if (!cfg.getBooleanOrFalse(qn_sign_in_as_text)) return;
                    } catch (Exception ignored) {
                    }
                    int result = (int) param.getResult();
                    if (result == 71 || result == 84) {
                        param.setResult(-1);
                    } else if (result == 47) {
                        String json = (String) invoke_virtual(iget_object_or_null(param.args[param.args.length - 1], "ark_app_message"), "toAppXml", new Object[0]);
                        if (json.contains("com.tencent.qq.checkin")) {
                            param.setResult(-1);
                        }
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
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ITEM_BUILDER_FAC)};
    }

    @Override
    public boolean isInited() {
        return inited;
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            ConfigManager mgr = ConfigManager.getDefaultConfig();
            mgr.getAllConfig().put(qn_sign_in_as_text, enabled);
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
            return ConfigManager.getDefaultConfig().getBooleanOrFalse(qn_sign_in_as_text);
        } catch (Exception e) {
            log(e);
            return false;
        }
    }
}
