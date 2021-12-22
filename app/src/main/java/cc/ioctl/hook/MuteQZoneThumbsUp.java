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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Method;
import me.singleneuron.qn_kernel.annotation.UiItem;
import me.singleneuron.qn_kernel.base.CommonDelayAbleHookBridge;
import me.singleneuron.qn_kernel.tlb.UiRoutineKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.LicenseStatus;
import org.ferredoxin.ferredoxinui.common.base.UiSwitchPreference;

@FunctionEntry
@UiItem
public class MuteQZoneThumbsUp extends CommonDelayAbleHookBridge {

    private final UiSwitchPreference mUiSwitchPreference = this.new UiSwitchPreferenceItemFactory("被赞说说不提醒", "不影响评论,转发或击掌的通知");

    @NonNull
    @Override
    public UiSwitchPreference getPreference() {
        return mUiSwitchPreference;
    }

    @Nullable
    @Override
    public String[] getPreferenceLocate() {
        return UiRoutineKt.get净化_扩展();
    }

    public static final MuteQZoneThumbsUp INSTANCE = new MuteQZoneThumbsUp();
    protected int MSG_INFO_OFFSET = -1;

    private MuteQZoneThumbsUp() {
        super(new DexDeobfStep(DexKit.C_QZONE_MSG_NOTIFY));
    }

    @Override
    public boolean initOnce() {
        try {
            Class<?> clz = DexKit.doFindClass(DexKit.C_QZONE_MSG_NOTIFY);
            Method showQZoneMsgNotification = null;
            for (Method m : clz.getDeclaredMethods()) {
                if (m.getReturnType().equals(void.class)) {
                    if (showQZoneMsgNotification == null ||
                        m.getParameterTypes().length > showQZoneMsgNotification
                            .getParameterTypes().length) {
                        showQZoneMsgNotification = m;
                    }
                }
            }
            XposedBridge.hookMethod(showQZoneMsgNotification, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    if (LicenseStatus.sDisableCommonHooks) {
                        return;
                    }
                    if (!isEnabled()) {
                        return;
                    }
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
                    if (desc != null && (desc.endsWith("赞了你的说说") || desc.endsWith("赞了你的分享") || desc
                        .endsWith("赞了你的照片"))) {
                        param.setResult(null);
                    }
                }
            });
            return true;
        } catch (Throwable e) {
            log(e);
            return false;
        }
    }
}
