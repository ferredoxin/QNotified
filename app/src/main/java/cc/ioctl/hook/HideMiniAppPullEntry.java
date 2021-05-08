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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashSet;

import cc.ioctl.H;
import de.robv.android.xposed.XC_MethodReplacement;
import me.singleneuron.qn_kernel.data.HostInformationProviderKt;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.config.ConfigItems;
import nil.nadph.qnotified.config.ConfigManager;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexFlow;
import nil.nadph.qnotified.util.DexKit;
import nil.nadph.qnotified.util.DexMethodDescriptor;
import nil.nadph.qnotified.util.Initiator;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static nil.nadph.qnotified.util.Utils.log;
import static nil.nadph.qnotified.util.Utils.loge;

@FunctionEntry
public class HideMiniAppPullEntry extends CommonDelayableHook implements Step {

    public static final HideMiniAppPullEntry INSTANCE = new HideMiniAppPullEntry();

    protected HideMiniAppPullEntry() {
        super(ConfigItems.qn_hide_msg_list_miniapp);
    }

    @Override
    protected boolean initOnce() {
        try {
            if (HostInformationProviderKt.isTim()) {
                return false;
            }
            if (isEnabled()) {
                String methodName = getInitMiniAppObfsName();
                if (methodName == null) {
                    loge("getInitMiniAppObfsName() == null");
                    return false;
                }
                findAndHookMethod(Initiator._Conversation(), methodName,
                    XC_MethodReplacement.returnConstant(null));
            }
            return true;
        } catch (Exception e) {
            log(e);
        }
        return false;
    }

    /**
     * Fast fail
     */
    @Nullable
    private String getInitMiniAppObfsName() {
        ConfigManager cache = ConfigManager.getCache();
        int lastVersion = cache.getIntOrDefault("qn_hide_miniapp_v2_version_code", 0);
        String methodName = cache.getString("qn_hide_miniapp_v2_method_name");
        if (H.getVersionCode() == lastVersion) {
            return methodName;
        }
        return null;
    }

    @NonNull
    @Override
    public Step[] getPreconditions() {
        return new Step[]{this};
    }

    @Override
    public boolean step() {
        if (getInitMiniAppObfsName() != null) {
            return true;
        }
        try {
            Class<?> clz = Initiator._Conversation();
            if (clz == null) {
                return false;
            }
            String smaliConversation = DexMethodDescriptor.getTypeSig(clz);
            byte[] dex = DexKit.getClassDeclaringDex(smaliConversation, null);
            if (dex == null) {
                loge("Error getClassDeclaringDex Conversation.class");
                return false;
            }
            for (byte[] key : new byte[][]{
                DexFlow.packUtf8("initMiniAppEntryLayout."),
                DexFlow.packUtf8("initMicroAppEntryLayout."),
                DexFlow.packUtf8("init Mini App, cost=")
            }) {
                HashSet<DexMethodDescriptor> rets = new HashSet<>();
                ArrayList<Integer> opcodeOffsets = DexKit
                    .a(dex, key);
                for (int j = 0; j < opcodeOffsets.size(); j++) {
                    try {
                        DexMethodDescriptor desc = DexFlow
                            .getDexMethodByOpOffset(dex, opcodeOffsets.get(j), true);
                        if (desc != null) {
                            rets.add(desc);
                        }
                    } catch (InternalError ignored) {
                    }
                }
                for (DexMethodDescriptor desc : rets) {
                    if (smaliConversation.equals(desc.declaringClass)
                        && "()V".equals(desc.signature)) {
                        // save and return
                        ConfigManager cache = ConfigManager.getCache();
                        cache.putInt("qn_hide_miniapp_v2_version_code",
                            H.getVersionCode());
                        cache.putString("qn_hide_miniapp_v2_method_name", desc.name);
                        cache.save();
                        return true;
                    }
                }
            }
            loge("No Conversation.?() func found");
            return false;
        } catch (Exception e) {
            log(e);
            return false;
        }
    }

    @Override
    public boolean isDone() {
        return getInitMiniAppObfsName() != null;
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Nullable
    @Override
    public String getDescription() {
        return "生成屏蔽下拉小程序解决方案";
    }
}
