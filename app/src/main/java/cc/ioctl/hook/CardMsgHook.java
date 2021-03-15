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

import android.os.Parcelable;
import mqq.app.AppRuntime;
import nil.nadph.qnotified.base.annotation.FunctionEntry;
import nil.nadph.qnotified.hook.CommonDelayableHook;
import nil.nadph.qnotified.step.DexDeobfStep;
import nil.nadph.qnotified.step.Step;
import nil.nadph.qnotified.util.DexKit;

@FunctionEntry
public class CardMsgHook extends CommonDelayableHook {

    public static final int R_ID_COPY_CODE = 0x00EE77CC;
    public static final CardMsgHook INSTANCE = new CardMsgHook();

    private CardMsgHook() {
        super("qn_send_card_msg");
    }

    @SuppressWarnings("JavaJniMissingFunction")
    static native boolean ntSendCardMsg(AppRuntime rt, Parcelable session, String msg)
        throws Exception;

    @Override
    public boolean initOnce() {
        return true;
    }

    @Override
    public Step[] getPreconditions() {
        return new Step[]{new DexDeobfStep(DexKit.C_ARK_APP_ITEM_BUBBLE_BUILDER),
            new DexDeobfStep(DexKit.C_FACADE),
            new DexDeobfStep(DexKit.C_TEST_STRUCT_MSG),
            new DexDeobfStep(DexKit.N_BASE_CHAT_PIE__INIT)};
    }
}
